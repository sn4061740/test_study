package android.com.glide37;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;

public class GlideUtils {
    private GlideUtils(){}
    private IGlideListener glideListener;
    private static GlideUtils instance;
    public static GlideUtils getInstance(){
        if(instance==null){
            instance=new GlideUtils();
        }
        return instance;
    }

    public void init(IGlideListener iGlideListener){
        this.glideListener=iGlideListener;
    }

    public void load(Context context, String url, ImageView imageView){
        if(verify(url)){
            return;
        }
        final long stime=System.currentTimeMillis();
        Glide.with(context)
            .load(url).placeholder(R.drawable.default_load)
            .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if(glideListener!=null){
                        glideListener.onError(model,e,stime);
                    }
                    return false;
                }
                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            })
            .thumbnail(0.05f)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .skipMemoryCache(true)
            .dontAnimate()
            .dontTransform()
            .into(imageView);
    }

    //加载image 图片
    public void load(String url, ImageView img){
        if(verify(url)){
            return;
        }
        load(img.getContext().getApplicationContext(),url,img);
    }
    //加载webp 图片
    public void loadWebp(String url,ImageView img){
        if(verify(url)){
            return;
        }
        load(img.getContext(),url,img);
    }
    //加载webp 图片
    public void loadWebp(Context  context, String url,ImageView img){
        if(verify(url)){
            return;
        }
        load(context,url,img);
    }

    public void load(final String url, final ImageView img, final IGlideListener glideListener1){
        Glide.with(img.getContext()).load(url).thumbnail(0.1f).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if(img!=null){
                    img.setImageDrawable(resource);
                }
                if(glideListener1!=null){
                    glideListener1.onSuccess();
                }
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                if(glideListener1!=null){
                    glideListener1.onError(url,new Exception("加载出错"),0);
                }
            }
        });
    }

    public void loadGif(final String url, final ImageView img, final IGlideListener glideListener1){
        Glide.with(img.getContext()).load(url).asGif().into(new SimpleTarget<GifDrawable>() {
            @Override
            public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                if (img != null) {
                    img.setImageDrawable(resource);
                }
                if (glideListener1 != null) {
                    glideListener1.onSuccess();
                }
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                if(glideListener1!=null){
                    glideListener.onError(url,new Exception("加载出错"),0);
                }
            }
        });
    }

    //清除?
    public void clear(View view){
        Glide.clear(view);
    }

    //重新加载
    public void resumeRequests(Context context){
        if(context!=null) {
            Glide.with(context).resumeRequests();
        }
    }
    //暂停加载
    public void pauseRequests(Context context){
        if(context!=null) {
            Glide.with(context).pauseRequests();
        }
    }

    //清理内存
    public void onTrimMemory(Context context,int level){
        try {
            if (level == TRIM_MEMORY_UI_HIDDEN) {
                Glide.get(context).clearMemory();
            }
            Glide.get(context).trimMemory(level);
        }catch (Exception ex){}
    }
    //低内存时清理
    public void onLowMemory(Context context){
        try {
            Glide.get(context).clearMemory();
        }catch (Exception ex){}
    }

    //验证
    private boolean verify(String url){
        if(isEmpty(url)){
            if(glideListener!=null){
                Log.e("GlideUtils","加载图片地址为空");
                //glideErrorListener.onError(new Throwable("加载图片地址为空"));
            }
            return true;
        }
        return false;
    }
    private boolean isEmpty(String s){
        return s==null||s.length()<=0;
    }


    public void loadRadius(final String url, final ImageView img, final IGlideListener glideListener1){
        Glide.with(img.getContext().getApplicationContext())
            .load(url)
            .asBitmap()
            .transform(new GlideRoundTransform(img.getContext()))
            .thumbnail(0.1f)
            .listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    if(glideListener1!=null){
                        glideListener1.onError(url,new Exception("加载出错"),0);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if(glideListener1!=null){
                        glideListener1.onSuccess();
                    }
                    return false;
                }
            })
            .into(img);
    }

}
