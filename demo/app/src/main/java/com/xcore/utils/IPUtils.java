package com.xcore.utils;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class IPUtils {

    /**
     * 返回域名和IP
     * @param urlStr1
     * @return
     */
    public static String getIp(String urlStr1){
        //解析地址得到IP
        String domainIp ="";
        try {
            URL url = new URL(urlStr1);
            domainIp = url.getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            InetAddress inetAddress= InetAddress.getByName(domainIp);
            domainIp+="|"+inetAddress.getHostAddress();
        }catch (Exception ex){
            domainIp+="|";
        }
        return domainIp;
    }

    /**
     * 获取url对应的域名
     *
     * @param url
     * @return
     */
    private static String getDomain(String url) {
        String result = "";
        try {
            int j = 0, startIndex = 0, endIndex = 0;
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/') {
                    j++;
                    if (j == 2)
                        startIndex = i;
                    else if (j == 3)
                        endIndex = i;
                }
            }
            result = url.substring(startIndex + 1, endIndex);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据域名得到IP
     * @param domain
     * @return
     */
    public static String getIP(String domain) {
        String dUrl=getDomain(domain);
        String ipAddress = "";
        InetAddress iAddress = null;
        try {
            iAddress = InetAddress.getByName(dUrl);
        } catch (UnknownHostException e) {
            //e.printStackTrace();
        }catch (Exception ex){}
        if (iAddress == null)
            Log.i("xxx", "iAddress ==null");
        else {
            ipAddress = iAddress.getHostAddress();
        }
        return ipAddress;
    }

    public static String getIpStr(String domain){
        String ipStr="";
        try {
            InetAddress address = InetAddress.getByName(new URL(domain).getHost());
            ipStr= address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ipStr;
    }

    /**
     * 检测对应ip/端口是否可以连能并上报服务器
     */
    public static void checkTracker(final List<CdnBean.CdnDataItem> items, final String shortId){
        final String sId=shortId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000); // 休眠5秒
                    List<CdnBean.CdnDataItem> mps= items;//BaseCommon.testUrlMaps;
                    if(mps!=null&&mps.size()>0) {
                        for (CdnBean.CdnDataItem item : mps) {
                            Thread.sleep(300);
                            toTest(item.getUrl(), item.getPort(),sId);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void toTest(final String url, final Integer port, final String shortId){
        final String sId=shortId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ipStr=getIp(url);//  IPUtils.getIP(url);// getIP(getDomain(url));
                try {
                    if(TextUtils.isEmpty(ipStr)){
                        return;
                    }
                    connect(url,ipStr,port,sId);
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
    private static void connect(final String url,final String server, final int servPort,String shortId){
        final String sId=shortId;
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
//            Log.e("TAG1", "新建一个socket");
//            Log.e("TAG1", "Connected to server... sending echo string");
        } catch (Exception e) {
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
                toSendServer(eT,server+":"+servPort,status,url,error,sId);
                if(socket!=null) {
                    socket.close();
                    socket = null;
                }
            }catch (Exception ex){}
        }
        //2. 通过套接字的输入输出流进行通信：一个Socket连接实例包括一个InputStream和一个OutputStream，它们的用法同于其他Java输入输出流。
//        in = socket.getInputStream();
//        out = socket.getOutputStream();
//        isalreadyconnected=1;
        //connect1( server,  servPort) ;
    }

    /**
     * 上报信息
     * @param time
     * @param ip
     * @param status
     * @param url
     * @param error
     */
    private static void toSendServer(long time,String ip,int status,String url,String error,String shortId){
        final String sId=shortId;
        HttpParams params = new HttpParams();
        params.put("sourceUrl", url);
        params.put("status", status);
        params.put("shortId", ip);
        params.put("key", String.valueOf(time));
        params.put("error", error);

        ApiFactory.getInstance().<String>toServerSocket(params, new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG", "发送socket测试信息成功" + s);
            }
        });
    }
}
