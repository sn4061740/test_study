package com.xcore.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.Tag;
import com.xcore.ui.activity.TagActivity;

import java.util.List;

public class HomeTagAdapter extends BaseRecyclerAdapter<List<Tag>, HomeTagAdapter.ViewHoler> {

    public HomeTagAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public HomeTagAdapter.ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_tag, parent, false);
            return new HomeTagAdapter.ViewHoler(view);
        }catch (Exception ex){}
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeTagAdapter.ViewHoler holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder==null){
            return;
        }
        List<Tag> tags= dataList.get(position);
        int[] txtRes=new int[]{R.id.txt_0,R.id.txt_1,R.id.txt_2,R.id.txt_3};
        for(int i=0;i<txtRes.length;i++){
            TextView txt=holder.itemView.findViewById(txtRes[i]);
            Tag tag=tags.get(i);
            txt.setText(tag.getName());
            onClickItem(txt,tag);
        }
    }

    private void onClickItem(View view, final Tag tag){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, TagActivity.class);
                intent.putExtra("tag",tag.getShortId());
                mContext.startActivity(intent);
            }
        });
    }

    class ViewHoler extends RecyclerView.ViewHolder{

//        Button btnTag;
        public ViewHoler(View itemView) {
            super(itemView);
//            btnTag=itemView.findViewById(R.id.btnTag);
        }
    }
}
