package android.com.glide37;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GlideRequestLinster implements RequestListener<String,Drawable> {
    private IGlideListener glideListener;
    public GlideRequestLinster(IGlideListener iGlideListener){
        this.glideListener=iGlideListener;
    }
    @Override
    public boolean onException(Exception e, String model, Target<Drawable> target, boolean isFirstResource) {
        if(glideListener!=null){
            glideListener.onError(model,e,0);
        }
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, String model, Target<Drawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if(glideListener!=null){
            glideListener.onSuccess();
        }
        return false;
    }
}
