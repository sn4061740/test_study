package com.xcore.ui.other;

import android.content.Context;

import com.xcore.utils.DialogView;

public abstract class BaseDialogView {
    protected Context ctx;
    private DialogView dialogView;

    public BaseDialogView(Context context){
        this.ctx=context;
    }

    public DialogView getDialogView() {
        return dialogView;
    }

    public void show(){
        int res=getLayoutId();
        if(res<=0){
            return;
        }
        try {
            if (ctx != null) {
                dialogView = new DialogView(ctx, null, res);
                dialogView.show();
            }
        }catch (Exception ex){}
    }
    public void onDestroy(){
        if(dialogView!=null){
            dialogView.hideLoading();
            dialogView.onDestroy();
        }
        ctx=null;
    }

    protected abstract int getLayoutId();
}
