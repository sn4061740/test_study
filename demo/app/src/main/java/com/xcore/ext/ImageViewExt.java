package com.xcore.ext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.xcore.utils.CacheFactory;

@SuppressLint("AppCompatCustomView")
public class ImageViewExt extends android.widget.ImageView {
    public ImageViewExt(Context context) {
        super(context);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageViewExt(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //加载图片
    public void loadUrl(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        Log.e("TAG","请求图片URL:"+url);
        CacheFactory.getInstance().getImage(getContext(),this,url);
    }
    public void loadGif(String url){
        CacheFactory.getInstance().getGifImage(this,url);
    }

    //加载图片
    public void load(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        CacheFactory.getInstance().getImage(getContext(),this,url);
    }
    //加载圆角图片
    public void loadRadius(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        CacheFactory.getInstance().getLoadRadius(this,url);
    }

}
