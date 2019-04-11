package com.xcore.ui.test;

import android.app.Activity;
import android.com.baselibrary.MyApplication;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.ui.Config;
import com.xcore.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.p2ptrans.P2PTrans;

//tor 切换
public class TorLineController {

    private List<String> torLines;
    private LineChangeListener lineChangeListener;
    private String currentPath="";
    private Activity activity;
    private String shortId="";
    private String sourceUrl;

    public TorLineController(LineChangeListener changeListener,Activity activity1,String sId){
        this.lineChangeListener=changeListener;
        torLines=new ArrayList<>();
        this.activity=activity1;
        this.shortId=sId;
    }
    public void change(String sourceUrl1){
        this.sourceUrl=sourceUrl1;
        //先判断本地有没有这个种子
        String localUrl=MainApplicationContext.SD_PATH+sourceUrl;
        File file=new File(localUrl);
        if(file.exists()&&lineChangeListener!=null){
            String fPath=file.getAbsolutePath();
            if(!torLines.contains(fPath)) {
                torLines.add(fPath);
                currentPath=fPath;
                String fUrl = P2PTrans.getUrlAddPass(fPath);
                fUrl = P2PTrans.getTorrentPlayUrl(fUrl);//URL　播放
                lineChangeListener.changeSuccess(fUrl);
                getStreamId(sourceUrl1);
                return;
            }
        }
        if(BaseCommon.VIDEO_URL==null||BaseCommon.VIDEO_URL.length()<=0){
            Config config=MainApplicationContext.getConfig();//从本地赋值
            String xUrl=config.get("VIDEO_URL");
            if(xUrl.length()<=0){//还未读取到地址
                if(lineChangeListener!=null){
                    lineChangeListener.changeError();
                }
                return;
            }
            BaseCommon.VIDEO_URL=xUrl;
        }
        String path=BaseCommon.VIDEO_URL+sourceUrl;
        if(torLines.contains(path)){
            path="";
        }
        if(path.length()<=0){
            List<String> torList= BaseCommon.videoLists;
            if(torList!=null&&torList.size()>0){
                for (String baseUrl:torList){
                    String newPath=baseUrl+sourceUrl;
                    if(torLines.contains(newPath)){
                        continue;
                    }
                    path=newPath;
                    break;
                }
            }
        }
        if(path.length()<=0){
            if(lineChangeListener!=null){
                lineChangeListener.changeError();
            }
        }else{
            currentPath=path;
            torLines.add(path);
            downTor(path,sourceUrl);
        }
    }
    //下载tor
    private void downTor(final String path, final String sourceUrl){
        String rootPath=MainApplicationContext.SD_PATH;
        OkGo.<File>get(path).tag(this).execute(new FileCallback(rootPath,sourceUrl) {
            @Override
            public void onSuccess(Response<File> response) {
                try {
//                    LogUtils.showTips("下载碾国。", R.color.title_color);
                    File file = response.body();
                    String fPath = file.getAbsolutePath();
                    String fUrl = P2PTrans.getUrlAddPass(fPath);
                    fUrl = P2PTrans.getTorrentPlayUrl(fUrl);//URL　播放
                    if(lineChangeListener!=null) {
                        lineChangeListener.changeSuccess(fUrl);
                    }
                    getStreamId(sourceUrl);
                    try {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("shortId", shortId);
//                        MobclickAgent.onEvent(activity, "tor_success", map);
                        MyApplication.toMobclickAgentEvent(activity,"tor_success",map);
                    }catch (Exception ex){}
                }catch (Exception ex){
                    if(lineChangeListener!=null){
                        lineChangeListener.changeError();
                    }
                }
            }
            @Override
            public void onError(Response<File> response) {
                if(lineChangeListener!=null){
                    lineChangeListener.changeError();
                }
                try {
                    int code=response.code();
                    //1、发送错误信息
                    final String exStr = LogUtils.getException(response.getException());
                    String eStr = exStr;
                    if (eStr == null || eStr.length() <= 0) {
                        eStr = "请求出错了。没有获取到异常消息..";
                    }
                    //上报错误
                    LogUtils.videoErrorUp(path, "|请求种子失败get,请求不到种子00" + eStr, code);
                    if (exStr.contains("No space left on device") || exStr.contains("ENOSPC")) {
                        LogUtils.showTips("去个人中心->我的缓存(清除缓存空间试试)", R.color.title_color);
                    }
                }catch (Exception ex){}

                try {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("shortId", shortId);
                    MyApplication.toMobclickAgentEvent(activity,"tor_error",map);
//                    MobclickAgent.onEvent(activity, "tor_error", map);
            }catch (Exception ex){}
            }
            @Override
            public void downloadProgress(Progress progress) {
                if(lineChangeListener!=null){
                    lineChangeListener.onProgress(progress.speed);
                }
            }
        });
    }
    private Timer timer;
    //获取streamId
    private void getStreamId(final String path){
        LogUtils.showLog(path);
        streamId="";
        stopTimer();
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            try{
                P2PTrans.StreamsResult result= P2PTrans.getStreams(BaseCommon.P2P_SERVER_PORT);
                if(result==null){
                    return;
                }
                List<P2PTrans.StreamInfo> infos=result.getStreams();
                if(infos==null||infos.size()<=0){
                    return;
                }
                boolean b=false;
                for(P2PTrans.StreamInfo info:infos){
                    String selectedFilePath = info.getSelectedFilePath();
                    if ((selectedFilePath==null||selectedFilePath.length()<=0) || "null".equals(selectedFilePath) || info.getId() == null) {
                        continue;
                    }
                    String xPath=path.replace(".Torrent","");
                    xPath=xPath.replace(".torrent","");
                    if(selectedFilePath.contains(xPath)){
                        streamId=info.getId();
                        LogUtils.showLog("得到的SID="+streamId);
                        stopTimer();
                        if(lineChangeListener!=null){
                            lineChangeListener.setStreamId(streamId);
                        }
                        break;
                    }
                }
            }catch (Exception ex){}
            }
        },1000,1000);//03522251e1f8c58ad2786554c7d5ec1b
    }
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;


    }

    private String streamId="";
    private boolean isEmpty(String s){
        return s==null||s.length()<=0;
    }
    public void onDestroy(){
        try {
            stopTimer();
            OkGo.getInstance().cancelTag(this);
            CacheModel cacheModel = CacheManager.getInstance().getLocalDownLoader().getRunByShortId(shortId);//.getDownHandler().getByShortId(shortId);
            if (cacheModel != null && cacheModel.isStop() == false) {//证明正在运行这个任务,没有停止的任务
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                if(streamId!=null&&streamId.length()>0) {
                    String xStr=P2PTrans.stopById(BaseCommon.P2P_SERVER_PORT, streamId);
                    LogUtils.showLog("停止任务:"+xStr);
                }else{
                    String vStr=P2PTrans.stopAllStream(BaseCommon.P2P_SERVER_PORT);
                    LogUtils.showLog("停止所有任务:"+vStr);
                }
                }
            }).start();

        }catch (Exception ex){}
    }
}
