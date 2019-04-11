package com.xcore.utils;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 检测
 */
public class TrackerCheck {
    StringBuffer stringBuffer=new StringBuffer();
    private String playUrl="";
    List<CdnBean.CdnDataItem> items=new ArrayList<>();
    private int status;//全部成功    900002:有失败的

    public void start(String shortId, String torUrl,String errorStr){
        this.playUrl=torUrl;
        status=LogUtils.PLAY_ERROR;
        stringBuffer.append("shortId="+shortId+"|"+errorStr+"\n");
        stringBuffer.append("------------------------------------------------------\n");

        List<CdnBean.CdnDataItem> itemList= BaseCommon.trackerList;
        for(CdnBean.CdnDataItem itemModel:itemList){
            CdnBean.CdnDataItem item=new CdnBean.CdnDataItem();
            item.setUrl(itemModel.getUrl());
            item.setPort(itemModel.getPort());
            items.add(item);
        }
        startCheck();
    }

    private void startCheck(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(items!=null&&items.size()>0){
                    CdnBean.CdnDataItem item=items.remove(0);
                    toTest(item.getUrl(),item.getPort());
                }else{//没有了,提交异常信息
                    String eStr=stringBuffer.toString();
                    LogUtils.videoErrorUp(playUrl,eStr,status);
                }
            }
        }).start();

    }

    private void toTest(final String url, final Integer port){
        new Thread(new Runnable() {
            @Override
            public void run() {
            String ipStr=IPUtils.getIP(url);//  IPUtils.getIP(url);// getIP(getDomain(url));
            try {
                if(TextUtils.isEmpty(ipStr)){
                    return;
                }
                connect(url,ipStr,port);
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }).start();
    }

    /**
     * 连接
     * @param url
     * @param server
     * @param servPort
     */
    private void connect(final String url,final String server, final int servPort){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1.创建一个Socket实例：构造函数向指定的远程主机和端口建立一个TCP连接
                Socket socket = new Socket();
                String error="";
                try {
                    socket.setReceiveBufferSize(8192);
                    socket.setSoTimeout(5000);// socket.setSoTimeout(2000);
                    SocketAddress address = new InetSocketAddress(server, servPort);
                    socket.connect(address, 5000);//1.判断ip、端口是否可连接
                    error="连接成功";
                } catch (Exception e) {
                    status=LogUtils.PLAY_ERROR_THRACKER_ERROR;
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
                        Log.e("TAG","检测完成了:"+url+"|"+server+" :"+servPort);
                        stringBuffer.append("检测完成了:"+url+"|"+server+":"+servPort+"\n");
                        stringBuffer.append(error+"\n");
                        stringBuffer.append("------------------------------------------------------\n");
                        if(socket!=null) {
                            socket.close();
                            socket = null;
                        }
                    }catch (Exception ex){}
                    finally {
                        startCheck();
                    }
                }
            }
        }).start();
        //2. 通过套接字的输入输出流进行通信：一个Socket连接实例包括一个InputStream和一个OutputStream，它们的用法同于其他Java输入输出流。
//        in = socket.getInputStream();
//        out = socket.getOutputStream();
//        isalreadyconnected=1;
        //connect1( server,  servPort) ;
    }

}
