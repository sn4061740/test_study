package com.xcore.cache;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.dolit.p2ptrans.P2PTrans;

public class LocalDownLoader {
    private DBHandler dbHandler;
    public LocalDownLoader(DBHandler dbHandler){
        this.dbHandler=dbHandler;
    }

    public Map<String,CacheModel> dataMaps=new Hashtable<>();
    public Map<String,CacheModel> rumMaps=new Hashtable<>();
    public Map<String,CacheModel> overMaps=new Hashtable<>();

    private Timer timer;
    private boolean isStopOption=false;//是否正在执行停止操作
    private int dbTimerNumber=0;//保存的时长 50 秒保存一次 dbTimerNumber%50==0

    //开始下载
    public void down(final DownModel downModel){
        String roomPath=MainApplicationContext.SD_PATH;
        String path=downModel.getUrl();
        String name ="";
        try {
            int lastIndex = path.lastIndexOf("/");
            name = downModel.getUrl().substring(lastIndex + 1);

            String pStr = MainApplicationContext.SD_PATH + name;
            File file1 = new File(pStr);
            if (file1.exists()) {
                String downPath = file1.getAbsolutePath();
                startDown(downPath, downModel);
                return;
            }
        }catch (Exception ex){}
        if(isEmpty(name)){
            LogUtils.showTips("缓存失败,请稍后重试!!!", R.color.color_1296db);
            return;
        }
        OkGo.<File>get(downModel.getUrl()).execute(new FileCallback(roomPath,name) {
            @Override
            public void onSuccess(Response<File> response) {
                File file=response.body();
                String downPath=file.getAbsolutePath();
                startDown(downPath,downModel);//575f1b03225f8d8b9e3f06d535197487
            }
            @Override
            public void onError(Response<File> response) {
                try{
                    LogUtils.showTips("缓存失败,请稍后重试!!!", R.color.color_1296db);
                }catch (Exception ex){}
                try {
                    int code = response.code();
                    String error = LogUtils.getException(response.getException());
                    LogUtils.showLog(error);
                    LogUtils.apiRequestError(downModel.getUrl(), error, 0, code);
                }catch (Exception ex){}
            }
        });
    }
    private void startDown(final String path, final DownModel downModel){
        String vPath=P2PTrans.getUrlAddPass(path);
        vPath=P2PTrans.getLocalTorrentPath(vPath);
        OkGo.<String>get(vPath).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
            String v=response.body();
            try{
                LogUtils.showLog(v);
                JsonObject jsonObject= new Gson().fromJson(v, JsonObject.class);
                JsonObject dataObj= jsonObject.getAsJsonObject("data");
                final String id=dataObj.get("id").getAsString();
                if(TextUtils.isEmpty(id)) {
                    return;
                }
                CacheModel cacheModel=new CacheModel();
                cacheModel.setStreamId(id);
                cacheModel.setShortId(downModel.getShortId());
                cacheModel.setName(downModel.getName());
                cacheModel.setUrl(downModel.getUrl());
                cacheModel.setConver(downModel.getConver());

                addDataMap(cacheModel);
                addRunMap(cacheModel);

                isStopOption=false;
                if(timer==null){
                    timer= new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getDownInfo();
                        }
                    },1000,2500);
                }
            }catch (Exception ex){}
            }
        });
    }
    //下载信息
    private void getDownInfo(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                if(isStopOption==true){//正在做停止任务的操作,不要继续往下了
                    return;
                }
                P2PTrans.StreamsResult result = P2PTrans.getStreams(BaseCommon.P2P_SERVER_PORT);
                if (result == null) {
                    return;
                }
                List<P2PTrans.StreamInfo> infos = result.getStreams();
                if (infos == null | infos.size() <= 0) {
                    return;
                }
                dbTimerNumber++;
                for (P2PTrans.StreamInfo info : infos) {
                    if(info==null||isStopOption==true){
                        continue;
                    }
//                    LogUtils.showLog("速度:"+info.getDownloadSpeed()+" 当前下载:"+info.getDownloadedBytes());
                    updateDataMap(info);
                    updateRunMap(info);
                }
                boolean b=checkRun();
                if(!b){
                    stopAll();
                }
            }catch (Exception ex){}
            }
        });
    }
    //停止任务时间器
    private void stopTaskTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// 初始数据 //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void init(){
        cacheSpeedLogBoo=MainApplicationContext.cacheSpeedLog;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                String playCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.PLAY_IS_CACHE);
                if(!TextUtils.isEmpty(playCacheStr)){
                    MainApplicationContext.IS_PLAYING_TO_CACHE=true;
                }
                String idleCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.IDLE_IS_CACHE);
                if(!TextUtils.isEmpty(idleCacheStr)){
                    MainApplicationContext.IS_IDLE_CACHE=true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                //初始化用户设置的清晰度
                String qPos = CacheManager.getInstance().getLocalHandler().get("quality_info");
                if (qPos != null && qPos.length() > 0) {
                    MainApplicationContext.CURRENT_QUALITY = qPos;
                }
            }catch (Exception ex){}
            try {//初始化是否打开硬件解码
                String openMediaStr = CacheManager.getInstance().getLocalHandler().get("media_open");
                if(!TextUtils.isEmpty(openMediaStr)) {//设置过了
                    if (openMediaStr.equals("0")) {//不开启
                        MainApplicationContext.mediaCodecEnabled = false;
                    } else {
                        MainApplicationContext.mediaCodecEnabled = true;
                    }
                }
            }catch (Exception ex){}
            try{
                //初始化用户设置的下载配置 仅wifi
                String openMediaStr = CacheManager.getInstance().getLocalHandler().get("only_wifi");
                if(!TextUtils.isEmpty(openMediaStr)) {
                    if (openMediaStr.equals("0")){//设置了不开启
                        MainApplicationContext.onlyWifiDownBoo = false;
                    } else{
                        MainApplicationContext.onlyWifiDownBoo = true;
                    }
                }
            }catch (Exception ex){}
            //初始化数据
            List<CacheModel> cacheModels=dbHandler.getDowns();
            if(cacheModels!=null&&cacheModels.size()>0) {
                for (CacheModel cacheModel : cacheModels) {
                    cacheModel.setComplete(0);
                    cacheModel.setChecked(false);
                    cacheModel.setStatus(2);
                    cacheModel.setShowCheck(false);
                    int percent = Integer.valueOf(cacheModel.getPercent());

                    //判断文件存不存在,如果不存在就把下载信息设置为0
                    String sPath = MainApplicationContext.SD_PATH;
                    String sName = cacheModel.getStreamId();//得到文件夹名
                    String sUrl = cacheModel.getUrl();//得到种子名
                    int sIndex = sUrl.lastIndexOf("/");
                    if (sIndex > -1) {
                        try {
                            sUrl = sUrl.substring(sIndex + 1);
                            sUrl = sUrl.replace(".Torrent", ".xv");
                            sUrl = sUrl.replace(".torrent", ".xv");
                            String fileName = sPath + sName + "/" + sUrl;
                            File f = new File(fileName);
                            if (!f.exists()) {
                                cacheModel.setComplete(0);
                                cacheModel.setPercent("0");
                                cacheModel.setTotalCount1(0);
                                cacheModel.setTotalSize("0");
                                cacheModel.setDownSize("0");
                            }
                        } catch (Exception ex) {
                        }
                    }
                    if (percent == 100) {
                        cacheModel.setComplete(1);
                        if (sIndex > 0) {
                            String fileName = sPath + sName + "/" + sUrl;
                            File f = new File(fileName);
                            if (f.exists()) {
                                addOverMap(cacheModel);
                            } else {//不存在
                                cacheModel.setComplete(0);
                                cacheModel.setPercent("0");
                                cacheModel.setDownSize("0");
                                cacheModel.setTotalSize("0");
                                addRunMap(cacheModel);
                            }
                        } else {
                            addOverMap(cacheModel);
                        }
                    } else {
                        addRunMap(cacheModel);
                    }
                    addDataMap(cacheModel);
                }
            }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// 添加数据 //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //添加到全部数据中
    private void addDataMap(CacheModel c1){
        final CacheModel c=c1;
        if(!dataMaps.containsKey(c.getShortId())){
            CacheModel cacheModel=new CacheModel();
            cacheModel.setShortId(c.getShortId());
            cacheModel.setStreamId(c.getStreamId());
            cacheModel.setConver(c.getConver());
            cacheModel.setUrl(c.getUrl());
            cacheModel.setName(c.getName());
            cacheModel.setTotalSize(c.getTotalSize());
            cacheModel.setDownSize(c.getDownSize());
            cacheModel.setPercent(c.getPercent());
            dataMaps.put(cacheModel.getShortId(),cacheModel);
        }else{
            CacheModel cacheModel= rumMaps.get(c.getShortId());
            if(cacheModel!=null){
                cacheModel.setStop(false);
            }
        }
    }
    private void updateDataMap(P2PTrans.StreamInfo info1){
        final P2PTrans.StreamInfo info=info1;
        try{
            CacheModel c=getModelByDataStreamId(info.getId());
            if(c==null){
                return;
            }
            if(info.getPercent()>=100){
                c.setComplete(1);
                c.setPercent("100");
                addOverMap(c);
            }else{
                if(c.isStop()) {
                    c.setStatus(2);
                }else{
                    c.setStatus(1);
                }
                c.setStop(false);
                c.setPercent(info.getPercent()+"");
                c.setDownSize(info.getDownloadedBytes()+"");
                c.setTotalSize(info.getTotalBytes()+"");
                c.setStreamInfo1(info);
            }
        }catch (Exception ex){}
    }
    private CacheModel getModelByDataStreamId(String sId){
        CacheModel cacheModel=null;
        try {
            for (CacheModel model : dataMaps.values()) {
                if (model != null && model.getStreamId().equals(sId)) {
                    cacheModel=model;
                    break;
                }
            }
        }catch (Exception ex){}
        return  cacheModel;
    }

    public List<CacheModel> getDataList(){
        List<CacheModel> cList=new ArrayList<>();
        for(CacheModel c:dataMaps.values()){
            cList.add(c);
        }
        return cList;
    }
    //查看datamaps 中是否包含
    private boolean getExist(String sname){
        boolean boo=false;
        try {
            for (CacheModel model : dataMaps.values()) {
                if (model != null && model.getUrl().contains(sname)) {
                    boo=true;
                    break;
                }
            }
        }catch (Exception ex){}
        return  boo;
    }
    //根据shortId 获取信息
    public CacheModel getDataByShortId(String shortId){
        if(isEmpty(shortId)){
            return null;
        }
        return dataMaps.get(shortId);
    }

    //添加到运行的数据中
    private void addRunMap(CacheModel c1){
        final CacheModel c=c1;
        if(!rumMaps.containsKey(c.getShortId())){
            CacheModel cacheModel=new CacheModel();
            cacheModel.setShortId(c.getShortId());
            cacheModel.setStreamId(c.getStreamId());
            cacheModel.setConver(c.getConver());
            cacheModel.setUrl(c.getUrl());
            cacheModel.setName(c.getName());
            cacheModel.setComplete(c.getComplete());
            cacheModel.setStatus(c.getStatus());
            cacheModel.setPercent(c.getPercent());
            cacheModel.setDownSize(c.getDownSize());
            cacheModel.setTotalSize(c.getTotalSize());
//                    cacheModel.setTotalCount1(c.getTotalCount1());
            cacheModel.setStop(false);
            rumMaps.put(cacheModel.getShortId(),cacheModel);
        }else{
            CacheModel cacheModel= rumMaps.get(c.getShortId());
            if(cacheModel!=null){
                cacheModel.setStop(false);
            }
        }
    }
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    Map<String,CacheInfoModel> cacheInfoModelMap=new Hashtable<>();

    private void updateRunMap(P2PTrans.StreamInfo info1){
        final P2PTrans.StreamInfo info=info1;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try{//TODO 这里的平均速度这些要分开放到一个map 里面来对应计算才对
                CacheModel c=getModelByRunStreamId(info.getId());
                if(c==null){
                    return;
                }
                if(info.getPercent()>=100){//停止任务
                    CacheModel cacheModel= rumMaps.remove(c.getShortId());
                    CacheModel c1=new CacheModel();
                    c1.setStreamId(cacheModel.getStreamId());
                    c1.setShortId(cacheModel.getShortId());
                    c1.setUrl(cacheModel.getUrl());
                    stopTask(Arrays.asList(c1));
                }else{
                    if(c.isStop()) {
                        c.setStatus(2);
                    }else{
                        c.setStatus(1);
                    }
                    Integer percent=Integer.valueOf(c.getPercent());
                    if(percent==null){
                        c.setPercent(info.getPercent()+"");
                    }else{
                        if(info.getPercent()>percent){
                            c.setPercent(info.getPercent()+"");
                        }
                    }
                    Integer downSize=Integer.valueOf(c.getDownSize());
                    if(downSize==null){
                        c.setDownSize(info.getDownloadedBytes()+"");
                    }else{
                        if(info.getDownloadedBytes()>downSize){
                            c.setDownSize(info.getDownloadedBytes()+"");
                        }
                    }
                    Integer totalSize=Integer.valueOf(c.getTotalSize());
                    if(totalSize==null){
                        c.setTotalSize(info.getTotalBytes()+"");
                    }else{
                        if(info.getTotalBytes()>totalSize){
                            c.setTotalSize(info.getTotalBytes()+"");
                        }
                    }
                    c.setStreamInfo1(info);

                    updateDownSpeed(c,false);
                }
            }catch (Exception ex){}
            }
        });
    }
    //更新速度
    private void updateDownSpeed(CacheModel c1,boolean isSave1){
        final CacheModel c=c1;
        final boolean isSave=isSave1;
        if(c==null){
            return;
        }
        final P2PTrans.StreamInfo info=c.getStreamInfo1();
        if(info==null){
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            //判断时间
            if((dbTimerNumber==5||dbTimerNumber%60==0)&&c.isStop()==false||isSave==true){
                String sId=c.getShortId();
                CacheInfoModel cacheInfoModel=null;
                if(cacheInfoModelMap.containsKey(sId)){
                    cacheInfoModel=cacheInfoModelMap.get(sId);
                }else{
                    cacheInfoModel=new CacheInfoModel();
                    cacheInfoModelMap.put(sId,cacheInfoModel);
                }

                cacheInfoModel.time=dbTimerNumber;
                cacheInfoModel.totalSpeed+=info.getDownloadSpeed();
                cacheInfoModel.httpTotalSpeed+=info.getServerTotalSpeed();
                cacheInfoModel.totalDownSize=info.getDownloadedBytes();
                cacheInfoModel.httpTotalDownSize=info.getTotalServerBytes();
                cacheInfoModel.totalBytesDown=info.getTotalBytesDown();
                cacheInfoModel.totalSize=Long.valueOf(c.getTotalSize());
                cacheInfoModel.perent= Integer.valueOf(c.getPercent());
                cacheInfoModel.shortId=c.getShortId();

                //添加到数据库中
                CacheModel c1=new CacheModel();
                updateMap(c,c1);
                try {
                    dbHandler.insertDown(c1);
                }catch (Exception ex){}
                sendSpeedToServer(cacheInfoModel);
            }
            }
        });
    }

    boolean cacheSpeedLogBoo=true;
    //发送速度到服务器
    private void sendSpeedToServer(CacheInfoModel infoModel){
        if (cacheSpeedLogBoo == false) {
            return;
        }
        try {
            CacheInfoModel cacheInfoModel = new CacheInfoModel();
            cacheInfoModel.time = dbTimerNumber;
            cacheInfoModel.totalSpeed = infoModel.totalSpeed;
            cacheInfoModel.httpTotalSpeed = infoModel.httpTotalSpeed;
            cacheInfoModel.totalDownSize = infoModel.totalDownSize;
            cacheInfoModel.httpTotalDownSize = infoModel.httpTotalDownSize;
            cacheInfoModel.totalBytesDown = infoModel.totalBytesDown;
            cacheInfoModel.totalSize = infoModel.totalSize;
            cacheInfoModel.perent = infoModel.perent;
            cacheInfoModel.shortId = infoModel.shortId;

            LogUtils.cacheSpeedUp(cacheInfoModel);
        }catch (Exception ex){}
    }

    private CacheModel getModelByRunStreamId(String sId){
        CacheModel cacheModel=null;
        try {
            for (CacheModel model : rumMaps.values()) {
                if (model != null && model.getStreamId().equals(sId)) {
                    cacheModel=model;
                    break;
                }
            }
        }catch (Exception ex){}
        return  cacheModel;
    }
    public CacheModel getRunByShortId(String shortId){
        if(isEmpty(shortId)){
            return null;
        }
        return rumMaps.get(shortId);
    }

    //添加到完成的数据中
    private void addOverMap(final CacheModel c){
        if(!overMaps.containsKey(c.getShortId())){
            CacheModel cacheModel=new CacheModel();
            cacheModel.setShortId(c.getShortId());
            cacheModel.setStreamId(c.getStreamId());
            cacheModel.setConver(c.getConver());
            cacheModel.setUrl(c.getUrl());
            cacheModel.setName(c.getName());
            cacheModel.setComplete(1);
            cacheModel.setPercent("100");
            cacheModel.setDownSize(c.getDownSize());
            cacheModel.setTotalSize(c.getTotalSize());
            cacheModel.settDelete("0");
            cacheModel.setStreamInfo1(c.getStreamInfo1());
            overMaps.put(cacheModel.getShortId(),cacheModel);

            //添加到数据库中
//            CacheModel c2=new CacheModel();
//            updateMap(cacheModel,c2);
//            dbHandler.insertDown(c2);
            updateDownSpeed(cacheModel,true);
        }
    }
    public CacheModel getOverByShortId(String shortId){
        if(isEmpty(shortId)){
            return null;
        }
        return overMaps.get(shortId);
    }

    //更新数据
    private void updateMap(CacheModel source,CacheModel taget){
        taget.setStatus(source.getStatus());
        taget.setComplete(source.getComplete());
        taget.setStreamInfo(source.getStreamInfo());
        taget.setStreamInfo1(source.getStreamInfo1());
        taget.setTotalSize(source.getTotalSize());
        taget.setTotalCount1(source.getTotalCount1());
        taget.setStreamId(source.getStreamId());
        taget.setDownSize(source.getDownSize());
        taget.setPercent(source.getPercent());
        taget.setChecked(source.isChecked());
        taget.setStop(source.isStop());
        taget.setShowCheck(source.isShowCheck());
        taget.setTimeCount(source.getTimeCount());
        taget.setUpdateTime(source.getUpdateTime());
        taget.setPlay(source.isPlay());
        taget.settDelete(source.gettDelete());
        taget.setUrl(source.getUrl());
        taget.setName(source.getName());
        taget.setShortId(source.getShortId());
        taget.setConver(source.getConver());
    }

    /**
     * 检查是否还有运行中的任务
     * @return true:有正在运行的任务   false:没有运行的任务了
     */
    private boolean checkRun(){
        if(isStopOption==true){//正在做停止任务的操作,不要继续往下了
            return true;
        }
        boolean b=false;
        for(CacheModel c:rumMaps.values()){
            if(c==null){
                continue;
            }
            if(c.isStop()==false){
                b=true;
            }
        }
        return b;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// 开始、停止任务 //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //开始所有任务
    public void startAll(){
        for(CacheModel cacheModel:rumMaps.values()){
            if (cacheModel == null) {
                continue;
            }
            DownModel downModel=new DownModel();
            downModel.setUrl(cacheModel.getUrl());
            downModel.setName(cacheModel.getName());
            downModel.setConver(cacheModel.getConver());
            downModel.setShortId(cacheModel.getShortId());
            down(downModel);
        }
    }

    //停止任务
    public void stopTask(List<CacheModel> cacheModels){
        isStopOption=true;
        try {
            for (CacheModel c : cacheModels) {
                if (c == null) {
                    continue;
                }
                CacheModel runModel = rumMaps.get(c.getShortId());
                if (runModel != null) {
                    runModel.setStatus(2);
                    runModel.setStop(true);
                }
                CacheModel dataModel = dataMaps.get(c.getShortId());
                if (dataModel != null) {
                    dataModel.setStatus(2);
                    dataModel.setStop(true);
                }
            }
            boolean b = checkRun();
            if (b == false) {//没有运行中的任务了,停止计时器,停止所有任务
                stopAll();
            }else {
                for(CacheModel cMod:cacheModels){
                    //更新
                    CacheModel v=new CacheModel();
                    updateMap(cMod,v);
                    updateDownSpeed(v,true);
                }
                for (CacheModel c : cacheModels) {
                    if(c==null){
                        continue;
                    }
                    stop(c);
                }
            }
        }catch (Exception ex){
            //ex.printStackTrace();
        }
        finally {
            isStopOption = false;
        }
    }

    /**
     * 停止所有
     */
    private void stopAll(){
        isStopOption=true;
        stopTaskTimer();
        try {
            String result = P2PTrans.stopAllStream(BaseCommon.P2P_SERVER_PORT);
            if(!isEmpty(result)) {
                LogUtils.showLog("停止所有任务:" + result);
            }else{
                LogUtils.showLog("没有得到停止所有任务信息");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try {
            //更新数据库
            for (CacheModel c : rumMaps.values()) {
                CacheModel v = new CacheModel();
                updateMap(c, v);
                dbHandler.update(v);
            }
            for(CacheModel cMod:rumMaps.values()){
                updateDownSpeed(cMod, true);
            }
        }catch (Exception ex){}
        finally {
            isStopOption=false;
        }
    }
    public void stopRunAll(){
        if(timer==null){
            return;
        }
        isStopOption=true;
        try {
            for (CacheModel c : rumMaps.values()) {
                if (c == null) {
                    continue;
                }
                CacheModel runModel = rumMaps.get(c.getShortId());
                if (runModel != null) {
                    runModel.setStatus(2);
                    runModel.setStop(true);
                }
                CacheModel dataModel = dataMaps.get(c.getShortId());
                if (dataModel != null) {
                    dataModel.setStatus(2);
                    dataModel.setStop(true);
                }
            }
            isStopOption = false;
            stopAll();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 停止单个任务,最好不要从这里调用,先调用 stopTask 停止任务
     * @param c
     */
    private void stop(final CacheModel c){
        final String streamId=c.getStreamId();
        final String url=c.getUrl();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = "";
                    if (isEmpty(c.getStreamId())) {
                        result = P2PTrans.stopByUrl(BaseCommon.P2P_SERVER_PORT,url);
                    } else {
                        result = P2PTrans.stopById(BaseCommon.P2P_SERVER_PORT, streamId);
                    }
                    if(!isEmpty(result)) {
                        LogUtils.showLog("停止单个任务:" + result);
                    }else{
                        LogUtils.showLog("没有得到停止任务的信息");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// 清除播放缓存 //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Context context= MainApplicationContext.context;
    //删除播放的没有下载过的文件
    public void clearPlayCache(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                //得到所有的文件信息
                String rootPath = MainApplicationContext.SD_PATH;
                File file = new File(rootPath);
                if (!file.exists()) {
                    return;
                }
                if (file == null) {
                    return;
                }
                File[] files = file.listFiles();
                if (files == null || (files != null && files.length <= 0)) {
                    return;
                }
                for (File fItem : files) {
                    if (fItem == null) {
                        continue;
                    }
                    boolean isHave=true;
                    if (fItem.isDirectory()) {//是文件夹//
                        String name = fItem.getName();
                        CacheModel c = getModelByDataStreamId(name);
                        if (c == null) {
                            isHave=false;
                        }
                    } else {//是文件
                        boolean b=getExist(fItem.getName());
                        if (!b) {//没有包含在里面
                            isHave=false;
                        }
                    }
                    if(!isHave){
                        deleteLocalFile(fItem);
                    }
                }
            }catch (Exception ex){}
            }
        }).start();
        try {
            Toast toast = Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// 删除缓存 、/////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //删除缓存
    public void deleteCache(List<CacheModel> cacheModels){
        final List<CacheModel> cList=cacheModels;
        isStopOption=true;
        //删除内存数据
        for(CacheModel c:cList){
            rumMaps.remove(c.getShortId());
            dataMaps.remove(c.getShortId());
            overMaps.remove(c.getShortId());
        }
        //停止任务
        stopTask(cList);

        new Thread(new Runnable() {
            @Override
            public void run() {
            //删除数据库数据
            for(CacheModel c:cList){
                CacheModel cacheModel=new CacheModel();
                cacheModel.setStreamId(c.getStreamId());
                cacheModel.setName(c.getName());
                dbHandler.delteDown(cacheModel);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }
        }).start();
        deleteLocal(cList);
    }
    public void deleteDb(final List<CacheModel> cacheModels){
        //删除数据库数据
        try {
            deleteLocal(cacheModels);
            for (CacheModel c : cacheModels) {
                CacheModel cacheModel = new CacheModel();
                cacheModel.setShortId(c.getShortId());
                dbHandler.delteCache(cacheModel);
            }
        }catch (Exception ex){}

    }
    private void deleteLocal(final List<CacheModel> cacheModels){
        //删除本地数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String rootPath = MainApplicationContext.SD_PATH;
                    for (CacheModel c : cacheModels) {
                        String url = c.getUrl();
                        int lastIndex = url.lastIndexOf("/");
                        String name = url.substring(lastIndex + 1);
                        String path = rootPath + name;
                        File file = new File(path);
                        deleteLocalFile(file);
                        if (c == null || isEmpty(c.getStreamId())) {
                            continue;
                        }
                        String fPath = rootPath + c.getStreamId();
                        File f = new File(fPath);
                        deleteLocalFile(f);
                        Thread.sleep(1000);
                    }
                }catch (Exception ex){}
            }
        }).start();
    }
    //删除缓存数据(正在运行、已缓存完成数据)
    private void deleteLocalFile(File file){
        try {
            if (!file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                SystemUtils.deleteDirWihtFile(file);
            } else {
                file.delete();
            }
        }catch (Exception ex){}
    }
    private boolean isEmpty(String s){
        return s==null||s.length()<=0;
    }
}
