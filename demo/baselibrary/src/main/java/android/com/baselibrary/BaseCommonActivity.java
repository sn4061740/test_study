package android.com.baselibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

public class BaseCommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onUMResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onUmPause();
    }

    public void onUMResume(){
        try {
            MobclickAgent.onResume(this);
        }catch (Exception ex){}
    }
    public void onUmPause(){
        try {
//        MobclickAgent.onPageEnd(className);
            MobclickAgent.onPause(this);
//        MobclickAgent.onPageEnd(className);
        }catch (Exception ex){}
    }

    public void toMobclickAgentEvent(Context context, String key){
        MobclickAgent.onEvent(context, key);
    }
    public void toMobclickAgentEvent(Context context, String key, Map<String,String> data){
        MobclickAgent.onEvent(context, key, data);
    }

    public void reportError(Context context, String errMsg){
        MobclickAgent.reportError(context,errMsg);
    }
    public void onProfileSignIn(String name){
        MobclickAgent.onProfileSignIn(name);
    }

}
