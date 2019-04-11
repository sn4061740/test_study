package com.xcore.ui.test;

import com.common.BaseCommon;
import com.xcore.MainApplicationContext;
import com.xcore.ui.Config;

import java.util.ArrayList;
import java.util.List;

//http 切换
public class HttpLineController {

    private List<String> httpLines=new ArrayList<>();
    private LineChangeListener lineChangeListener;

    public HttpLineController(LineChangeListener changeListener){
        this.lineChangeListener=changeListener;
        httpLines=new ArrayList<>();
    }
    //切换
    public void change(String souceUrl){
        souceUrl=souceUrl.replace(".Torrent",".xv");
        if(BaseCommon.HTTP_URL==null||BaseCommon.HTTP_URL.length()<=0){
            Config config=MainApplicationContext.getConfig();//从本地赋值
            String xUrl=config.get("HTTP_URL");
            if(xUrl.length()<=0){//还未读取到地址
                if(lineChangeListener!=null){
                    lineChangeListener.changeError();
                }
                return;
            }
            BaseCommon.HTTP_URL=xUrl;
        }
        String path=BaseCommon.HTTP_URL+souceUrl;
        if(httpLines.contains(path)){
            path="";
        }
        if(path.length()<=0){
            List<String> httpAccels= BaseCommon.httpAccelerateLists;
            if(httpAccels!=null&&httpAccels.size()>0){
                for(String baseUrl:httpAccels){
                    String newPath=baseUrl+souceUrl;
                    if(httpLines.contains(newPath)){
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
                httpLines.add(path);
                lineChangeListener.changeSuccess(path);
            }
        }
    }

}
