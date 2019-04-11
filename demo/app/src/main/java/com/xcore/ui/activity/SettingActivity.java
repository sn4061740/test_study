package com.xcore.ui.activity;

import android.com.lockpattern.widget.ILockPatternListener;
import android.com.lockpattern.widget.LockPatternActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.TCallback;
import com.xcore.ext.wheelview.BottomDialog;
import com.xcore.ext.wheelview.WheelView;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.Config;
import com.xcore.ui.other.ModifyNickDialogView;
import com.xcore.ui.other.ModifyPassDialogView;
import com.xcore.utils.ACache;
import com.xcore.utils.AppUpdateUtils;
import com.xcore.utils.DateUtils;
import com.xcore.utils.DialogView;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private boolean guestPass;
    private ACache aCache;
    private byte[] gesturePassword;

    private ImageView imgCheckBox;

    ImageView playCacheImage;
    ImageView idleCacheImage;
    ImageView openMedio;
    ImageView onlyWifi;

    TextView qualityText;
    int qualityPos=0;

    Config config;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("设置");
        setEdit("");

        config=MainApplicationContext.getConfig();

        imgCheckBox=findViewById(R.id.img_checkbox);
        TextView txtVersion=findViewById(R.id.txt_current_version);
        String vStr="当前版本:"+SystemUtils.getVersion(this);
        txtVersion.setText(vStr);

        qualityText=findViewById(R.id.txt_quality);
        //初始化清晰度
        String cQuality=MainApplicationContext.CURRENT_QUALITY;
        qualityPos=Integer.valueOf(cQuality);
        qualityPos--;

        String qText=MainApplicationContext.QUALITY_LIST.get(cQuality);
        qualityText.setText(qText);

        findViewById(R.id.set_pass_layout).setOnClickListener(this);
        findViewById(R.id.set_user_protocol).setOnClickListener(this);
        findViewById(R.id.btn_toLogin).setOnClickListener(this);

        findViewById(R.id.set_clear_speace).setOnClickListener(this);
        findViewById(R.id.set_clear_speace).setVisibility(View.GONE);

//        findViewById(R.id.set_camera).setOnClickListener(this);
        findViewById(R.id.set_nickname).setOnClickListener(this);
        findViewById(R.id.set_pass_login).setOnClickListener(this);
        findViewById(R.id.set_select_quality).setOnClickListener(this);

        findViewById(R.id.btn_getting_update).setOnClickListener(this);

        playCacheImage=findViewById(R.id.img_play_cache);
        playCacheImage.setOnClickListener(this);

        idleCacheImage=findViewById(R.id.img_idle_cache);
        idleCacheImage.setOnClickListener(this);

        long time=MainApplicationContext.IDLE_TIMER;
        TextView idleTxt=findViewById(R.id.txt_idle_info);

        onlyWifi=findViewById(R.id.open_onlyWifi);
        onlyWifi.setOnClickListener(this);

        openMedio=findViewById(R.id.open_medio);
        openMedio.setOnClickListener(this);

        String missValue=DateUtils.getMiss(time);
        idleTxt.setText("空闲时自动下载("+missValue+"无操作)");

        //更新手势状态
        initCheckStatus();
        //更新播放状态
        updatePlayCacheStatus();
        //更新空闲下载状态
        updateIdleCacheStatus();
        //更新解码状态
        updateMediaStatus();
        //更新wifi下载状态
        updateOnlyWifi();
    }

    private void updateMediaStatus(){
        try {
            boolean mediaCodecEnabled = MainApplicationContext.mediaCodecEnabled;
            if (mediaCodecEnabled) {
                openMedio.setImageResource(R.drawable.checkbox_selected);
            } else {
                openMedio.setImageResource(R.drawable.checkbox_select);
            }
        }catch (Exception ex){}
    }
    private void updateOnlyWifi(){
        try {
            boolean onlyWifiDownEnabled = MainApplicationContext.onlyWifiDownBoo;
            if (onlyWifiDownEnabled) {
                onlyWifi.setImageResource(R.drawable.checkbox_selected);
            } else {
                onlyWifi.setImageResource(R.drawable.checkbox_select);
            }
        }catch (Exception ex){}
    }

    //修改用户名
    private void testNickname(){
        try {

//            Intent intent = new Intent(this, UpgradeUserInfoActivity.class);
//            startActivity(intent);
        }catch (Exception ex){}
    }

    @Override
    protected void initData() {
    }

    //设置手势状态
    private void initCheckStatus(){
        try {
            aCache = ACache.get(SettingActivity.this);
            //得到当前用户的手势密码
            gesturePassword = aCache.getAsBinary(android.com.lockpattern.util.ACache.GESTURE_PASSWORD);
            guestPass = gesturePassword != null;
            if (guestPass) {//有密码了 打开
                imgCheckBox.setImageResource(R.drawable.checkbox_selected);
            } else {//没有密码
                imgCheckBox.setImageResource(R.drawable.checkbox_select);
            }
        }catch (Exception ex){}
    }

    private void updatePlayCacheStatus(){
        try {
            boolean boo = MainApplicationContext.IS_PLAYING_TO_CACHE;
            int res = -1;
            if (boo == true) {
                res = R.drawable.checkbox_selected;
                CacheManager.getInstance().getLocalHandler().put(CacheManager.PLAY_IS_CACHE, "xxxxxxxx");
            } else {
                res = R.drawable.checkbox_select;
                CacheManager.getInstance().getLocalHandler().remove(CacheManager.PLAY_IS_CACHE);
            }
            playCacheImage.setImageResource(res);
        }catch (Exception ex){}
    }

    private void updateIdleCacheStatus(){
        boolean boo=MainApplicationContext.IS_IDLE_CACHE;
        int res=-1;
        if(boo==true){
            res=R.drawable.checkbox_selected;
            CacheManager.getInstance().getLocalHandler().put(CacheManager.IDLE_IS_CACHE,"xxxxxxxx");
        }else{
            res=R.drawable.checkbox_select;
            CacheManager.getInstance().getLocalHandler().remove(CacheManager.IDLE_IS_CACHE);
        }
        idleCacheImage.setImageResource(res);
    }

    ILockPatternListener lockPatternListener=new ILockPatternListener() {
        @Override
        public void onOpenSuccess() {//清除密码
            aCache.clear();
            initCheckStatus();
            //LockPatternActivity.toActivity(SettingActivity.this,lockPatternListener,LockPatternActivity.LockType.SET,true);
        }
        @Override
        public void onForgetPassword() {
            Log.e("TAG","忘记密码");
            aCache.clear();
            initCheckStatus();
        }
        @Override
        public void onSetSuccess() {//设置成功
            initCheckStatus();
        }
        @Override
        public void onNonePass() {
        }

        @Override
        public void onCancelOpenSuccess() {
        }

        @Override
        public void onCancelSetSuccess() {//取消设置

        }
    };
    private DialogView modifyDialogView;


    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.set_nickname:
                boolean pwdBoo=MainApplicationContext.toGuestLogin(SettingActivity.this);
                if(pwdBoo){
                    return;
                }
                new ModifyNickDialogView(SettingActivity.this).show();
                break;
            case R.id.set_pass_layout:
//                String guest=config.get("USER_GUEST");
//                if("1".equals(guest)){//是游客
//                    MainApplicationContext.showips("请先登录", SettingActivity.this, "toLogin");
//                    return;
//                }
                if(guestPass) {//有密码 要取消
                    LockPatternActivity.toActivity(SettingActivity.this,lockPatternListener,LockPatternActivity.LockType.OPEN_SET,true);
                }else{
                    //到设置手势去
                    LockPatternActivity.toActivity(SettingActivity.this,lockPatternListener,LockPatternActivity.LockType.SET,true);
                }
                break;
            case R.id.set_pass_login:
                boolean pwdBoo1=MainApplicationContext.toGuestLogin(SettingActivity.this);
                if(pwdBoo1){
                    return;
                }
                new ModifyPassDialogView(SettingActivity.this).show();
                break;
            case R.id.set_user_protocol:
                intent=new Intent(SettingActivity.this,ProtocolActivity.class);
                break;
            case R.id.btn_toLogin:
                MainApplicationContext.exitLogin(SettingActivity.this);
                break;
            case R.id.set_clear_speace:
                break;
            case R.id.img_play_cache://播放时缓存
                boolean boo1=MainApplicationContext.toGuestLogin(SettingActivity.this);
                if(boo1){
                    return;
                }
                boolean boo = MainApplicationContext.IS_PLAYING_TO_CACHE;
                MainApplicationContext.IS_PLAYING_TO_CACHE = !boo;
                updatePlayCacheStatus();
                break;
            case R.id.img_idle_cache://闲时缓存
                boolean boo3=MainApplicationContext.toGuestLogin(SettingActivity.this);
                if(boo3){
                    return;
                }
                boolean idleBoo = MainApplicationContext.IS_IDLE_CACHE;
                MainApplicationContext.IS_IDLE_CACHE = !idleBoo;
                updateIdleCacheStatus();
                break;
            case R.id.set_select_quality:
                showSelectQualty();
                break;
            case R.id.open_medio:
                boolean boo2=MainApplicationContext.mediaCodecEnabled;
                MainApplicationContext.mediaCodecEnabled=!boo2;
                if(!boo2){//开
                    CacheManager.getInstance().getLocalHandler().put("media_open","1");
                }else{//关
                    CacheManager.getInstance().getLocalHandler().put("media_open","0");
                }
                updateMediaStatus();
                break;
            case R.id.open_onlyWifi://仅仅wifi
                boolean onlyWifiDownBoo=MainApplicationContext.onlyWifiDownBoo;
                MainApplicationContext.onlyWifiDownBoo=!onlyWifiDownBoo;
                if(!onlyWifiDownBoo){//开
                    CacheManager.getInstance().getLocalHandler().put("only_wifi","1");
                }else{//关
                    CacheManager.getInstance().getLocalHandler().put("only_wifi","0");
                }
                updateOnlyWifi();
                break;
            case R.id.btn_getting_update://检查升级
                toast("获取版本信息");
                gettingVersion();
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }


    private boolean isNew=false;
    //更新版本
    private void gettingVersion(){
        if(isNew){
            toast("当前已是最新版本");
            return;
        }
        ApiSystemFactory.getInstance().<VersionBean>getVersion(new TCallback<VersionBean>() {
            @Override
            public void onNext(VersionBean versionBean) {
            if(versionBean!=null){
                VersionBean.VersionData versionData= versionBean.getData();
                if(versionData!=null){
                    int vCode=versionData.getInsideVersion();
                    int curCode=BaseCommon.VERSION_CODE;
                    if(curCode>=vCode){
                        isNew=true;
                        toast("当前已是最新版本");
                    }else {
                        String vStr=new Gson().toJson(versionData);
                        if(!isDestroyed()&&!isFinishing()){//当前还在
                            new AppUpdateUtils(SettingActivity.this,vStr);
                        }
                    }
                }
            }
            }
        },"");
    }

    private BottomDialog bottomDialog;
    //显示选择清晰度弹窗
    private void showSelectQualty(){
        View outerView1 = LayoutInflater.from(this).inflate(R.layout.dialog_select_quality, null);
        //日期滚轮
        final WheelView wv1 = (WheelView) outerView1.findViewById(R.id.wv1);
        Map<String,String> qList= MainApplicationContext.QUALITY_LIST;
        List<String> qualityList=new ArrayList<String>(qList.values());
        wv1.setItems(qualityList,qualityPos);

        //联动逻辑效果
        wv1.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index,String item) {
              Log.e("TAG",item+" position:"+index);
            }
        });

        TextView tv_ok = (TextView) outerView1.findViewById(R.id.tv_ok);
        TextView tv_cancel = (TextView) outerView1.findViewById(R.id.tv_cancel);
        //点击确定
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bottomDialog.dismiss();
                String mSelectDate = wv1.getSelectedItem();
                qualityPos=wv1.getSelectedPosition();
                String posStr=(qualityPos+1)+"";
                qualityText.setText(mSelectDate);
                MainApplicationContext.CURRENT_QUALITY=posStr;
                CacheManager.getInstance().getLocalHandler().put("quality_info",posStr);
            }
        });
        //点击取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bottomDialog.dismiss();
            }
        });
        //防止弹出两个窗口
        if (bottomDialog !=null && bottomDialog.isShowing()) {
            return;
        }

        bottomDialog = new BottomDialog(this, R.style.ActionSheetDialogStyle);
        //将布局设置给Dialog
        bottomDialog.setContentView(outerView1);
        bottomDialog.show();//显示对话框
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==100){
            //设置状态
            initCheckStatus();
            return;
        }
    }


    @Override
    public void onDestroy() {
        if(modifyDialogView!=null) {
            modifyDialogView.onDestroy();
        }
        modifyDialogView=null;
        super.onDestroy();
    }
}
