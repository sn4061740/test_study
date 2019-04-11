package com.xcore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.ui.Config;
import com.xcore.ui.activity.GuideActivity;
import com.xcore.ui.activity.NoticeTipsPopupActivity;

public class GuideUtil {
    public static String GUIDE_KEY="guide_password";

    public static void show(Activity activity, FragmentManager fragmentManager){
        try {
            Config config=MainApplicationContext.getConfig();
            String value=config.get(GUIDE_KEY);
            //判断是否要显示引导
            if(!TextUtils.isEmpty(value)){
                //跳转到详情页面
                String shortId=MainApplicationContext.SHORT_ID;
                if(shortId!=null&&shortId.length()>0){
                    ViewUtils.toPlayer(activity,null,shortId,"","","");
                }
                MainApplicationContext.getNotice();
                return;
            }
            GuideActivity.toActivity(activity);
            config.put(GUIDE_KEY,"1");
            config.save();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
