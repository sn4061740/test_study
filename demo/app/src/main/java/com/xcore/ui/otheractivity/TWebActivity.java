package com.xcore.ui.otheractivity;

import android.com.baselibrary.BaseCommonActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.bean.CdnBean;
import com.xcore.ui.Config;
import com.xcore.ui.activity.XMainActivity;
import com.xcore.ui.js.AndroidtoJs;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;

import java.util.List;

public class TWebActivity extends BaseCommonActivity {

    WebView webView;
    String toUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweb);

        Intent intent= getIntent();
        toUrl=intent.getStringExtra("toUrl");

        webView=findViewById(R.id.webView);

        initWebview();
    }

    private void initWebview(){
//        List<CdnBean.CdnDataItem> dataItems=  BaseCommon.ccList;
//        if(dataItems==null||dataItems.size()<=0){
//            return;
//        }
//        CdnBean.CdnDataItem item= dataItems.get(0);
        String xUrl=toUrl;//item.getUrl();
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

        webView.addJavascriptInterface(new AndroidtoJs(TWebActivity.this),"testAndroid");
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
            Toast.makeText(TWebActivity.this, message, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

}
