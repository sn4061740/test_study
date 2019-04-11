package com.xcore.ui.other;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.ui.LoginType;
import com.xcore.ui.activity.FeedbackRecodeActivity;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.UpgradeActivity;
import com.xcore.utils.DialogView;

//提示弹窗
public class TipsDialogView extends BaseDialogView{
    private DialogView dialogView;
    private TipsEnum tipsEnum=TipsEnum.TO_TIPS;
    private String content="";
    private ITipsListener tipsListener;

    public TipsDialogView setTipsListener(ITipsListener tipsListener) {
        this.tipsListener = tipsListener;
        return this;
    }

    public TipsDialogView(Context context, String contentStr, TipsEnum tipsEnum){
        super(context);
        this.content=contentStr;
        this.tipsEnum=tipsEnum;
    }
    public void show(){
        super.show();
        dialogView=getDialogView();
        initView();
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_tips;
    }

    private void init(){
        View viewLayout=dialogView.get(R.id.btnLayout);
        View viewLayout1=dialogView.get(R.id.btnLayout1);
        viewLayout.setVisibility(View.GONE);
        viewLayout1.setVisibility(View.GONE);

        TextView textView= (TextView) dialogView.get(R.id.content);
        textView.setText(content);
        switch (tipsEnum){
            case TO_CLEAR:
                viewLayout1.setVisibility(View.VISIBLE);
                Button btnSureClear= (Button) dialogView.get(R.id.btn_sure);
                if(btnSureClear!=null){
                    btnSureClear.setText("清理");
                }
                break;
            case TO_DELETE:
                viewLayout1.setVisibility(View.VISIBLE);
                Button btnSureDelete= (Button) dialogView.get(R.id.btn_sure);
                if(btnSureDelete!=null){
                    btnSureDelete.setText("确定");
                }
                break;
            case TO_SPREAD:
                viewLayout1.setVisibility(View.VISIBLE);
                Button btnSureDelete1= (Button) dialogView.get(R.id.btn_sure);
                if(btnSureDelete1!=null){
                    btnSureDelete1.setText("去推广");
                }
                break;
            case TO_TIPS:
                viewLayout.setVisibility(View.VISIBLE);
//                dialogView.get(R.id.btn_ok);
                break;
            case TO_LOGIN:
                viewLayout1.setVisibility(View.VISIBLE);
                Button btnSure= (Button) dialogView.get(R.id.btn_sure);
                if(btnSure!=null){
                    btnSure.setText("去登录");
                }
                break;
            case TO_FEED_BACK:
                viewLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
    //初始化点击事件
    private void initView(){
         View btnOk=dialogView.get(R.id.btn_ok);
         if(btnOk!=null){
             btnOk.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                 if(tipsEnum==TipsEnum.TO_FEED_BACK){
                     FeedbackRecodeActivity.toActivity(ctx);
                 }
                 onCancel();
                 }
             });
         }
         View btnCancel= (Button) dialogView.get(R.id.btn_cancel);
         if(btnCancel!=null){
             btnCancel.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     onCancel();
                 }
             });
         }
        View btnSure= (Button) dialogView.get(R.id.btn_sure);
        if(btnSure!=null){
            btnSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toTips();
                }
            });
        }
    }

    //点击确定,进行下一步操作
    private void toTips(){
        switch (tipsEnum){
            case TO_CLEAR://清除功能暂时没用
                dialogView.showLoading();
                //CacheManager.getInstance().getLocalDownLoader().clearPlayCache();
                //M3u8DownTaskManager.getInstance().clearSpeace();
//                if(tipsListener!=null){
//                    tipsListener.onSureSuccess();
//                }
                break;
            case TO_TIPS:
                if(tipsListener!=null){
                    tipsListener.onOkSuccess();
                }
                break;
            case TO_LOGIN:
                LoginActivity.toActivity((Activity) ctx,LoginType.LOGIN);
                if(tipsListener!=null){
                    tipsListener.onSureSuccess();
                }
                break;
            case TO_DELETE:
                if(tipsListener!=null){
                    tipsListener.onSureSuccess();
                }
                break;
            case TO_SPREAD:
                if(tipsListener!=null){
                    tipsListener.onSureSuccess();
                }
                UpgradeActivity.toActivity(ctx);
                break;
        }
        onCancel();
    }

    private void onCancel(){
        if(tipsListener!=null){
            tipsListener.onCancelSuccess();
        }
        if(dialogView!=null){
            dialogView.hideLoading();
        }
        if(dialogView!=null) {
            dialogView.onDestroy();
        }
    }
}
