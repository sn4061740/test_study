package android.com.lockpattern.widget;

import android.app.Activity;
import android.com.baselibrary.BaseCommonActivity;
import android.com.lockpattern.R;
import android.com.lockpattern.util.ACache;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.locks.Lock;

public class LockPatternActivity extends BaseCommonActivity {
    public enum LockType{
        NONE,
        OPEN,
        SET,
        OPEN_SET
    }
    static boolean isCallToClose=false;//回调后是否关闭当前,默认不关闭
    static LockType lockType=LockType.NONE;
    //默认回调
    static ILockPatternListener lockPatternListener=new ILockPatternListener() {
        @Override
        public void onOpenSuccess() {
            Log.e("TAG","解锁成功");
        }
        @Override
        public void onSetSuccess() {
            Log.e("TAG","设置锁成功");
        }
        @Override
        public void onNonePass() {
            Log.e("TAG","没有设置密码");
        }
        @Override
        public void onForgetPassword() {
            Log.e("TAG","忘记密码");
        }
        @Override
        public void onCancelOpenSuccess() {
            Log.e("TAG","取消打开");
        }
        @Override
        public void onCancelSetSuccess() {
            Log.e("TAG","取消设置");
        }
    };
    public static void toActivity(Activity context, ILockPatternListener iLockPatternListener, LockType type,boolean isClose){
        if(iLockPatternListener!=null) {
            lockPatternListener =iLockPatternListener;
        }
        isCallToClose=isClose;
        Intent intent=new Intent(context,LockPatternActivity.class);
        intent.putExtra("type",type.ordinal());
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG","销毁了...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pattern);

        findViewById(R.id.btn_setLock).setOnClickListener(onClickListener);
        findViewById(R.id.btn_openLock).setOnClickListener(onClickListener);

        Intent intent= getIntent();
        int type=intent.getIntExtra("type",-1);

        if(type==LockType.OPEN.ordinal()){
            lockType=LockType.OPEN;
//            intent=new Intent(LockPatternActivity.this,OpenLockPatternActivity.class);
//            startActivityForResult(intent,ACache.OPEN_RESULT);
            OpenLockPatternActivity.toActivity(LockPatternActivity.this,null);
        }else if(type==LockType.SET.ordinal()){
            lockType=LockType.SET;
            intent=new Intent(LockPatternActivity.this,SetLockPatternActivity.class);
            startActivityForResult(intent,ACache.SET_RESULT);
        }else if(type==LockType.OPEN_SET.ordinal()){
            lockType=LockType.OPEN_SET;
            OpenLockPatternActivity.toActivity(LockPatternActivity.this,"00");
        }
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent=null;
        if(v.getId()==R.id.btn_openLock){
            intent=new Intent(LockPatternActivity.this,OpenLockPatternActivity.class);
            startActivityForResult(intent,ACache.OPEN_RESULT);
        }else if(v.getId()==R.id.btn_setLock){
            intent=new Intent(LockPatternActivity.this,SetLockPatternActivity.class);
            startActivityForResult(intent,ACache.SET_RESULT);
        }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ACache.OPEN_RESULT&&resultCode==ACache.OPEN_RESULT){
            if(lockPatternListener!=null){
                lockPatternListener.onOpenSuccess();
            }
        }else if(requestCode==ACache.SET_RESULT&&resultCode==ACache.SET_RESULT){
            if(lockPatternListener!=null){
                lockPatternListener.onSetSuccess();
            }
        }else if(requestCode==ACache.OPEN_RESULT&&resultCode==ACache.OPEN_CANCEL_RESULT){
            if(lockPatternListener!=null){
                lockPatternListener.onCancelOpenSuccess();
            }
        }else if(requestCode==ACache.SET_RESULT&&resultCode==ACache.SET_CANCEL_RESULT){
            if(lockPatternListener!=null){
                lockPatternListener.onCancelSetSuccess();
            }
        }else if(requestCode==ACache.OPEN_RESULT&&resultCode==ACache.OPEN_NO_PASS){
            if(lockPatternListener!=null){
                lockPatternListener.onNonePass();
            }
        }else if(requestCode==ACache.OPEN_RESULT&&resultCode==ACache.OPEN_FORGET_PASS){
            if(lockPatternListener!=null){
                lockPatternListener.onForgetPassword();
            }
        }
        if(isCallToClose&&lockType!=LockType.NONE){
            finish();
        }
    }

}
