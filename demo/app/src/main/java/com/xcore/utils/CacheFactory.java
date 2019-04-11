package com.xcore.utils;

import android.animation.ObjectAnimator;
import android.com.glide37.GlideUtils;
import android.com.glide37.IGlideListener;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.xcore.MainApplicationContext;
import com.xcore.ui.touch.ShareMovieListenner;

/**
 * 图片缓存类  请求全部从这里发出去
 */
public class CacheFactory{
    private CacheFactory(){
        GlideUtils.getInstance().init(glideErrorListener);
    }
    private static CacheFactory instance;
    public static CacheFactory getInstance(){
        if(instance==null){
            instance=new CacheFactory();
        }
        return instance;
    }
    //小于19 不用webp  >19 才用。。
    boolean getLoadingWebp(){
        boolean webpImageEnabled=MainApplicationContext.webpImageEnabled;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            webpImageEnabled=false;
        }
        return webpImageEnabled;
    }

    IGlideListener glideErrorListener=new IGlideListener() {
        @Override
        public void onSuccess() {
            Log.e("TAG","加载图片成功");
        }

        @Override
        public void onError(String model, Exception e, long stime) {
            final Throwable ex=e;
            String msg="";
            if(ex==null){
                msg="图片加载出错误,但是得到的错误异常是null,404 处理";
            }else {
                msg=LogUtils.getException(ex);
            }
            long endTime=System.currentTimeMillis();
            long eT=endTime-stime;
            try {
                if (msg != null && msg.length() > 0) {
                    LogUtils.imageUp(msg,model,eT,0);
                }
            }catch (Exception e1){
            }
        }
    };

    //加载图片
    public void getImage(final ImageView pic,String url){
        boolean webpImageEnabled=getLoadingWebp();//MainApplicationContext.webpImageEnabled;
        if(webpImageEnabled){
            String xUrl=url;
            xUrl=xUrl.replace(".jpg",".webp");
            xUrl=xUrl.replace(".png",".webp");
            GlideUtils.getInstance().loadWebp(xUrl,pic);
        }else{
            GlideUtils.getInstance().load(url,pic);
        }
    }
    //加载图片
    public void getImage(final Context context,final ImageView pic,String url){
        boolean webpImageEnabled=getLoadingWebp();//MainApplicationContext.webpImageEnabled;
        if(webpImageEnabled){
            String xUrl=url;
            xUrl=xUrl.replace(".jpg",".webp");
            xUrl=xUrl.replace(".png",".webp");
            GlideUtils.getInstance().loadWebp(xUrl,pic);
        }else{
            GlideUtils.getInstance().load(url,pic);
        }
    }

    //加载图片
    public void getImage(final ImageView pic, final String url, final ShareMovieListenner movieListenner){
        if(url==null||(url!=null&&url.equals("null"))){
            return;
        }
        GlideUtils.getInstance().load(url,pic,new IGlideListener(){
            @Override
            public void onError(String model, Exception e, long stime) {
                if(movieListenner!=null){
                    movieListenner.onError();
                }
            }
            @Override
            public void onSuccess() {
                if(movieListenner!=null){
                    movieListenner.onSuccess();
                }
            }
        });
    }

    public void getLoadRadius(final ImageView pic, final String url){
        GlideUtils.getInstance().loadRadius(url,pic,glideErrorListener);
    }

    //加载图片
    public void getGifImage(final ImageView pic,final String url){
        GlideUtils.getInstance().loadGif(url, pic, new IGlideListener() {
            @Override
            public void onError(String model, Exception e, long stime) {

            }

            @Override
            public void onSuccess() {

            }
        });
//        final ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
//            @Override
//            public void animate(View view) {
//                view.setAlpha( 0f );
//                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0f, 1f );
//                fadeAnim.setDuration( 250 );
//                fadeAnim.start();
//            }
//        };
//        LogUtils.showLog(url);
//        if(url==null||(url!=null&&url.equals("null"))){
//            return;
//        }
//        final long startTime=System.currentTimeMillis();
//        Glide.with(pic.getContext())
//                .load(url)
//                .asGif().thumbnail(0.5f)
////                .placeholder(R.drawable.network_load)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)//是否使用缓存
//                .animate(animationObject)
//                .fitCenter()
//                .listener(new RequestListener<String, GifDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
//                        String v=LogUtils.getException(e);
//                        LogUtils.showLog(v);
//                        return false;
//                    }
//                    @Override
//                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .into(pic);
    }
}
