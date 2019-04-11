package com.xcore.ui;

import android.content.Context;

import com.google.gson.Gson;
import com.jay.config.AESUtils;
import com.jay.config.FileUtil;
import com.xcore.MainApplicationContext;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private Context ctx;
    private Map<String,String> configMap=new HashMap<>();

    public String TOKEN="token";
    public String REFRESH_TOKEN="refresh_token";
    public String TOKEN_EXPIRES_IN="token_expires_in";
    public String CURRENT_EXPIRES_IN="token_current_expires";
    public String ONLY_KEY="only_key";

    private boolean CHANGE=false;

    public Config(Context context){
        this.ctx=context;
        get();
    }

    //添加 key - value
    public void put(String key,String value){
        configMap.put(key,value);
        CHANGE=true;
    }

    public String getKey(){
        String v=get(ONLY_KEY);
        if(v.length()<=0){
            v=SystemUtils.getFingerprint();
            configMap.put(ONLY_KEY,v);
            CHANGE=true;
            save();
        }
        return v;
    }
    //获取 key - value
    public String get(String key){
        String value=configMap.get(key);
        return (value==null||value.length()<=0)?"":value;
    }
    //删除
    public void remove(String key){
        configMap.remove(key);
        CHANGE=true;
    }
    //保存到本地
    public void save(){//没有改变过的就不保存了
        if(ctx==null||!CHANGE)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                //Log.e("TAG","保存信息");
                String configStr=new Gson().toJson(configMap);
                //加密存储
                byte[] bytes=AESUtils.aesEncrypt(configStr);
                if(bytes==null){//加密失败
                    return;
                }
                //把bytes 写入本地文件中
//                String fileName = ctx.getExternalCacheDir() + "/config.ini";
                String fileName=MainApplicationContext.DATABASE_PATH+"/config.ini";
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(bytes);
                outputStream.close();
            }catch (Exception ex){}
            finally {
                CHANGE=false;
            }
            }
        }).start();
    }
    //获取
    public void get(){//1.3.7  51 1热更版本   52 官网版本后加入
        if(ctx==null)return;
        String fileName = ctx.getExternalCacheDir() + "/config.ini";
        String v=MainApplicationContext.DATABASE_PATH+"/config.ini";
        LogUtils.showLog("初始化信息》。。。。");
        File file = new File(v);
        if (!file.exists()) {
            File f1=new File(fileName);
            if(!f1.exists()){//都没有就算了。。
                return;
            }else{//如果cache 下面有则复制到files 下面去
                try {
                    FileUtil.copyFile(f1, file);
                }catch (Exception ex){}
            }
        }
        try {
            if(!file.exists()){
                return;
            }
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = in.read(buffer)) >= 0) {
                out.write(buffer, 0, r);
            }
            String result=AESUtils.aesDecrypt(out.toByteArray());
            if(result.length()>0){
                configMap=new Gson().fromJson(result,Map.class);
            }
            //LogUtils.showLog(result);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
