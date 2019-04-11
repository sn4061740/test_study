package com.xcore;

import android.content.Context;
import android.util.Log;

import com.common.BaseCommon;
import com.jay.config.Md5Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiSystemFactory;
import com.xcore.tinker.TinkerManager;
import com.xcore.ui.IHotListener;
import com.xcore.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HotService {
    private Context ctx;
    private IHotListener hotListener;
    public HotService(Context context,IHotListener iHotListener){
        this.ctx=context;
        this.hotListener=iHotListener;
    }

    public void start(){
        ApiSystemFactory.getInstance().<VersionBean>getHotUpdateV1(new TCallback<VersionBean>() {
            @Override
            public void onNext(VersionBean s) {
                try {
                    versionData = s.getData();
                    if (versionData == null) {
                        if(hotListener!=null){
                            hotListener.onHotCancel();
                        }
                        return;
                    }
                    Integer vCode=versionData.getInsideVersion();
                    if(vCode==null){
                        if(hotListener!=null){
                            hotListener.onHotCancel();
                        }
                        return;
                    }
                    int hotVersion = BaseCommon.HOT_VERSION;
                    String versionName = BaseCommon.VERSION_NAME;
                    if (versionData.getInsideVersion() > hotVersion && versionData.getName().equals(versionName)) {
                        downHotUpdate();
                    }else{
                        if(hotListener!=null){
                            hotListener.onHotCancel();
                        }
                    }
                }catch (Exception ex){
                    if(hotListener!=null){
                        hotListener.onHotCancel();
                    }
                }
            }
            @Override
            public void onError(Response<VersionBean> response) {
                super.onError(response);
                Log.e("TAG","检查热更新失败");
                if(hotListener!=null){
                    hotListener.onHotCancel();
                }
            }
        });
    }
    VersionBean.VersionData versionData;
    List<String> aList=new ArrayList<>();
    private void downHotUpdate(){
        try {
            if (versionData == null) {
                if(hotListener!=null){
                    hotListener.onHotCancel();
                }
                return;
            }
            List<String> apkList = BaseCommon.apkLists;
            if (apkList == null || apkList.size() <= 0) {
                if(hotListener!=null){
                    hotListener.onHotCancel();
                }
                return;
            }
            for (String sUrl : apkList) {
                aList.add(sUrl);
            }
            startDown();
        }catch (Exception ex){
            if(hotListener!=null){
                hotListener.onHotCancel();
            }
        }
    }

    private void startDown(){
        if(versionData==null){
            if(hotListener!=null){
                hotListener.onHotCancel();
            }
            return;
        }
        String sUrl="";
        if(aList!=null&&aList.size()>0) {
            sUrl=aList.remove(0);
            String hotPath=MainApplicationContext.HOT_PATH;
            final String apkUrl=sUrl+versionData.getDownUrl();
            OkGo.<File>get(apkUrl).execute(new FileCallback(hotPath,versionData.getDownUrl()) {
                @Override
                public void onSuccess(Response<File> response) {
                    File file=response.body();
                    downFileSuccess(file);
                }
                @Override
                public void onError(Response<File> response) {
                    super.onError(response);
                    try {
                        int code = response.code();
                        String errMsg=LogUtils.getException(response.getException());
                        LogUtils.apiRequestError(apkUrl,errMsg,0,code);
                    }catch (Exception ex){}
                    finally {
                        startDown();
                    }
                }
            });
        }else{
            if(hotListener!=null){
                hotListener.onHotCancel();
            }
        }
    }

    private void downFileSuccess(File file){
        if(file==null){
            startDown();
            return;
        }
        //验证文件md5
        String md5=Md5Utils.getMD5(file);
        if(!md5.equals(versionData.getMd5())){
            startDown();
            return;
        }
        MainApplicationContext.hotPath=file.getAbsolutePath();
        TinkerManager.loadPatch(file.getAbsolutePath());
    }

}
