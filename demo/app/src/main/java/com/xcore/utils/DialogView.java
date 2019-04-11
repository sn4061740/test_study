package com.xcore.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tu.loadingdialog.LoadingDailog;
import com.xcore.R;

/**
 * 自定义dialog
 */
public class DialogView {

    protected Context ctx;
    private View view;
    AlertDialog alertDialog;
    protected LoadingDailog dialog;

    public DialogView(Context context,ViewGroup parent,int res){
        this.ctx=context;
        view=LayoutInflater.from(ctx).inflate(res,parent,false);
    }
    public View getView(){
        return view;
    }

    //显示
    public void show(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            // 设置提示框的标题
            builder.setView(view);//设置取消按钮,null是什么都不做，并关闭对话框
            alertDialog = builder.create();// 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setWindowAnimations(R.style.fade);
            // 显示对话框
            alertDialog.show();
        }catch (Exception ex){}
    }

    public View get(int id){
        return view.findViewById(id);
    }
    //
    public void showLoading(){
        try {
            if (dialog == null) {
                LoadingDailog.Builder loadBuilder;
                loadBuilder = new LoadingDailog.Builder(ctx)
                        .setMessage("加载中...")
                        .setCancelable(true)
                        .setCancelOutside(true);
                dialog = loadBuilder.create();
            }
            dialog.show();
        }catch (Exception ex){}
    }
    public void hideLoading(){
        if(dialog!=null){
            dialog.cancel();
        }
    }

    //销毁
    public void onDestroy(){
        if(alertDialog!=null){
            alertDialog.dismiss();
        }
        alertDialog=null;
    }

}
