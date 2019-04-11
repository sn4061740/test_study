package com.xcore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.ui.activity.PVideoViewActivity88;

/**
 * 生成 VIEW
 */
public class ViewUtils {
    public static float dpToPx(Context context,float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static TextView getText(Context context,String txt, int res){
        TextView textView=new TextView(context);
        textView.setText(txt);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setPadding((int)dpToPx(context,10),
                (int)dpToPx(context,2),
                (int)dpToPx(context,10),
                (int)dpToPx(context,2));
        textView.setBackgroundResource(res);
        textView.setMaxEms(7);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(context.getResources().getColor(R.color.item_txt_color));
        return textView;
    }

    private static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
    /**
     * 设置图片颜色
     */
    public static void setImageColor(ImageView img,ColorStateList colorStateList){
        Drawable src =img.getDrawable();
        img.setImageDrawable(tintDrawable(src,colorStateList));
    }


    public static void toPlayer(Activity activity, View view,String shortId,String playerUrl,String position,String streamId){
        try {
            Intent intent = new Intent(activity, PVideoViewActivity88.class);
            intent.putExtra("shortId", shortId);
            if (!TextUtils.isEmpty(playerUrl)) {
                intent.putExtra("playUrl", playerUrl);
            }
            if (!TextUtils.isEmpty(streamId)) {
                intent.putExtra("streamId", streamId);
            }
            if (!TextUtils.isEmpty(position)) {
                intent.putExtra("position", Integer.valueOf(position));
            }
            activity.startActivity(intent);
        }catch (Exception ex){}
    }
    public static void toPlayer(Activity activity, View view,String shortId,String playerUrl,String position){
        try {
            Intent intent = new Intent(activity, PVideoViewActivity88.class);
            intent.putExtra("shortId", shortId);
            if (!TextUtils.isEmpty(playerUrl)) {
                intent.putExtra("playUrl", playerUrl);
            }
            if (!TextUtils.isEmpty(position)) {
                intent.putExtra("position", Integer.valueOf(position));
            }
            activity.startActivity(intent);
        }catch (Exception ex){}
    }

    public static String replaceStr(String str){
        String v="";
        if(str.length()<2){
            return str;
        }
        if(str.length()<6){
            v=str.substring(2);
        }else {
            v = str.substring(2, str.length() - 2);
        }
        String s="";
        for(int i=0;i<v.length();i++){
            s+="*";
            if(s.length()>6){
                break;
            }
        }
        v=str.replace(v,s);
        return v;
    }

}
