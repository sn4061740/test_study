package com.xcore.ui.other;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.Config;
import com.xcore.utils.DialogView;

//修改密码
public class ModifyNickDialogView extends BaseDialogView {

    public ModifyNickDialogView(Context context){
        super(context);
    }
    public void show(){
        super.show();

        final DialogView dialogView= getDialogView();
        if(dialogView==null){
            return;
        }
        View sureBtn=dialogView.get(R.id.btn_sure);
        if(sureBtn!=null) {
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//更改密码
                 updatePass();
                }
            });
        }
        View cancelBtn=dialogView.get(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });
    }

    //更新密码
    private void updatePass(){
        final DialogView dialogView= getDialogView();
        EditText newNick=(EditText)dialogView.get(R.id.edit_nickname);
        String newNickStr=newNick.getText().toString().trim();
        if(newNickStr.length()<=0){
            toast("请输入昵称");
            return;
        }
        HttpParams params=new HttpParams();
        params.put("NickName",newNickStr);
        if(dialogView!=null){
            dialogView.showLoading();
        }
        ApiFactory.getInstance().<SpeedDataBean>updateUserInfo(params, new TCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                toast("修改昵称成功");
                onDestroy();
            }

            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                toast("修改昵称失败");
                if(dialogView!=null){
                    dialogView.hideLoading();
                }
            }
        });
        try {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(newNick.getWindowToken(), 0);
        }catch (Exception ex){}
    }
    private void toast(String msg){
        if(ctx==null)return;
        Toast toast=Toast.makeText(ctx,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void onDestroy(){
        super.onDestroy();
        ctx=null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_modify_nick;
    }
}
