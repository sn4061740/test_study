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
public class ModifyPassDialogView {
    Context ctx;
    private DialogView dialogView;

    public ModifyPassDialogView(Context context){
        this.ctx=context;
    }
    public void show(){
        dialogView=new DialogView(ctx,null,R.layout.dialog_modify_pass);
        dialogView.show();
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
                if(dialogView!=null) {
                    dialogView.onDestroy();
                }
                dialogView=null;
            }
        });
    }

    //更新密码
    private void updatePass(){
        if(dialogView==null){
            return;
        }
        EditText oldPass=(EditText)dialogView.get(R.id.edit_oldPass);
        EditText newPass=(EditText)dialogView.get(R.id.edit_newPass);
        EditText reNewPass=(EditText)dialogView.get(R.id.edit_rePass);

        String oldStr=oldPass.getText().toString().trim();
        final String newStr=newPass.getText().toString().trim();
        String reNewStr=reNewPass.getText().toString().trim();
        if(oldStr.length()<=0){
            toast("请输入原始密码");
            return;
        }
        if(newStr.length()<=0){
            toast("请输入新密码");
            return;
        }
        if(newStr.length()<6){
            toast("密码最少6位");
            return;
        }
        if(!newStr.equals(reNewStr)){//两次输入的密码不一致
            toast("两次输入的密码不一致");
            return;
        }
        HttpParams params=new HttpParams();
        params.put("oldPwd",oldStr);
        params.put("newPwd",newStr);
        params.put("reNewPwd",newStr);
        if(dialogView!=null){
            dialogView.showLoading();
        }
        ApiFactory.getInstance().<SpeedDataBean>updateUserPass(params, new TCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                toast("修改密码成功");
                Config config=MainApplicationContext.getConfig();
                config.put("upass",newStr);
                config.save();
                onDestroy();
            }
            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                if(dialogView!=null){
                    dialogView.hideLoading();
                }
                toast("修改密码出错,稍后重试!");
            }
        });
        try {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow(reNewPass.getWindowToken(), 0);
        }catch (Exception ex){}
    }
    private void toast(String msg){
        if(ctx==null)return;
        Toast toast=Toast.makeText(ctx,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void onDestroy(){
        if(dialogView!=null){
            dialogView.hideLoading();
            dialogView.onDestroy();
        }
        ctx=null;
    }
}
