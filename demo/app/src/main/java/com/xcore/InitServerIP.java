package com.xcore;

import android.util.Log;

import com.common.BaseCommon;
import com.xcore.utils.IPUtils;

import java.util.List;

import cn.dolit.DLBT.DolitBT;

public class InitServerIP {

    public void start(){//播放速度不上传的话 这个日志也不上传,这里也没必要加了
        boolean playSpeedLogBoo=MainApplicationContext.playSpeedLog;
        if(playSpeedLogBoo==false){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
            List<String> httpList=BaseCommon.httpAccelerateLists;
            MainApplicationContext.httpIpAndUrl="";
            for(int i=0;i<httpList.size();i++){
                String url=httpList.get(i);
                String ipStr= IPUtils.getIP(url);
                if(i==0) {
                    MainApplicationContext.httpIpAndUrl += url + ":" + ipStr;
                }else{
                    MainApplicationContext.httpIpAndUrl += "|"+url + ":" + ipStr;
                }
                Log.e("TAG","得到IP:"+ipStr);
                MainApplicationContext.AddessIp(ipStr);
            }
            }
        }).start();
    }

}
