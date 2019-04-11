package com.xcore.ui.test;

import com.common.BaseCommon;
import com.xcore.MainApplicationContext;
import com.xcore.ui.Config;

import java.util.ArrayList;
import java.util.List;

import cn.dolit.p2ptrans.P2PTrans;

//m3u8 切换
public class M3u8LineController {
    private List<String> m3u8Lines=new ArrayList<>();
    private LineChangeListener lineChangeListener;

    public M3u8LineController(LineChangeListener changeListener){
        this.lineChangeListener=changeListener;
        m3u8Lines=new ArrayList<>();
    }
    public void change(String sourceUrl1,String md5){
        String sourceUrl=sourceUrl1;
        if(BaseCommon.M3U8_URL==null||BaseCommon.M3U8_URL.length()<=0){
            Config config=MainApplicationContext.getConfig();//从本地赋值
            String xUrl=config.get("M3U8_URL");
            if(xUrl.length()<=0){//还未读取到地址
                if(lineChangeListener!=null){
                    lineChangeListener.changeError();
                }
                return;
            }
            BaseCommon.M3U8_URL=xUrl;
        }
        String path=BaseCommon.M3U8_URL+sourceUrl;
        if(m3u8Lines.contains(path)){
            path="";
        }
        if(path.length()<=0){
            List<String> m3u8List= BaseCommon.m3u8List;
            if(m3u8List!=null&&m3u8List.size()>0){
                for(String baseUrl:m3u8List){
                    String newPath=baseUrl+sourceUrl;
                    if(m3u8Lines.contains(newPath)){
                        continue;
                    }
                    path=newPath;
                    break;
                }
            }
        }
        if(path.length()<=0){
            if(lineChangeListener!=null){
                lineChangeListener.changeError();
            }
        }else{
            if(lineChangeListener!=null){
                m3u8Lines.add(path);
                path=P2PTrans.getM3u8PlayUrl(path,md5);
                lineChangeListener.changeSuccess(path);
            }
        }
    }
    //停止
    public void onDestroy(){
//        try {
//            String url = "http://127.0.0.1:" + M3u8Utils.getInstance().getPort() + "/stop";
//            OkGo.<String>get(url).execute(new StringCallback() {
//                @Override
//                public void onSuccess(Response<String> response) {
//                    String v = response.body();
//                    LogUtils.showLog(v);
//                }
//                @Override
//                public void onError(Response<String> response) {
//                    super.onError(response);
//                }
//            });
//        }catch (Exception ex){}
    }

}
