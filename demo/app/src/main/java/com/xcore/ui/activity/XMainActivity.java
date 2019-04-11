package com.xcore.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.common.BaseCommon;
import com.jwsd.libzxing.OnQRCodeListener;
import com.jwsd.libzxing.QRCodeManager;
import com.xcore.HeartTest;
import com.xcore.InitServerIP;
import com.xcore.JavaSocketTest;
import com.xcore.JavaSpeedTest;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CdnBean;
import com.xcore.tinker.CustomerTinkerLike;
import com.xcore.ui.Config;
import com.xcore.ui.fragment.HomeFragment100;
import com.xcore.ui.fragment.RecommendFragment;
import com.xcore.ui.fragment.ReferralFragment;
import com.xcore.ui.fragment.SelfCenterFragment1;
import com.xcore.ui.fragment.TypeFragment1;
import com.xcore.ui.js.AndroidtoJs;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.VideoCheckUtil;
import com.xcore.utils.ViewUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMainActivity extends BaseActivity {
    private WebView webView;
    private static ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private static List<Fragment> fragments=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_xmain;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        webView=findViewById(R.id.webView);
        Log.e("TAG","main...");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.black_1A));
                switch (item.getItemId()) {
                    case R.id.item_news:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_lib:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_find:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.item_more:
                        toolbar.setBackgroundColor(getResources().getColor(R.color.color_1f1711));
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
                }
            });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
        init();
        onScanQR(null);
        //打开webview 页面
        initWebview();
    }
    private HeartTest heartTimer;
    private void init(){
        try {
            if(MainApplicationContext.context==null){
                MainApplicationContext.context=XMainActivity.this.getApplicationContext();
            }

            //标识本次登录是否退出过，初始化设置未退出过
            MainApplicationContext.isExitLogin = false;
            MainApplicationContext.removeActivityBy(this);

            //MainApplicationContext.initHttpServer();
            //M3u8Utils.getInstance().startServer();

            //启动服务
            //MainApplicationContext.startP2PServer();

            //设置http服务器IP
            new InitServerIP().start();

            //测试文件速度
            new JavaSpeedTest().start();

            //检测域名信息
            new JavaSocketTest().start();

            //初始化缓存
            //CacheManager.getInstance().initDown();

            //检查视频相关的判断
            VideoCheckUtil.init();
            setGuide();

            //开始心跳
            MainApplicationContext.startHeart();
        }catch (Exception ex){}
        try{
            Config config=MainApplicationContext.getConfig();
            String uname=config.get("uname");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uname", uname);
            toMobclickAgentEvent(XMainActivity.this, "login_success", map);
        }catch (Exception e){
            //e.printStackTrace();
        }
        try {
            //初始化本地缓存信息
            CacheManager.getInstance().init();
            //初始化缓存
            CacheManager.getInstance().initDown();
        }catch (Exception ex){}
    }
    private void setGuide(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//            }
//            //新手引导
//            GuideUtil.show(XMainActivity.this,XMainActivity.this.getSupportFragmentManager());
//            }
//        }).start();
    }
    @Override
    protected void initData() {

    }
    //扫描、识别监听
    public void onScanQR(View view) {
        QRCodeManager.getInstance()
            .with(this)
//            .setReqeustType(0)
//                .setRequestCode(1001)
            .scanningQRCode(new OnQRCodeListener() {
                @Override
                public void onCompleted(String result) {
                    //LogUtils.showLog("(结果)"+ result);
                    if(!TextUtils.isEmpty(result)) {
                        Uri uri = Uri.parse(result);
                        String sid = uri.getQueryParameter("sid");
                        if (sid != null && sid.length() > 0) {
                            ViewUtils.toPlayer(XMainActivity.this, null, sid, "", "0", null);
                        } else {
                            String codeStr=uri.getQueryParameter("code");
                            String cStr=uri.getQueryParameter("sharecode");
                            if(codeStr!=null&&codeStr.length()>0){
                                toast("推广码:"+codeStr);
                            }else if(cStr!=null&&cStr.length()>0){
                                toast("推广码:"+cStr);
                            }else {
                                toast(result);
                            }
                        }
                    }else{
                        if(result!=null) {
                            toast(result);
                        }else{
                            toast("未能识别该二维码信息");
                        }
                    }
                }
                @Override
                public void onError(Throwable errorMsg) {
//                    LogUtils.showLog("(错误)" + errorMsg.toString());
                    toast("未能识别该二维码信息");
                }

                @Override
                public void onCancel() {
                    //LogUtils.showLog("(取消)扫描任务取消了");
                }
                /**
                 * 当点击手动添加时回调
                 *
                 * @param requestCode
                 * @param resultCode
                 * @param data
                 */
                @Override
                public void onManual(int requestCode, int resultCode, Intent data) {
                    LogUtils.showLog("点击了手动添加了");
                }

            });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            LogUtils.showLog("走了这里了" + requestCode + "---" + resultCode);
            //注册onActivityResult
            QRCodeManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data);
        }catch (Exception ex){}
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        HomeFragment100 indexFragment= HomeFragment100.newInstance("","");
        fragments.add(indexFragment);

        RecommendFragment recommendFragment= RecommendFragment.newInstance("","");
//        ReferralFragment referralFragment=new ReferralFragment();

        fragments.add(recommendFragment);

        TypeFragment1 blankFragment= TypeFragment1.newInstance("","");
        fragments.add(blankFragment);

        SelfCenterFragment1 selfCenterFragment= new SelfCenterFragment1();
        fragments.add(selfCenterFragment);

        adapter.addFragment(indexFragment);
        adapter.addFragment(recommendFragment);
        adapter.addFragment(blankFragment);
        adapter.addFragment(selfCenterFragment);
        viewPager.setAdapter(adapter);
    }
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplicationContext.finishAllActivity();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
    public static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static void disableShiftMode(BottomNavigationView navigationView) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);

                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                    itemView.setShiftingMode(false);
                    itemView.setChecked(itemView.getItemData().isChecked());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomerTinkerLike.isMain=true;
        Log.e("TAG","Main onResume...");
    }

    @Override
    public void onPause() {
        super.onPause();
        CustomerTinkerLike.isMain = true;
//        Activity activity=MainApplicationContext.getLastActivity();
//        if(activity!=null&&activity!=this) {
//            CustomerTinkerLike.isMain = false;
//            Log.e("TAG","不是主界面");
//        }
//        Log.e("TAG","Main onPause..");
    }
    @Override
    public void onDestroy() {
        if(heartTimer!=null) {
            heartTimer.destroy();
        }
        Log.e("TAG","MAIN  Destroy...");
        if(webView!=null){
            webView.destroy();
        }
        webView=null;
        super.onDestroy();
    }


    public void initWebview(){
       List<CdnBean.CdnDataItem> dataItems=  BaseCommon.ccList;
       if(dataItems==null||dataItems.size()<=0){
           return;
       }
        CdnBean.CdnDataItem item= dataItems.get(0);
        String xUrl=item.getUrl();
        webView=findViewById(R.id.webView);
        Config config=MainApplicationContext.getConfig();
        String fCode=config.getKey();//SystemUtils.getFingerprint();
        if(xUrl.indexOf("?")>=0){//有问号
            xUrl+="&data=";
        }else{
            xUrl+="?data=";
        }
        xUrl+=fCode+"&t="+System.currentTimeMillis();
        final String vUrl=xUrl;

        webView.addJavascriptInterface(new AndroidtoJs(XMainActivity.this),"testAndroid");
        webView.loadUrl(vUrl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            // 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                return true;
                //开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
            }
            // 当每一个页面加载出来时的动作 可以获取当前页面的信息 如URL 如标题等
            @Override
            public void  onPageStarted(WebView view, String url, Bitmap favicon) {
                //设定加载开始的操作
                // 如可以得到当前的URL
                //current_url=view.getUrl();
                Log.e("TAG","加载开始了...");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
                Log.e("TAG","加载完成了...");
//                String call = "javascript:alertMessage(\"" + "content" + "\")";
//                webView.loadUrl(call);
            }
            //加载页面的服务器出现错误时（如404）调用 使用自定义的错误界面 更符合软件的整体设计风格
            //步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
            //步骤2：将该html文件放置到代码根目录的assets文件夹下
            //步骤3：复写WebViewClient的onRecievedError方法
            //该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                try {
                    Log.e("TAG", "出错了...onReceivedError(WebView view, int errorCode, String description, String failingUrl)");
                    LogUtils.apiRequestError(failingUrl, description, 0, errorCode);
                }catch (Exception ex){}
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                try {
                    Log.e("TAG", "出错了...onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        int code = error.getErrorCode();
                        String errDesc = (String) error.getDescription();
                        LogUtils.apiRequestError(vUrl, "WEB_C" + errDesc, 0, code);
                    }
                }catch (Exception ex){}
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                //SDK 》21 的才会进这里
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        int statusCode = errorResponse.getStatusCode();
                        String errMsg = errorResponse.getReasonPhrase();
                        errMsg = "WEB_C错误,错误消息:" + errMsg;
                        Log.e("TAG", errMsg);
                        LogUtils.apiRequestError(vUrl, errMsg, 0, statusCode);
                    }
                }catch (Exception ex){}
            }
            //处理https请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
                Log.e("TAG","HTTPS 出错？..");
            }
        });
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d("test", cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId() );
            return true;
        }
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //Toast.makeText(WebviewActivity.this, message, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

}
