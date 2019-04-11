package com.xcore.ui.test;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;

import java.io.File;

//本地
public class LocalLinneController {

    private Activity activity;
    private String shortId;
    LineChangeListener lineChangeListener;

    public LocalLinneController(LineChangeListener changeListener,Activity activity1){
        this.lineChangeListener=changeListener;
        this.activity=activity1;
    }

    public void change(String shortId){
        this.shortId=shortId;
        CacheModel cacheModel = CacheManager.getInstance().getLocalDownLoader().getOverByShortId(shortId);
        if (cacheModel != null && cacheModel.getComplete() == 1) {
            String sourceUrl=cacheModel.getUrl();
            final String sId=cacheModel.getStreamId();
            String path="";
            try {
                //得到的路径格式：http://xx.com/xvcvccvcx.Torrent
                int lastIndex = sourceUrl.lastIndexOf("/");
                String nameStr = sourceUrl.substring(lastIndex + 1);
                nameStr = nameStr.replace(".Torrent", "");
                nameStr = nameStr.replace(".torrent", "");
                path = MainApplicationContext.SD_PATH + sId + "/" + nameStr + ".xv";

                File file=new File(path);
                if(file.exists()){
                    if(lineChangeListener!=null){
                        lineChangeListener.changeSuccess(path);
                        try {
                            Toast toas = Toast.makeText(activity, "本影片已缓存成功,不消耗流量!", Toast.LENGTH_SHORT);
                            toas.setGravity(Gravity.CENTER, 0, 0);
                            toas.show();
                        }catch (Exception ex){}
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

}
