package android.com.baselibrary;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.Map;

public class MyApplication extends android.app.Application {
    protected String CHANNEL="";
    private Context context;
    public static boolean umengLog=true;
    @Override
    public void onCreate() {
        super.onCreate();

        //////////////////////////////////////////////////////////////
        ////////////////////////// 友盟统计 ///////////////////////////
        ////////////////////////////////////////////////////////////
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret   ikingUmeng:IK     Umeng:官网  AUmeng:IK Other01  DUmeng
         */
        //ADV_B  ADV_A   MM_A  修改版本 basecommon  SplashPresenter修改连接测试。。 token　验证到期时间
//        try {
//            UMConfigure.init(this, "5ba468bfb465f54b5d00016b", CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, null);
//
//            UMConfigure.setLogEnabled(true);
//            UMConfigure.setEncryptEnabled(true);
//
//            MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//            MobclickAgent.setSessionContinueMillis(3000);
//        }catch (Exception ex){}
    }
    public void init(Context ctx,String channel){
        this.context=ctx;
        CHANNEL=channel;
        try {
            Log.e("TAG","初始化Umeng"+CHANNEL);
            UMConfigure.init(context, "5ba468bfb465f54b5d00016b", CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, null);

            UMConfigure.setLogEnabled(true);
            UMConfigure.setEncryptEnabled(true);

            MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
            MobclickAgent.setSessionContinueMillis(3000);
        }catch (Exception ex){}
    }
    public void killProcess(){
        try {
            MobclickAgent.onProfileSignOff();
            MobclickAgent.onKillProcess(context);
        }catch (Exception e){}
        finally {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
    public void setUmengLog(boolean boo){
        umengLog=boo;
    }
    public static void toMobclickAgentEvent(Context context, String key, Map<String,String> data){
        MobclickAgent.onEvent(context, key, data);
    }
}
