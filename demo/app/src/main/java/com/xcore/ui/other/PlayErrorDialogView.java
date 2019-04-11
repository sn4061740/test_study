package com.xcore.ui.other;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.Config;
import com.xcore.utils.DialogView;
import com.xcore.utils.NetWorkUtils;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayErrorDialogView extends BaseDialogView {

    public interface IPlayErrorDialogListener{
        void onSuccess();
        void onError();
    }

    IPlayErrorDialogListener playErrorDialogListener;

    public PlayErrorDialogView setPlayErrorDialogListener(IPlayErrorDialogListener playErrorDialogListener) {
        this.playErrorDialogListener = playErrorDialogListener;
        return this;
    }

    public PlayErrorDialogView(Context context, String playUrl1, String sId) {
        super(context);
        this.playUrl=playUrl1;
        this.shortId=sId;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_play_error_ui;
    }

    private String playUrl="";
    private String shortId="";

    private List<Integer> resList= Arrays.asList(R.id.radioBtn0,R.id.radioBtn1,R.id.radioBtn2);
    private List<String> resStr=Arrays.asList("无法播放","播放慢","其他");
    private List<ImageView> imgList=new ArrayList<>();

    public void show(){
        super.show();
        final DialogView dialogView= getDialogView();
        if(dialogView==null){
            return;
        }
        for(Integer i:resList){
            ImageView img= (ImageView) dialogView.get(i);
            imgList.add(img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapImage(v);
                }
            });
        }

        final EditText editText= (EditText) dialogView.get(R.id.edit_content);
        View sureBtn=dialogView.get(R.id.btn_sure);
        if(sureBtn!=null) {
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//更改密码
                    String content=editText.getText().toString();
                    sendMessage(content);
                }
            });
        }
        View cancelBtn=dialogView.get(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });
    }

    private int currentIndex=-1;
    private void tapImage(View v){
        int ix=-1;
        for(ImageView img:imgList){
            ix++;
            if(v==img){
                img.setImageResource(R.drawable.radio_checked);
                currentIndex=ix;
            }else{
                img.setImageResource(R.drawable.radio_check);
            }
        }
    }

    //发送请求
    private void sendMessage(String msg){
        if (currentIndex == -1) {
            Toast.makeText(ctx, "请选择一个类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if(msg==null||msg.length()<=0){
            Toast.makeText(ctx, "请输入反馈内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String typeStr = resStr.get(currentIndex);
        String msgStr = "SOURCE_ID=" + shortId + "|URL=" + playUrl + "|ERROR_CONTENT=" + msg + "TYPE=" + typeStr + "|INFO_ID=人工提交";
        int v1 = NetWorkUtils.getNetworkState(ctx);
        msg += "|NETWORK=" + v1;
        if (v1 > 1) {
            String v2 = NetWorkUtils.getOperatorName(ctx);
            if (TextUtils.isEmpty(v2)) {
                v2 = "未获取到运营商信息";
            }
            msg += "|NETWOORK_NAME=" + v2;
        }
        msg += "|网络状态说明(0:没有网络,1:wifi,2:2G,3:3G,4:4G,5:手机流量,运营商http://baike.baidu.com/item/imsi)";

        HttpParams params = new HttpParams();
        params.put("ResponseCode", 10001);
        params.put("RequestAddress", playUrl);
        params.put("ResponseTime", 0);
        params.put("RequestMethod", "POST");
        params.put("RequestParameter", msgStr);
        Config config=MainApplicationContext.getConfig();
        String fCode =config.getKey();// SystemUtils.getFingerprint();
        params.put("onlyCode", fCode);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new TCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                if(playErrorDialogListener!=null){
                    playErrorDialogListener.onSuccess();
                }
                onDestroy();
            }
            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                if(playErrorDialogListener!=null){
                    playErrorDialogListener.onError();
                }
                onDestroy();
            }
        });
    }

}
