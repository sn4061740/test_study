package com.xcore.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.LoginPresenter;
import com.xcore.presenter.view.LoginView;
import com.xcore.ui.Config;
import com.xcore.ui.LoginController;
import com.xcore.ui.LoginType;
import com.xcore.utils.AppUpdateUtils;
import com.xcore.utils.SystemUtils;

public class LoginActivity extends BaseActivity implements View.OnTouchListener,View.OnClickListener {
    public static int RECODE_ID=8;
    public static int SUCCESS_ID=88;

    private boolean isShowPass=false;
    EditText loginPassword;
    EditText loginAccount;
    CheckBox recodePass;
    Button registerBtn;
    Button btnLogin;

    LoginController loginController;

    private static LoginType loginType=LoginType.LOGIN;
    public static void toActivity(Activity activity, LoginType type){
        loginType=type;
        try {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            if (type == LoginType.EXIT_LOGIN || type == LoginType.FORGET_PASS) {
                activity.finish();
            }
        }catch (Exception ex){}
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(loginType==LoginType.LOGIN) {//除了 LOGIN 其他进这里的都是要进主页面的
            loginController = new LoginController(LoginActivity.this, true);
        }else{
            loginController = new LoginController(LoginActivity.this, false);
        }

        loginPassword = findViewById(R.id.edit_loginPassword);
        loginAccount = findViewById(R.id.edit_loginAccount);
        recodePass = findViewById(R.id.recodePass);
        registerBtn = findViewById(R.id.btn_loginRegister);
        btnLogin = findViewById(R.id.btn_login);

        Drawable drawable = getResources().getDrawable(R.drawable.login_user);
        drawable.setBounds(5, 0, 65, 60);
        loginAccount.setCompoundDrawables(drawable, null, null, null);

        Drawable drawable1 = getResources().getDrawable(R.drawable.login_mima);
        drawable1.setBounds(5, 0, 65, 60);

        Drawable drawable5 = getResources().getDrawable(R.drawable.login_look);
        drawable5.setBounds(5, 0, 80, 45);
//        Drawable drawable5=loginPassword.getCompoundDrawables()[2];
        loginPassword.setCompoundDrawables(drawable1, null, drawable5, null);

        //点击登录
        btnLogin.setOnClickListener(this);
        //点击注册
        registerBtn.setOnClickListener(this);
        //显示隐藏密码
        loginPassword.setOnTouchListener(this);
        recodePass.setVisibility(View.GONE);

        Config config= MainApplicationContext.getConfig();
        String uname=config.get("uname");
        String fCode=config.getKey();// SystemUtils.getFingerprint();
        if(!uname.equals(fCode)){//不是游客,显示账号
            loginAccount.setText(config.get("uname"));
        }
        //如果是退出登录或token 过期,则把其他页面都清除了,数据重新设置
        if(loginType!=LoginType.LOGIN){
            //删除 token 相关信息
            config.remove(config.TOKEN);
            config.remove(config.REFRESH_TOKEN);
            config.remove(config.TOKEN_EXPIRES_IN);
            config.remove(config.CURRENT_EXPIRES_IN);
            //删除用户信息
            config.remove("uname");
            config.remove("upass");
            MainApplicationContext.removeActivityBy(LoginActivity.this);
        }
    }

    @Override
    protected void initData() {
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//是退出或在登录界面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(loginType==LoginType.EXIT_LOGIN||loginType==LoginType.TOKEN_LOGIN||loginType==LoginType.FORGET_PASS) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    MainApplicationContext.finishAllActivity();
                }
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_loginRegister://注册 //有返回的跳转 把注册的用户名和密码传回来
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,RECODE_ID);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
            case R.id.btn_login:
                toLogin();
                break;
        }
    }

    public void toLogin(){
        //判断用户名密码
        String uname=loginAccount.getText().toString().trim();
        String upass=loginPassword.getText().toString().trim();
        loginController.toLogin(uname,upass);
        //判断密码规则
        //presenter.getLogin(uname, upass, true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断请求码和返回码是不是正确的，这两个码都是我们自己设置的
        if (requestCode == RECODE_ID && resultCode == SUCCESS_ID) {
            String name = data.getStringExtra("uname");// 拿到返回过来的输入的账号
            String pwd = data.getStringExtra("upass");// 拿到返回过来的输入的账号
            // 把得到的数据显示到输入框内 注册的不用记住密码

            loginAccount.setText(name);
            loginPassword.setText(pwd);
            toLogin();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable drawable = loginPassword.getCompoundDrawables()[2];
        //如果右边没有图片，不再处理
        if (drawable == null)
            return false;
        //如果不是按下事件，不再处理
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > loginPassword.getWidth()
                - loginPassword.getPaddingRight()
                - drawable.getIntrinsicWidth()){
            if(isShowPass==false) {
                HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
                loginPassword.setTransformationMethod(method);
                isShowPass=true;
            }else {
                TransformationMethod method =  PasswordTransformationMethod.getInstance();
                loginPassword.setTransformationMethod(method);
                isShowPass=false;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        loginController=null;
        super.onDestroy();
    }
}
