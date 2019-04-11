package com.xcore.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.common.BaseCommon;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.DataUtils;
import com.xcore.data.utils.TCallback;
import com.xcore.ext.ImageViewExt;
import com.xcore.services.ApiFactory;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.CacheFactory;

import cn.carbs.android.avatarimageview.library.AvatarImageView;


public class ActiveCodeActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_active_code;
    }

    ImageViewExt vCodeImage;
    EditText vCodeEdit;
    EditText activiTxt;
    Button btnSend;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        setTitle("激活码兑换");
        setEdit("");
        vCodeImage=findViewById(R.id.vCodeImage);
        vCodeEdit=findViewById(R.id.vCodeEdit);
        activiTxt=findViewById(R.id.activiTxt);
        btnSend=findViewById(R.id.btnSend);

        try {
            UserInfo info = DataUtils.playerBean.getData();
            if (info != null) {
                AvatarImageView avatarImageView= findViewById(R.id.item_avatar);
                CacheFactory.getInstance().getImage(avatarImageView,info.getHeadUrl());

                TextView nickTxt= findViewById(R.id.userNickTxt);
                nickTxt.setText(info.getNickName());
            }
        }catch (Exception ex){}
        String url= BaseCommon.API_URL+"home/GetYZM?t="+Math.random();
        vCodeImage.load(url);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        vCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url= BaseCommon.API_URL+"home/GetYZM?t="+Math.random();
                vCodeImage.load(url);
            }
        });
    }

    private void send(){
        String codeEdit=vCodeEdit.getText().toString();
        String actEdit=activiTxt.getText().toString();

        if(codeEdit.isEmpty()){
            toast("请输入验证码");
            return;
        }
        if(actEdit.isEmpty()){
            toast("请输入兑换码");
            return;
        }
        ApiFactory.getInstance().<String>actCode(actEdit,codeEdit, new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG",s);
                s=s.replace("\"","");
                MainApplicationContext.showips(s, null, TipsEnum.TO_TIPS);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","状态码："+response.code());
            }
        });
    }

    @Override
    protected void initData() {
    }

}
