package com.xcore.ui.activity;

import android.com.baselibrary.MyApplication;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.BaseBean;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.DataUtils;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.LogUtils;
import com.xcore.utils.QRCodeUtils;
import com.xcore.utils.SystemUtils;

import java.util.HashMap;

public class SpreadShareActivity extends BaseActivity{
    String qcodeStr="";
    String qrcodeUrl="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_spread_share;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("推广分享");
        setEdit("");

        ImageView imageView=findViewById(R.id.qrecode);
        TextView mTextView=findViewById(R.id.infoTxt);

        TextView txt_shareCode=findViewById(R.id.txt_shareCode);

        PlayerBean playerBean= DataUtils.playerBean;
        if(playerBean!=null) {
            UserInfo userInfo = playerBean.getData();
            mTextView.setText(userInfo.getShareText());
            qcodeStr=userInfo.getShareUrl();
            qrcodeUrl=userInfo.getQrcodeUrl();
            txt_shareCode.setText(userInfo.getShareCode());
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //跳网页
                Intent intent=new Intent( Intent.ACTION_VIEW );
                intent.setData( Uri.parse( qrcodeUrl) ); //这里面是需要调转的rul
                intent = Intent.createChooser( intent, null );
                startActivity(intent);
                return false;
            }
        });
        try {
//            Resources res = getResources();
//            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
            //生成二维码
//            QRCodeUtils.qrCode(qrcodeUrl, imageView, 200, 200);
            Bitmap bitmap= QRCodeUtils.createQRCodeBitmap(qrcodeUrl,220);//QRCodeManager.getInstance().createQRCode(qrcodeUrl, 200, 200);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){}
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveQrcode();
                }catch (Exception e){}
            }
        });
        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            boolean boo=SystemUtils.copy(qcodeStr,SpreadShareActivity.this);
            if(boo){
                toast("复制成功,快去分享吧!!!");
                try {//点击复制分享 发送事件
                    HashMap<String, String> map = new HashMap<String, String>();
                    PlayerBean playerBean = DataUtils.playerBean;
                    if (playerBean != null) {
                        UserInfo userInfo = playerBean.getData();
                        map.put("share_code", userInfo.getShareCode());
                    }
                    MyApplication.toMobclickAgentEvent(SpreadShareActivity.this, "share_success", map);
//                    MobclickAgent.onEvent();
                }catch (Exception e){}
            }
            }
        });
    }
    private void saveQrcode(){
        try {
            SystemUtils.captureScreen(SpreadShareActivity.this);
            ApiFactory.getInstance().<BaseBean>saveShare(new TCallback<BaseBean>() {
                @Override
                public void onNext(BaseBean s) {
                    LogUtils.showLog(s.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {//点击保存  发送事件
            HashMap<String, String> map = new HashMap<String, String>();
            PlayerBean playerBean = DataUtils.playerBean;
            if (playerBean != null) {
                UserInfo userInfo = playerBean.getData();
                map.put("share_code", userInfo.getShareCode());
            }
//            MobclickAgent.onEvent(SpreadShareActivity.this, "save_share_success", map);
            MyApplication.toMobclickAgentEvent(SpreadShareActivity.this, "save_share_success", map);
        }
    }

    @Override
    protected void initData() {
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
//    }
}
