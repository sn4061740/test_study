package com.xcore.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.maning.updatelibrary.InstallUtils;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.AdvBean;
import com.xcore.data.bean.BannerBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.Config;
import com.xcore.ui.activity.FindActivity;
import com.xcore.ui.activity.MakeLTDActivity;
import com.xcore.ui.activity.StarDetailActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.activity.ThemeActivity;
import com.xcore.ui.activity.ThemeListActivity;
import com.xcore.ui.activity.TypeActivity;
import com.xcore.ui.otheractivity.TWebActivity;

import java.io.File;
import java.util.Map;

/**
 * 跳转
 */
public class JumpUtils {

    //跳转广告
    public static void to(Context activity, AdvBean advBean) {
        Intent intent = null;
        if(advBean==null){
            return;
        }
        //advBean.setToUrl("http://192.168.1.7/test.html");
        String advUrl=UriUtils.getAdvUrl(advBean.getToUrl(),(Activity) activity,advBean.getShortId());
        //advBean.setToUrl(advUrl);
        switch (advBean.getJump()) {
            case 1:
                ViewUtils.toPlayer((Activity) activity, null, advBean.getShortId(), null, null, null);
                break;
            case 2:
                intent = new Intent(activity, TypeActivity.class);
                intent.putExtra("shortId", advBean.getShortId());
                intent.putExtra("type", 2);
                intent.putExtra("tag", advBean.getContent());
                break;
            case 3:
                intent = new Intent(activity, TagActivity.class);
                intent.putExtra("tag", advBean.getContent());
                break;
            case 4:
                if (advBean.getShortId().equals("")) {//没有专题ID就切换到推荐页
                    intent = new Intent(activity, ThemeListActivity.class);
                } else {//跳到专题页面
                    intent = new Intent(activity, ThemeActivity.class);
                    intent.putExtra("shortId", advBean.getShortId());//传进专题ID
                }
                break;
            case 5://传的参数是 明星的信息 json 字串
//                intent=new Intent(activity,StarDetailActivity.class);
//                intent.putExtra("shortId",advBean.getShortId());
                break;
            case 7:
                intent = new Intent(activity, TypeActivity.class);
                intent.putExtra("shortId", advBean.getShortId());
                intent.putExtra("type", 7);
                intent.putExtra("tag", advBean.getContent());
                break;
            case 8://浏览器中打开
                try {
                    intent = new Intent(Intent.ACTION_VIEW);//https://www.potato.im/1avco1
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(advUrl);
                    intent.setData(content_url);

//                    intent=new Intent(activity, TWebActivity.class);
//                    intent.putExtra("toUrl",advUrl);
                } catch (Exception ex) {
                }
                break;
            case 9://下载 【暂时浏览器中打开】
                try {
                    isBoo=false;

                    AdvBean advBean1=new AdvBean();
                    advBean1.setShortId(advBean.getShortId());
                    advBean1.setToUrl(advUrl);
                    advBean1.setContent(advBean.getContent());
                    advBean1.setImagePath(advBean.getImagePath());
                    advBean1.setIndex(advBean.getIndex());
                    advBean1.setJump(advBean.getJump());
                    advBean1.setType(advBean.getType());
                    advBean1.setVideoPath(advBean.getVideoPath());
                    advBean1.setTime(advBean.getTime());

                    systemDown(advBean1, activity);
                } catch (Exception ex) {
                }
                break;
            case 10:
                intent = new Intent(activity, FindActivity.class);
                intent.putExtra("tag", "");
                break;
            case 11:
                intent = new Intent(activity, MakeLTDActivity.class);
                intent.putExtra("shortId", advBean.getShortId());
                break;
            case 12://跳转 webview 页面打开
                intent=new Intent(activity, TWebActivity.class);
                intent.putExtra("toUrl",advUrl);
                break;
        }
        if (intent != null) {
            activity.startActivity(intent);
        }
        if(advBean.getJump()!=9) {
            //点击广告了,发送请求
            sendToAdv(advBean.getShortId());
        }
    }

    public static void to(Context context, BannerBean.BannerData bannerData){
        Intent intent=null;
        if(context==null||bannerData==null){
            return;
        }
        String advUrl=bannerData.getShortId();
        if(!advUrl.isEmpty()&&advUrl.contains("http")) {//不是空
//            Config config=MainApplicationContext.getConfig();
//            String code=config.getKey();
//            if (advUrl.contains("?")) {
//                advUrl += "&k=" + code;
//            } else {
//                advUrl += "?k=" + code;
//            }
            advUrl=UriUtils.getAdvUrl(advUrl,(Activity) context,"");
            //bannerData.setShortId(advUrl);
        }
        switch (bannerData.getJump()){
            case 1://电影详情 banner 跳转？
//                intent=new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
                ViewUtils.toPlayer((Activity)context,null,bannerData.getShortId(),null,null,null);
                return;
            case 2://类型
                intent=new Intent(context, TypeActivity.class);
                intent.putExtra("shortId",bannerData.getShortId());
                intent.putExtra("type",2);
                intent.putExtra("tag",bannerData.getContent());
                break;
            case 3://标签
                intent=new Intent(context, TagActivity.class);
                intent.putExtra("tag",bannerData.getContent());
                break;
            case 4://专题
                intent=new Intent(context, ThemeListActivity.class);
                if(bannerData.getShortId().equals("")){//没有专题ID就切换到推荐页
//                    XMainActivity.stupToFrament(1);
                    intent=new Intent(context, ThemeListActivity.class);
                }else{//跳到专题页面
                    intent=new Intent(context, ThemeActivity.class);
                    intent.putExtra("shortId",bannerData.getShortId());//传进专题ID
                }
                break;
            case 5://电影名星 跳到女星界面

                break;
            case 6://排序方式
                break;
            case 7://类型?
                intent=new Intent(context, TypeActivity.class);
                intent.putExtra("shortId",bannerData.getShortId());
                intent.putExtra("type",7);
                intent.putExtra("tag",bannerData.getContent());
                break;
            case 8://浏览地址
                try {
                    if (!TextUtils.isEmpty(bannerData.getShortId())) {
                        intent = new Intent(Intent.ACTION_VIEW);//https://www.potato.im/1avco1
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(advUrl);
                        intent.setData(content_url);
                    }
                }catch (Exception ex){}
                finally {
                    sendToAdv(bannerData.getShortId());
                }
                break;
            case 9://下载地址 直接下载..  到浏览器打开下载
                try {
                    AdvBean advBean=new AdvBean();
                    advBean.setType(-99);
                    advBean.setContent(bannerData.getContent());
                    advBean.setShortId("");
                    advBean.setToUrl(advUrl);
                    isBoo=false;
                    systemDown(advBean,context);
                }catch (Exception ex){}

                break;
            case 10://发现
                intent=new Intent(context, FindActivity.class);
                intent.putExtra("tag","");
//                intent=new Intent(getContext(), GifFindActivity.class);
                break;
            case 11://跳一本道、东京热....
                intent=new Intent(context, MakeLTDActivity.class);
                intent.putExtra("shortId",bannerData.getShortId());
                break;
            case 12://跳转 webview 页面打开
                intent=new Intent(context, TWebActivity.class);
                intent.putExtra("toUrl",advUrl);
                break;
        }
        if(intent!=null){
            context.startActivity(intent);
        }
    }

    public static void to(Context context, HomeBean.HomeTypeItem homeTypeItem){
        Intent intent=null;
        if(context==null||homeTypeItem==null){
            return;
        }
        switch (homeTypeItem.getJump()){
            case 1://电影详情 banner 跳转？
//                intent=new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
                ViewUtils.toPlayer((Activity)context,null,homeTypeItem.getShortId(),null,null,null);
                return;
            case 2://类型
                intent=new Intent(context, TypeActivity.class);
                intent.putExtra("shortId",homeTypeItem.getShortId());
                intent.putExtra("type",2);
                intent.putExtra("tag",homeTypeItem.getName());

                //先测试
//                intent=new Intent(getContext(), MakeLTDActivity.class);
//                intent.putExtra("shortId","q");
                break;
            case 3://标签
                intent=new Intent(context, TagActivity.class);
                intent.putExtra("tag",homeTypeItem.getName());
                break;
            case 4://专题
                intent=new Intent(context, ThemeListActivity.class);
                if(homeTypeItem.getShortId().equals("")){//没有专题ID就切换到推荐页
//                    XMainActivity.stupToFrament(1);
                    intent=new Intent(context, ThemeListActivity.class);
                }else{//跳到专题页面
                    intent=new Intent(context, ThemeActivity.class);
                    intent.putExtra("shortId",homeTypeItem.getShortId());//传进专题ID
                }
                break;
            case 5://电影名星 跳到女星界面

                break;
            case 6://排序方式
                break;
            case 7://类型
                intent=new Intent(context, TypeActivity.class);
                intent.putExtra("shortId",homeTypeItem.getShortId());
                intent.putExtra("type",7);
                intent.putExtra("tag",homeTypeItem.getName());
                break;
            case 8://浏览地址
                if(!TextUtils.isEmpty(homeTypeItem.getShortId())){
                    intent = new Intent(Intent.ACTION_VIEW);//https://www.potato.im/1avco1
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(homeTypeItem.getShortId());
                    intent.setData(content_url);
                }
                sendToAdv(homeTypeItem.getShortId());
                break;
            case 9://下载地址 直接下载..  到浏览器打开下载
                if(!TextUtils.isEmpty(homeTypeItem.getShortId())){
                    intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(homeTypeItem.getShortId());
                    intent.setData(content_url);
                }
                sendToAdv(homeTypeItem.getShortId());
                break;
            case 10://发现
                intent=new Intent(context, FindActivity.class);
                intent.putExtra("tag","");
//                intent=new Intent(getContext(), GifFindActivity.class);
                break;
            case 11://跳一本道、东京热....
                intent=new Intent(context, MakeLTDActivity.class);
                intent.putExtra("shortId",homeTypeItem.getShortId());
                break;
            case 12://跳转 webview 页面打开
                intent=new Intent(context, TWebActivity.class);
                intent.putExtra("toUrl",homeTypeItem.getShortId());
                break;
        }
        if(intent!=null){
            context.startActivity(intent);
        }
    }

    private static boolean isBoo=false;
    //系统下载
    private static void systemDown(AdvBean advBean,Context context){
        try {
            if (isBoo && advBean.getType() == -99) {
                return;
            }
            sendToAdv(advBean.getShortId());

            final String sId=advBean.getShortId();

            isBoo = true;
            String baseUrl=advBean.getToUrl();
            long name=System.currentTimeMillis();
            Uri uri = Uri.parse(baseUrl);
            final DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
//            request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,name+".apk");
            request.setDestinationInExternalPublicDir("download", name+".apk");

            File file=Environment.getExternalStoragePublicDirectory("download");
            final String path=file.getAbsolutePath()+"/"+name+".apk";

            request.setDescription(advBean.getContent());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            // 获取此次下载的ID
            final long refernece = dManager.enqueue(request);
            // 注册广播接收器，当下载完成时自动安装
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            BroadcastReceiver receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (refernece == myDwonloadID && context != null) {
                        installApk(context,path);
                    }
                    toSendDownSuccess(sId);
                }
            };
            context.registerReceiver(receiver, filter);
            Toast.makeText(context, "即将开始下载", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){}
    }
    private static void installApk(Context context,String pathStr){
        try {
            InstallUtils.installAPK(context, pathStr,
                new InstallUtils.InstallCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("TAG", "打开安装中...");
                        //Toast.makeText(AppUpdateActivity.this, "安装程序成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        //tv_info.setText("安装失败:" + e.toString());
                        Log.e("TAG", "安装失败");
                    }
                });
        }catch (Exception ex){}
    }


    private static void sendToAdv(final String sId){
        //TODO
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory.getInstance().<String>clickedAdv(sId, new TCallback<String>() {
                    @Override
                    public void onNext(String s) {
                        Log.e("TAG",s);
                    }
                });
            }
        }).start();
    }

    //下载成功
    private static void toSendDownSuccess(final String shortId){
        //TODO...
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory.getInstance().<String>addown(shortId, new TCallback<String>() {
                    @Override
                    public void onNext(String s) {
                        Log.e("TAG",s);
                    }
                });
            }
        }).start();
    }
    //正在安装中.
    private static void toSendInstallSuccess(){
        //TODO...

    }
}
