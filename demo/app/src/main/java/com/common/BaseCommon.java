package com.common;

import com.xcore.data.bean.CdnBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCommon {
    //Umeng
    public static String CHANNEL="";
    public static String VERSION_NAME="";
    public static int VERSION_CODE=0;
    public static boolean isTestServer=false;//是否是测试服
    public static int HOT_VERSION=0;

    //服务端口
    public static int P2P_SERVER_PORT=8777;
    public static int HTTP_PORT=8080;

    public static List<String> jsonList=
            Arrays.asList(
            );
    public static List<String> JSON_LIST=new ArrayList<>();
    public static List<String> apiList=new ArrayList<>();

    //API请求路径
    public static String API_URL="";

    public static List<String> videoLists=new ArrayList<>();
    //初始路径
    public static String VIDEO_URL="";

    public static List<String> resLists=new ArrayList<>();
    //资源路径
    public static String RES_URL="";

    public static List<String> apkLists=new ArrayList<>();
    //apk 更新路径
    public static String APK_URL="";
    //测试地址
    public static List<CdnBean.CdnDataItem> testUrlMaps=new ArrayList<>();
	
    public static List<String> httpAccelerateLists=new ArrayList<>();

    //速度测试
    public static List<CdnBean.CdnDataItem> downSpeedTestList=new ArrayList<>();

    public static List<CdnBean.CdnDataItem> ccList=new ArrayList<>();

    public static List<CdnBean.CdnDataItem> trackerList=new ArrayList<>();

    public static List<String> recodeList=new ArrayList<>();

    public static List<String> m3u8List=new ArrayList<>();
    public static String M3U8_URL="";

    public static String HTTP_URL="";
    //检测线路
    public static List<CdnBean.CdnDataItem> hList=new ArrayList<>();
    public static List<CdnBean.CdnDataItem> mList=new ArrayList<>();
    public static List<CdnBean.CdnDataItem> pList=new ArrayList<>();

}
