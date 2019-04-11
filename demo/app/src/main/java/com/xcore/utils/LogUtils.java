package com.xcore.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheInfoModel;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.Config;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.ui.test.PlaySpeedModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志上报
 */
public class LogUtils {
    //控制台日志信息
    public static boolean SHOW_LOG=true;
    //上报状态码
    public static int PLAY_ERROR=900001;//播放失败,tracker 成功 种子的
    public static int PLAY_ERROR_THRACKER_ERROR=900002;//播放失败,tracker 失败 种子的

    public static int PLAY_ERROR_100=900003;//视频中断，一般是视频源异常或者不支持的视频类型
    public static int PLAY_ERROR_70003=900004;//打开视频失败  一般不会来吧。。。

    public static int PLAY_ERROR_HTTP=900005;//http、m3u8 的播放失败
    public static int ERROR_OTHER=-99999;//其他错误

    public static int ERROR_CODE_404=404;//请求不到
    public static int ERROR_CODE_408=408;//请求超时
    public static int ERROR_CODE_418=418;//Connection reset
    public static int ERROR_CODE_5XX=500;//服务器错误
    public static int ERROR_CODE_200=200;//成功
    public static int ERROR_CODE_900100=900100;//网络返回解析失败

    //版本升级错误码...
//    public static int VERSION_CODE_SUCCESS=60000;//版本更新成功
    public static int VERSION_CODE_CANCEL=60001;//用户取消下载
    public static int VERSION_CDOE_V_ERROR=60002;//下载成功,检验失败
    public static int VERSION_CDOE_NULL_ERROR=60003;//下载成功,获取到的文件为null
    public static int VERSION_CDOE_ERROR=60004;//下载失败
    public static int VERSION_CDOE_K_ERROR=60005;//未获取到 开始下载时候的 key


    private static Context context=MainApplicationContext.context;


    static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    static Toast toast;
    public static void showTips(String msg,int color){
        TextView view=null;
        if(toast!=null){
            view=  (TextView) toast.getView().findViewById(android.R.id.message);
            toast.setText(msg);
        }else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setText(msg);
            view= (TextView) toast.getView().findViewById(android.R.id.message);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        if(view!=null) {
            view.setTextColor(context.getResources().getColor(color));
        }
        toast.show();
    }
    /**
     * 显示日志信息
     * @param msg
     */
    public static void showLog(String msg){
        if(isEmpty(msg)||!SHOW_LOG){
            return;
        }
        Log.e("TAG",msg);
    }
    //判断字符串是否为空
    private static boolean isEmpty(String msg){
        return msg==null||msg.length()<=0;
    }

    //图片出错上报  新版本上报
    public static void imageUp(final String msg1, final String model, final long eT,int status1) {
        final String msg=msg1;
        boolean imageErrorLogBoo=MainApplicationContext.imageErrorLog;
        if(imageErrorLogBoo==false){
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                int status = ERROR_CODE_404;
                if (msg.contains("timeout") ||
                        msg.contains("SocketTimeoutException") ||
                        msg.contains("connect timed out")||
                        msg.contains("timed out")) {
                    status = ERROR_CODE_408;
                } else if (msg.contains("reset")) {
                    status = ERROR_CODE_418;
                } else if (msg.contains("404")) {
                    status = ERROR_CODE_404;
                } else if (msg.contains("5xx") || msg.contains("5XX") || msg.contains("500")) {
                    status = ERROR_CODE_5XX;
                } else {//其他
                    status = LogUtils.ERROR_OTHER;
                }
                String vMsg = msg;// + getNetworkInfo();
//                HttpParams params = new HttpParams();
//                params.put("ResponseCode", status);
//                params.put("RequestAddress", model);
//                params.put("ResponseTime", eT);
//                params.put("RequestMethod", "GET");
//                params.put("RequestParameter", model + "::" + vMsg);
//                ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
//                    @Override
//                    public void onNext(SpeedDataBean speedDataBean) {
//                        showLog("上传图片日志:" + speedDataBean.toString());
//                    }
//                });
                toErr(model,vMsg,status,eT);
            }catch (Exception ex){}
            }
        });
    }
    //获取网络信息
    public static String getNetworkInfo(){
        try {
            int v1 = NetWorkUtils.getNetworkState(null);
            String msg = "|NETWORK=" + v1;
            if (v1 > 1) {
                String v2 = NetWorkUtils.getOperatorName(null);
                if (TextUtils.isEmpty(v2)) {
                    v2 = "未获取到运营商信息";
                }
                msg += "|NETWOORK_NAME=" + v2;
            }
            msg += "|网络状态说明(0:没有网络,1:wifi,2:2G,3:3G,4:4G,5:手机流量,运营商http://baike.baidu.com/item/imsi)";
            return msg;
        }catch (Exception ex){}
        return "";
    }

    static Map<String,String> ids=new HashMap<>();

    public static String shortId="";
    public static String sId="";
    public static boolean isUpSpeed=false;
    /**
     * 播放速度上报
     * @param playSpeedModel1
     */
    public static void playSpeedUp(PlaySpeedModel playSpeedModel1,String shortId1){
        if(isUpSpeed){
            return;
        }
        isUpSpeed=true;
        final PlaySpeedModel playSpeedModel=playSpeedModel1;
        if(!shortId.equals(shortId1)){
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String hUrl=playSpeedModel.getHttpUrl();
                    HttpParams params=new HttpParams();

                    params.put("key",playSpeedModel.getAvgSpeed());
                    params.put("duration",playSpeedModel.getStopTime());
                    params.put("pos",playSpeedModel.getCurrentPos());//当前播放
                    params.put("httpUrl",hUrl);
                    params.put("total",playSpeedModel.getTotal());
                    params.put("httpAvg",playSpeedModel.getHttpAvg());
                    params.put("httpTotal",playSpeedModel.getHttpTotal());
                    params.put("line",playSpeedModel.getLine());
                    if("".equals(sId)||sId==null||sId.length()<=0) {
//                        LogUtils.showLog("上传播放速度。。。");
                        params.put("shortId", shortId);
                        ApiFactory.getInstance().<String>toPlayInfo(params, new TCallback<String>() {
                            @Override
                            public void onNext(String s) {
//                                LogUtils.showLog("得到播放KEY="+s);
                                try {
                                    JsonObject o = new Gson().fromJson(s, JsonObject.class);
                                    JsonElement keyStr = o.get("data");
                                    String key = keyStr.getAsString();
                                    sId = key;
                                }catch (Exception e){
//                                    Log.e("TAG", "转换得到key 出错。。");
                                }finally {
                                    isUpSpeed=false;
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                isUpSpeed=false;
                            }
                        });
                    }else{
//                        LogUtils.showLog("更新播放速度。。。"+sId);
                        params.put("shortId", sId);
                        ApiFactory.getInstance().<String>toUpdatePlayInfo(params, new TCallback<String>() {
                            @Override
                            public void onNext(String s) {
//                                Log.e("TAG", "进度更新：" + s);
                                isUpSpeed=false;
                            }
                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                isUpSpeed=false;
                            }
                        });
                    }
                }catch (Exception ex){}
            }
        });
    }

    /**
     * 缓存速度上报
     * @param infoModel
     */
    public static void cacheSpeedUp(CacheInfoModel infoModel){
        final CacheInfoModel cacheModel=infoModel;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpParams params=new HttpParams();
                    long totalSize =cacheModel.totalSize;
                    if(totalSize<=0){
                        return;
                    }

                    final String shortId=cacheModel.shortId;
                    double downSpeed = cacheModel.getAvgSpeed();
                    double serverTotalSpeed =cacheModel.getHttpAvgSpeed();
                    long totalBytesDown = cacheModel.totalBytesDown;
                    long totalServerBytes = cacheModel.httpTotalDownSize;


                    LogUtils.showLog(shortId+"=>总平均速度:"+downSpeed+" ||| HTTP平均速度:"+serverTotalSpeed);

                    String hUrl = MainApplicationContext.httpIpAndUrl;
                    params.put("key", downSpeed);//总速度的平均速度
                    params.put("duration", cacheModel.totalDownSize);//已下载
                    params.put("pos", cacheModel.perent);//当前进度 80 %
                    params.put("httpUrl", hUrl);//http 地址
                    params.put("httpAvg", serverTotalSpeed);//http速度
                    params.put("total", totalBytesDown);//本次总下载
                    params.put("httpTotal", totalServerBytes);//本次http 总下载
                    params.put("totalSize", totalSize);//总大小

                    String sId= ids.get(shortId);
                    if(TextUtils.isEmpty(sId)){//添加
                        params.put("shortId", shortId);
                        ApiFactory.getInstance().<String>addCacheInfo(params, new TCallback<String>() {
                            @Override
                            public void onNext(String s) {
//                        showLog(s);
                                try {
                                    JsonObject jsonObject = new Gson().fromJson(s, JsonObject.class);
                                    JsonElement jsonElement = jsonObject.get("data");
                                    if(jsonElement!=null) {
                                        String idStr = jsonElement.getAsString();
                                        ids.put(shortId,idStr);
                                    }
                                }catch (Exception e){}
                            }
                        });
                    }else{
                        params.put("shortId", sId);
                        ApiFactory.getInstance().<String>updateCacheInfo(params, new TCallback<String>() {
                            @Override
                            public void onNext(String s) {
//                        showLog(s);
                            }
                        });
                    }
                }catch (Exception e){}
            }
        });
    }

    //API 请求出错上报 新增请求时间
    public static void apiRequestError(final String model, final String msg1, final long timer, final int code) {
        final String msg=msg1;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                boolean apiErrorLogBoo=MainApplicationContext.apiErrorLog;
                if(apiErrorLogBoo==false){
                    return;
                }
                try {
                    int status = code;
                    if (status <= 0) {
                        if (msg.contains("timeout") ||
                                msg.contains("SocketTimeoutException") ||
                                msg.contains("connect timed out")||
                                msg.contains("timed out")) {
                            status = ERROR_CODE_408;
                        } else if (msg.contains("reset")) {
                            status = ERROR_CODE_418;
                        } else if (msg.contains("404")) {
                            status = ERROR_CODE_404;
                        } else if (msg.contains("5xx") || msg.contains("5XX") || msg.contains("500")) {
                            status = ERROR_CODE_5XX;
                        } else {//其他的当404 处理
                            status = LogUtils.ERROR_OTHER;
                        }
                    }
                    String vMsg = msg;// + getNetworkInfo();
//                    HttpParams params = new HttpParams();
//                    params.put("ResponseCode", status);
//                    params.put("RequestAddress", model);
//                    params.put("ResponseTime", timer);
//                    params.put("RequestMethod", "GET");
//                    params.put("RequestParameter", vMsg);
//                    params.put("onlyCode", fCode);
//                    ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
//                        @Override
//                        public void onNext(SpeedDataBean speedDataBean) {
//                            showLog("API日志上传:" + speedDataBean.toString());
//                        }
//                    });

                    toErr(model,vMsg,status,timer);

                }catch (Exception ex){}
            }
        });
    }

    private static void toErr(String model,String eMsg,int status,long timer){
        String vMsg = eMsg + getNetworkInfo();
        Config config=MainApplicationContext.getConfig();
        String fCode =config.getKey();// SystemUtils.getFingerprint();
        HttpParams params = new HttpParams();
        params.put("ResponseCode", status);
        params.put("RequestAddress", model);
        params.put("ResponseTime", timer);
        params.put("RequestMethod", "GET");
        params.put("RequestParameter", vMsg);
        params.put("onlyCode", fCode);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                showLog("日志上传:" + speedDataBean.toString());
            }
        });
    }

    //升级 请求出错上报 新增请求时间
    public static void versionRequestError(final String model, final String msg1,final int code) {
        final String msg=msg1;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                boolean apiErrorLogBoo=MainApplicationContext.apiErrorLog;
                if(apiErrorLogBoo==false){
                    return;
                }
                try {
//                    String fCode = SystemUtils.getFingerprint();
                    int status = code;
                    if (status <= 0) {
                        if (msg.contains("timeout") ||
                                msg.contains("SocketTimeoutException") ||
                                msg.contains("connect timed out")||
                                msg.contains("timed out")) {
                            status = ERROR_CODE_408;
                        } else if (msg.contains("reset")) {
                            status = ERROR_CODE_418;
                        } else if (msg.contains("404")) {
                            status = ERROR_CODE_404;
                        } else if (msg.contains("5xx") || msg.contains("5XX") || msg.contains("500")) {
                            status = ERROR_CODE_5XX;
                        } else {//其他的当404 处理
                            status = LogUtils.ERROR_OTHER;
                        }
                    }
                    String vMsg = msg ;//+ getNetworkInfo();
//                    HttpParams params = new HttpParams();
//                    params.put("ResponseCode", status);
//                    params.put("RequestAddress", model);
//                    params.put("ResponseTime", 0);
//                    params.put("RequestMethod", "GET");
//                    params.put("RequestParameter", vMsg);
//                    params.put("onlyCode", fCode);
//                    ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
//                        @Override
//                        public void onNext(SpeedDataBean speedDataBean) {
//                            showLog("版本升级日志上传:" + speedDataBean.toString());
//                        }
//                    });
                    toErr(model,vMsg,status,0);
                }catch (Exception ex){}
            }
        });
    }

    //视频加载出错上报
    public static void videoErrorUp(final String model, final String msg1, final int statusCode) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                final String msg = msg1;
                int status = statusCode;
                if (status <= 0) {
                    if (msg.contains("timeout") ||
                            msg.contains("SocketTimeoutException") ||
                            msg.contains("connect timed out")||
                            msg.contains("timed out")) {
                        status = ERROR_CODE_408;
                    } else if (msg.contains("reset")) {
                        status = ERROR_CODE_418;
                    } else if (msg.contains("5xx") || msg.contains("5XX") || msg.contains("500")) {
                        status = ERROR_CODE_5XX;
                    } else {//其他
                        status = LogUtils.ERROR_OTHER;
                    }
                }
                String vMsg = msg;// + getNetworkInfo();

//                HttpParams params = new HttpParams();
//                params.put("ResponseCode", status);
//                params.put("RequestAddress", model);
//                params.put("ResponseTime", 0);
//                params.put("RequestMethod", "GET");
//                params.put("RequestParameter", vMsg);
//                ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
//                    @Override
//                    public void onNext(SpeedDataBean speedDataBean) {
//                        showLog("上传视频日志:" + speedDataBean.toString());
//                    }
//                });
                toErr(model,vMsg,status,0);
            }catch (Exception ex){}
            }
        });
    }

    //程序异常出错上报
    public static void crashUp(final String msg) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            String vMsg=msg+getNetworkInfo();
            ApiFactory.getInstance().<LikeBean>toCrashLog(vMsg, new TCallback<LikeBean>() {
                @Override
                public void onNext(LikeBean likeBean) {
                    showLog("上传日志:" + likeBean.toString());
                }
            });
            }
        });
    }

    /**
     * 得到具体的错误消息
     * @param ex
     * @return
     */
    public static String getException(Exception ex) {
        if (ex == null) {
            return "得到的错误消息是 null";
        }
        String ret = ex.getMessage();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            ex.printStackTrace(pout);
            ret = new String(out.toByteArray());
            pout.close();
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

    public static String getException(Throwable e){
        if(e==null){
            return "";
        }
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        }catch (Exception ex){

        }
        return "";
    }

    //上日志文件到服务器
    public static void uploadLog(final Handler handler) {
        try {
            String sdcarPath = getInnerSDCardPath() + "/Download/DLBT_LOG/";
            try {
                ZipUtils.ZipFolder(sdcarPath, getInnerSDCardPath() + "/Download/a.zip");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            handler.sendEmptyMessage(3);
            return;
        }
        String path1=getInnerSDCardPath() + "/Download/a.zip";
        File f=new File(path1);
        if(!f.exists()){
            handler.sendEmptyMessage(2);
            return;
        }
        ApiFactory.getInstance().<String>uploadFile(f, new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG",s);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","上传出错");
                handler.sendEmptyMessage(1);
            }
        });


//        //FTP 上传文件
//        if(IS_OPEN_DLBT_LOG==false){
//            return;
//        }
//        FTPUtils.getInstance().SDPATH = getInnerSDCardPath() + "/Download/";
//        FTPUtils.getInstance().initFTPSetting("ftp://192.168.8.198",14147,"a","123");
//        FTPUtils.getInstance().FTPPort=21;
//        FTPUtils.getInstance().FTPUrl="222.98.47.213";//"47.74.236.24";//"192.168.8.198";
//        FTPUtils.getInstance().UserName="a";
//        FTPUtils.getInstance().UserPassword="123";
//        FTPUtils.getInstance().handler=handler;
//        File file =new File(sdcarPath);
//        List<String> paths=new ArrayList<>();
//        if(file.exists()&&file.isDirectory()){
//            File[] files= file.listFiles();
//            for(File f:files){
//                paths.add(f.getName());
//            }
//        }
//        paths.add("a.zip");
//        FTPUtils.getInstance().startUpload(sdcarPath,paths);

    }

    //上报socket 检测信息
    public static void toSendSocketServer(final long time, final String ip, final int status, final String url, final String error){
        int statusCode=ERROR_CODE_200;
        if(status==0){
            if(error.contains("timeout")||
                    error.contains("SocketTimeoutException")||
                    error.contains("connect timed out")){
                statusCode=ERROR_CODE_408;
            }else if(error.contains("reset")){
                statusCode=ERROR_CODE_418;
            }else if(error.contains("5xx")||error.contains("5XX")||error.contains("500")){
                statusCode=ERROR_CODE_5XX;
            }else{//其他的当404 处理
                 statusCode=ERROR_OTHER;
            }
        }

        HttpParams params = new HttpParams();
        params.put("sourceUrl", url);
        params.put("status", statusCode);
        params.put("shortId", ip);
        params.put("key", String.valueOf(time));
        params.put("error", error);

        ApiFactory.getInstance().<String>toServerSocket(params, new TCallback<String>() {
            @Override
            public void onNext(String s) {
            }
        });
    }

    //上报观看记录到本地
    public static void updateCollect(MovieBean movieBean1, final DetailTouchType touchType, final int position){
        final MovieBean movieBean=movieBean1;
        if(movieBean==null){
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                CacheBean cacheBean = new CacheBean();
                if (touchType == DetailTouchType.TOUCH_COLLECT) {
                    if (movieBean.isCollect == true) {
                        cacheBean.settDelete("0");
                    } else {
                        cacheBean.settDelete("1");
                    }
                } else {
                    if (movieBean.isCollect == true) {
                        cacheBean.settDelete("1");
                    } else {
                        cacheBean.settDelete("0");
                    }
                }
                cacheBean.setShortId(movieBean.getShortId());
                cacheBean.setpStar(String.valueOf(movieBean.getAvgRating()));
                cacheBean.setPlayCount(String.valueOf(movieBean.getPlayCount()));
                cacheBean.settYear(movieBean.getYear());
                cacheBean.setTitle(movieBean.getTitle());
                cacheBean.settDesc(movieBean.getDesc());
                cacheBean.setTime(String.valueOf(movieBean.getDuration()));
                cacheBean.setActor(movieBean.getActor());

                List<CategoriesBean> tagList = movieBean.getTagsList();
                Gson gson = new Gson();
                String tags = gson.toJson(tagList);
                List<NvStar> nvStars = movieBean.getActorList();
                String actors = gson.toJson(nvStars);

                cacheBean.setTags(tags);
                cacheBean.setActors(actors);
                cacheBean.setCover(movieBean.getCover());
                if (touchType == DetailTouchType.TOUCH_COLLECT) {
                    cacheBean.settType(CacheType.CACHE_COLLECT);
                } else if (touchType == DetailTouchType.TOUCH_RECODE) {
                    cacheBean.settType(CacheType.CACHE_RECODE);
                    cacheBean.settDelete("1");
                } else if (touchType == DetailTouchType.TOUCH_CACHE) {
                    cacheBean.settType(CacheType.CACHE_DOWN);
                }
                cacheBean.setPlayPosition(String.valueOf(position));
                cacheBean.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                CacheManager.getInstance().getDbHandler().insertCache(cacheBean);
            }catch (Exception ex){}
            }
        });

    }


    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
