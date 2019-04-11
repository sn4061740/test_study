package com.xcore;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.IPUtils;
import com.xcore.utils.LogUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * socket ip 端口测试
 */
public class JavaSocketTest {
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void start(){
        boolean socketTestLogBoo=MainApplicationContext.socketTestLog;
        if(socketTestLogBoo==false){
            return;
        }
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000); // 休眠10秒
                    if(MainApplicationContext.heartTimer==null){
                        return;
                    }
                    List<CdnBean.CdnDataItem> mps= BaseCommon.testUrlMaps;
                    if(mps!=null&&mps.size()>0) {
                        for (CdnBean.CdnDataItem item : mps) {
                            Thread.sleep(100);
                            toTest(item.getUrl(), item.getPort());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void toTest(final String url, final Integer port){
        String ipStr= IPUtils.getIP(url);// getIP(getDomain(url));
        try {
            if(TextUtils.isEmpty(ipStr)){
                return;
            }
            connect(url,ipStr,port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void connect(final String url,final String server, final int servPort){
        //1.创建一个Socket实例：构造函数向指定的远程主机和端口建立一个TCP连接
        //socket = new Socket(server, servPort);
        long startTime=System.currentTimeMillis();
        Socket socket = new Socket();
        int status=0;
        String error="";
        try {
            socket.setReceiveBufferSize(8192);
            socket.setSoTimeout(5000);// socket.setSoTimeout(2000);
            SocketAddress address = new InetSocketAddress(server, servPort);
            socket.connect(address, 5000);//1.判断ip、端口是否可连接
            status=1;
        } catch (Exception e) {
            status=0;
            if(e!=null) {
                error = LogUtils.getException(e);
            }else{
                error="检测IP + 端口出现异常,但是异常信息是null，怎么弄呢。。。";
            }
            if(TextUtils.isEmpty(error)){
                error="连接失败,错误消息null";
            }
        }
        finally {
            try{
                long endTime=System.currentTimeMillis();
                long eT=endTime-startTime;
                LogUtils.toSendSocketServer(eT,server+":"+servPort,status,url,error);
                if(socket!=null) {
                    socket.close();
                    socket = null;
                }
            }catch (Exception ex){}
        }
    }

}
