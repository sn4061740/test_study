package com.xcore;

import com.common.BaseCommon;
import com.jay.config.Md5Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.LogUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaSpeedTest {
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void start(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
            try {
                Thread.sleep(80000);
                if(MainApplicationContext.heartTimer==null){
                    return;
                }
                List<CdnBean.CdnDataItem> mps= BaseCommon.downSpeedTestList;
                if(mps!=null&&mps.size()>0) {
                    for (CdnBean.CdnDataItem item : mps) {
                        testSpeed(item);
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            }
        });
    }

    //平均速度
    private Map<String,List<Long>> speeds=new HashMap<>();

    /**
     * 开始测试速度
     * @param item
     */
    private void testSpeed(CdnBean.CdnDataItem item){
        final CdnBean.CdnDataItem cdnDataItem=item;
        speeds.put(cdnDataItem.getUrl(),new ArrayList<Long>());
        final long startTime=System.currentTimeMillis();
        OkGo.<File>get(cdnDataItem.getUrl()).execute(new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                final File file =response.body();
                try {
                    long endTime = System.currentTimeMillis();
                    long eT = endTime - startTime;
                    toServer(file, cdnDataItem, eT, "成功");
                }catch ( Exception ex){}
            }
            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                try {
                    List<Long> sp = speeds.get(cdnDataItem.getUrl());
                    sp.add(progress.speed);
                    LogUtils.showLog("下载速度：" + progress.speed);
                }catch (Exception ex){}
            }
            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                try {
                    long endTime = System.currentTimeMillis();
                    long eT = endTime - startTime;
                    String msg = response.message();
                    if (msg == null || msg.length() <= 0) {
                        msg = "请求文件错误,但是没有得到异常信息";
                    }
                    toServer(null, cdnDataItem, eT, msg);
                }catch (Exception ex){}
            }
        });
    }

    /**
     * @param file
     * @param item
     * @param time
     */
    private void toServer(final File file, final CdnBean.CdnDataItem item, final long time,final String error){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int status = 0;
                    if(file!=null) {
                        final String value = Md5Utils.getMD5(file);
                        if (value.equals(item.getMd5())) {//文件验证成功
                            status = 1;
                        }
                    }
                    List<Long> sList = speeds.get(item.getUrl());
                    long total = 0;
                    long speed = 0;
                    if (sList != null && sList.size() > 0) {
                        for (Long l : sList) {
                            total += l;
                        }
                        speed = total / sList.size();
                    }
                    String domainIp = "未知";
                    try {
                        URL url = new URL(item.getUrl());
                        String domain = url.getHost();
                        domainIp = domain + "|";
                        InetAddress inetAddress = InetAddress.getByName(domain);
                        domainIp += inetAddress.getHostAddress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    HttpParams params = new HttpParams();
                    params.put("sourceUrl", item.getUrl());
                    params.put("status", status);
                    params.put("shortId", domainIp);
                    params.put("key", String.valueOf(time));
                    params.put("error", error);
                    params.put("speed", speed);
                    ApiFactory.getInstance().<String>testSpeedByFile(params, new TCallback<String>() {
                        @Override
                        public void onNext(String s) {
//                        Log.e("TAG","速度测试返回:"+s);
                        }
                    });
                }catch (Exception ex){}
            }
        });
    }

}
