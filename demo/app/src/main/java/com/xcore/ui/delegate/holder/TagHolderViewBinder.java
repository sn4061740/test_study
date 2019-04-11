package com.xcore.ui.delegate.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.commonAdapter.ItemViewBinder;
import com.xcore.data.bean.Tag;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.adapter.HomeTagAdapter;
import com.xcore.ui.decorations.GridDividerItemDecoration;
import com.xcore.ui.delegate.TagItem;

import java.util.ArrayList;
import java.util.List;

public class TagHolderViewBinder extends ItemViewBinder<TagItem,TagHolderViewBinder.TagHolder> {
    Context mContext;

    public TagHolderViewBinder(Context ctx){
        this.mContext=ctx;
    }

    @Override
    protected void onViewRecycled(@NonNull TagHolderViewBinder.TagHolder holder) {
        super.onViewRecycled(holder);

    }

    @NonNull
    @Override
    protected TagHolderViewBinder.TagHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.holder_tag, parent, false);
        return new TagHolderViewBinder.TagHolder(root);
    }
    HomeTagAdapter homeTagAdapter;

    @Override
    protected void onBindViewHolder(@NonNull final TagHolderViewBinder.TagHolder holder, @NonNull final TagItem item) {
        try {
            List<Tag> tagList=item.getTags();
            homeTagAdapter=new HomeTagAdapter(mContext);
            holder.tagRecyclerView.setLayoutManager(new GridLayoutManager(mContext,1));
//            holder.tagRecyclerView.addItemDecoration(new GridDividerItemDecoration(2,mContext.getResources().getColor(R.color.color_black)));
            holder.tagRecyclerView.setAdapter(homeTagAdapter);

            List<List<Tag>> tags=splitList(tagList,4);
            homeTagAdapter.setData(tags);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    //拆分成4个一组
    private List<List<Tag>> splitList(List<Tag> list , int groupSize){
        int length = list.size();
        // 计算可以分成多少组
        int num = ( length + groupSize - 1 )/groupSize ; // TODO
        List<List<Tag>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i+1) * groupSize < length ? ( i+1 ) * groupSize : length ;
            newList.add(list.subList(fromIndex,toIndex)) ;
        }
        return  newList ;
    }
    class TagHolder extends RecyclerView.ViewHolder{

        RecyclerView tagRecyclerView;
        public TagHolder(View v) {
            super(v);
            tagRecyclerView=v.findViewById(R.id.tagRecyclerView);
        }
    }

}