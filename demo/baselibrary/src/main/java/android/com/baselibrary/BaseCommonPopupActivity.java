package android.com.baselibrary;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

public class BaseCommonPopupActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        try {
            MobclickAgent.onResume(this);
        }catch (Exception ex){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPause(this);
        }catch (Exception ex){}
    }
}
