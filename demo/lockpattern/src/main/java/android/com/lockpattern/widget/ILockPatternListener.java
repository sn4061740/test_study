package android.com.lockpattern.widget;

public interface ILockPatternListener {
    void onOpenSuccess();//解锁成功
    void onSetSuccess();//设置成功
    void onNonePass();//没有设置密码
    void onForgetPassword();//忘记密码

    void onCancelOpenSuccess();//取消打开
    void onCancelSetSuccess();//取消设置
}
