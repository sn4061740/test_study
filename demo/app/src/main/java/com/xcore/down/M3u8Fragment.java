package com.xcore.down;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.HttpServerManager;
import com.jay.config.DownConfig;
import com.jay.config.Status;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.sliderRecyclerView.BaseRecyclerViewAdapter;
import com.xcore.sliderRecyclerView.SlideRecyclerView;
import com.xcore.ui.other.ITipsListener;
import com.xcore.ui.other.TipsDialogView;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class M3u8Fragment extends BaseFragment {

    List<CacheModel> cacheModels=new ArrayList<>();
    boolean isRun;
    private SlideRecyclerView recyclerView;
    private M3u8Adapter adapter;
    private IM3u8Listener listener;
    TextView txtInfo;
    private LinearLayout contentLayout;
    int xPos=0;

    public void setListener(IM3u8Listener listener) {
        this.listener = listener;
    }

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    public void setCacheModels(List<CacheModel> cacheModels1){
        cacheModels=cacheModels1;
        for(int i=0;i<cacheModels.size();i++){
            CacheModel c= cacheModels.get(i);
            if(c!=null&&c.isSelected()){
                xPos=i;
                break;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_m3u8_down;
    }

    IM3u8Listener im3u8Listener=new IM3u8Listener() {
        @Override
        public void onComplete(int position) {
            final int pos=position;
            try {
                if (pos < cacheModels.size()) {
                    CacheModel cacheModel1 = cacheModels.remove(pos);
                    if (listener != null) {
                        listener.onComplete(position, cacheModel1);
                    }
                    for (CacheModel cacheModel : cacheModels) {
                        HttpServerManager.getInstance().removeListener(cacheModel.getTaskId());
                    }
                    adapter.setData(cacheModels);
                    updateStatus();
                }
            }catch (Exception ex){}
        }
        @Override
        public void onComplete(int position,CacheModel cacheModel) {
        }
    };
    public void delete(){
        for(CacheModel mod:cacheModels){
            HttpServerManager.getInstance().removeListener(mod.getTaskId());
        }
        update();
    }

    public void update(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                updateStatus();
            }
        });
    }
    public void updateStatus(){
            if(cacheModels.size()<=0){
                txtInfo.setVisibility(View.GONE);
                showNullLayout();
                contentLayout.setVisibility(View.GONE);
                return;
            }
            hideNullLayout();
            contentLayout.setVisibility(View.VISIBLE);
            txtInfo.setVisibility(View.VISIBLE);
            if(isRun){
                int maxLen=HttpServerManager.getInstance().getM3u8DownManager().getDownMax();
                txtInfo.setText("最大同时缓存数:"+maxLen+",当前缓存个数:"+cacheModels.size());
            }else{
                txtInfo.setText("已缓存个数:"+cacheModels.size()+"");
            }
    }

    private LinearLayout downStatus;

    @Override
    protected void initView(View view) {
        txtInfo= view.findViewById(R.id.txt_info);
        contentLayout=view.findViewById(R.id.contentLayout);
        downStatus=view.findViewById(R.id.down_status);
//        if(isRun){
//            downStatus.setVisibility(View.GONE);
//        }

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new M3u8Adapter(getActivity(), cacheModels,im3u8Listener);
        recyclerView.setAdapter(adapter);

        updateStatus();

        adapter.setOnDeleteClickListener(new M3u8Adapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, final int position) {
            try {
                new TipsDialogView(getActivity(),"删除后会清理本地缓存,确定要删除吗?",TipsEnum.TO_DELETE).setTipsListener(new ITipsListener() {
                    @Override
                    public void onSureSuccess() {
                        try {
                            CacheModel cacheModel = cacheModels.remove(position);
                            recyclerView.closeMenu();
                            DownConfig config=new DownConfig();
                            config.setDownUrl(cacheModel.getUrl());
                            HttpServerManager.getInstance().removeDown(config);
                            //adapter.notifyItemRangeRemoved(position,1);
//                            update();
                            adapter.setData(cacheModels);
                            updateStatus();

//                            if (cacheModel.getUrl().contains(".xv")) {
//                                List<com.xcore.cache.CacheModel> cList = new ArrayList<>();
//                                com.xcore.cache.CacheModel mod = new com.xcore.cache.CacheModel();
//                                mod.setShortId(cacheModel.getShortId());
//                                cList.add(mod);
//                                CacheManager.getInstance().getLocalDownLoader().deleteDb(cList);
//
//                                M3u8DownTaskManager.getInstance().deleteTask(cacheModel.getTaskId());
//                                return;
//                            }
//                            //删除数据库
//                            CacheManager.getInstance().getM3U8DownLoader().delete(cacheModel);
//                            M3u8DownTaskManager.getInstance().deleteTask(cacheModel.getTaskId());
                        }catch (Exception ex){}
                    }
                    @Override
                    public void onCancelSuccess() {
                        if(recyclerView!=null) {
                            recyclerView.closeMenu();
                        }
                    }
                    @Override
                    public void onOkSuccess() {
                    }
                }).show();
            }catch (Exception ex){}
            }
        });
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
            try {
                CacheModel cacheModel = cacheModels.get(position);
                if (cacheModel == null) {
                    return;
                }
                if(cacheModel.getPrent()>=100){
                    ViewUtils.toPlayer(getActivity(),null,cacheModel.getShortId(),"","0");
                    return;
                }

                DownConfig downConfig2=new DownConfig();
                downConfig2.setDownUrl(cacheModel.getUrl());
                if(cacheModel.getStatus()==Status.WAIT){//是等待状态
                    cacheModel.setStatus(Status.STOP);
                    HttpServerManager.getInstance().stopDown(downConfig2);
                }else if(cacheModel.getStatus()!=Status.RUN){
                    cacheModel.setStatus(Status.RUN);
                    downConfig2.setV1(cacheModel.getV1());
                    downConfig2.setRoot(MainApplicationContext.M3U8_PATH);
                    downConfig2.setThreadNum(3);
                    downConfig2.setmConver(cacheModel.getConver());
                    downConfig2.setmSize(cacheModel.getTotalSize());
                    downConfig2.setmName(cacheModel.getTitle());
                    downConfig2.setmId(cacheModel.getShortId());

                    HttpServerManager.getInstance().startDown(downConfig2);
                }else{
                    cacheModel.setStatus(Status.STOP);
                    HttpServerManager.getInstance().stopDown(downConfig2);
                }

//                M3U8TaskModel taskModel = M3u8DownTaskManager.getInstance().getM3u8TaskModel(cacheModel.getTaskId());
//                if(taskModel==null){
//                    return;
//                }
//                if(taskModel.getUrl().contains(".xv")||taskModel.getPrent()>=100||taskModel.getStatus() == M3U8TaskModel.TaskStatus.COMPLETE){
//                    ViewUtils.toPlayer(getActivity(), null, cacheModel.getShortId(), "", "0");
//                    return;
//                }
//                if (taskModel != null) {
//                    if (taskModel.getStatus() == M3U8TaskModel.TaskStatus.RUNNING||
//                            taskModel.getStatus() == M3U8TaskModel.TaskStatus.WAITING) {
//                        taskModel.stopTask();
//                        adapter.notifyItemChanged(position);
//                    } else if (taskModel.getStatus() != M3U8TaskModel.TaskStatus.RUNNING) {//没有在等待中的就开始
//                        if(cacheModels!=null&&cacheModels.size()>0){
//                            for(CacheModel cacheModel1:cacheModels){
//                                M3u8DownTaskManager.getInstance().stopTask(cacheModel1.getTaskId());
//                            }
//                        }
//                        boolean b=M3u8DownTaskManager.getInstance().addTask(taskModel);
//                        if(b==false){
//                            //Toast.makeText(getContext(),"超过最大任务数",Toast.LENGTH_SHORT).show();
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }
            }catch (Exception ex){}
            }
        });
        if(xPos>5) {
            recyclerView.scrollToPosition(xPos);
            LinearLayoutManager mLayoutManager =
                    (LinearLayoutManager) recyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(xPos, 0);
        }
    }

    @Override
    protected void initData() {

    }
}
