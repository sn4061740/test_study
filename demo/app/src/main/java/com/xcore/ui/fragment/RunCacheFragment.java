package com.xcore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownModel;
import com.xcore.ui.adapter.XRunAdapter;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.ChangeTotalLister;
import com.xcore.ui.touch.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RunCacheFragment extends BaseFragment implements ChangeTotalLister {
    private OnFragmentInteractionListener mListener;
    private LinearLayout bottomLayout;
    private TextView totalTxt;
    private RelativeLayout xLayout;
    private XRecyclerView recyclerView;

    private XRunAdapter runAdapter;

    private ImageView pauseImage;
    private TextView pauseTxt;

    private boolean isSelect=false;
    private boolean isEdit=false;

    private boolean isPlay=false;
    private Timer timer;

    protected LoadingDailog dialog=null;
    private static String shortId="";

    public RunCacheFragment() {

    }

    public static RunCacheFragment newInstance(String param1, String param2) {
        RunCacheFragment fragment = new RunCacheFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        shortId=param1;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_run_cache;
    }

    @Override
    protected void initView(View view) {
        hideNullLayout();
        pauseImage= view.findViewById(R.id.pauseImage);
        pauseTxt=view.findViewById(R.id.pauseTxt);

        xLayout=view.findViewById(R.id.xLayout);

        recyclerView=view.findViewById(R.id.runRecyclerView);

        runAdapter=new XRunAdapter(getActivity(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(runAdapter);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        totalTxt=view.findViewById(R.id.totalTxt);
        bottomLayout=view.findViewById(R.id.bottomLayout);
        Button btnSelectAll=view.findViewById(R.id.btn_selectAll);
        Button btnDelete= view.findViewById(R.id.btn_delete);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelect=!isSelect;
                runAdapter.selectAll(isSelect);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        onEdit(false);
        initData();
        if(shortId!=null&&shortId.length()>0) {
            int scrollerIndex=0;
            List<CacheModel> cList = runAdapter.dataList;
            for (int i = 0; i < cList.size(); i++) {
                if (cList.get(i).getShortId().equals(shortId)){
                    scrollerIndex=i;
                    break;
                }
            }
            if(scrollerIndex>0){
                recyclerView.scrollToPosition(scrollerIndex);
                LinearLayoutManager mLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                mLayoutManager.scrollToPositionWithOffset(scrollerIndex, 0);
            }
        }

        pauseImage.setImageResource(R.drawable.pause);
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseAll();
            }
        });
        pauseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseAll();
            }
        });
        startTimer();
    }

    //删除
    private void delete(){
        try {
            List<CacheModel> deleteList = new ArrayList<>();
            for (CacheModel cacheModel : cacheModels) {
                if (cacheModel.isChecked()) {
                    deleteList.add(cacheModel);
                }
            }
            CacheManager.getInstance().getLocalDownLoader().deleteCache(deleteList);// .getDownHandler().deleteRunTask(deleteList, 0);//batchDelete(deleteList);

            for (CacheModel item : deleteList) {
                CacheModel mod = getCacheModelByShortId(item.getShortId());
                if (mod != null) {
                    cacheModels.remove(mod);
                }
            }
        }catch (Exception e){}
        toChangeCount=0;
        initData();

        mListener.onItemClick(null);
        onEdit(false);

        if(isPlay==false){//显示
            pauseTxt.setText("全部开始");
            pauseImage.setImageResource(R.drawable.pause);
        }
    }
    //全部暂停或开始
    public void onPauseAll(){
        isPlay=!isPlay;
        if(isPlay==false){//开始->暂停   任务开始
            //停止所有的下载任务
            CacheManager.getInstance().getLocalDownLoader().stopRunAll();// .getDownHandler().stopRunAll();
        }else{
            CacheManager.getInstance().getLocalDownLoader().startAll();// getDownHandler().startRunAll();
        }
        updatePlay();
    }
    private void updatePlay(){
        if(dialog==null){
            LoadingDailog.Builder loadBuilder;
            loadBuilder=new LoadingDailog.Builder(getActivity())
                    .setMessage("请稍后...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            dialog=loadBuilder.create();
        }
        if(dialog!=null){
            dialog.show();
        }
        if(isPlay){//显示
            pauseTxt.setText("全部暂停");
            pauseImage.setImageResource(R.drawable.play);
        }else{
            pauseTxt.setText("全部开始");
            pauseImage.setImageResource(R.drawable.pause);
        }
        if(dialog!=null){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                if(dialog!=null){
                    dialog.cancel();
                }
                }
            },1000);
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
        if(msg.what==0){
           initData();
        }else if(msg.what==2){
            //更新状态UI

        }
        }
    };

    private int toChangeCount=0;
    private int tLen=0;
    List<CacheModel> cacheModels=new ArrayList<>();
    private CacheModel getCacheModelByShortId(String shortId){
        CacheModel mod=null;
        for(CacheModel item:cacheModels){
            if(item!=null&&item.getShortId().equals(shortId)){
                mod=item;
                break;
            }
        }
        return mod;
    }
    //初始数据
    public void initData(){
        try {
            int len = 0;
            //得到缓存信息
            Map<String, CacheModel> cahceModelMap = CacheManager.getInstance().getLocalDownLoader().rumMaps;// getDownHandler().runMaps;// runCaches;
            if (cahceModelMap != null && cahceModelMap.size() > 0) {
                for (CacheModel item : cahceModelMap.values()) {
                    CacheModel mod = getCacheModelByShortId(item.getShortId());
                    int percent = Integer.valueOf(item.getPercent());
                    if (item.getComplete() == 1 || percent >= 100) {
                        if (mod != null) {
                            cacheModels.remove(mod);
                        }
                        continue;
                    }
                    if (item.getStatus() == 1) {
                        len++;
                    }
                    if (mod == null) {
                        mod = new CacheModel();
                        mod.setShortId(item.getShortId());
                        mod.setConver(item.getConver());
                        mod.setUrl(item.getUrl());
                        mod.setName(item.getName());
                        cacheModels.add(mod);
                    }
                    mod.setBgSelected(false);
                    mod.setPercent(item.getPercent());
                    mod.setStatus(item.getStatus());
                    mod.setTotalCount1(item.getTotalCount1());
                    mod.setTotalSize(item.getTotalSize());
                    mod.setStreamId(item.getStreamId());
                    mod.setDownSize(item.getDownSize());
                    mod.setComplete(item.getComplete());
                    mod.setStreamInfo(item.getStreamInfo());
                    mod.setChecked(isSelect);
                    mod.setShowCheck(isEdit);
                    mod.setStreamInfo1(item.getStreamInfo1());

                    if(shortId!=null&&mod.getShortId().equals(shortId)){
                        mod.setBgSelected(true);
                    }
                }
            }
            if (tLen != cahceModelMap.size()) {
                toChangeCount = 0;
                //        过滤在map 里面没有的
                boolean isDelete = true;
                while (isDelete) {
                    boolean isF = false;
                    for (CacheModel c : cacheModels) {
                        CacheModel item = cahceModelMap.get(c.getShortId());
                        if (item == null) {
                            //删除
                            cacheModels.remove(c);
                            isF = true;
                            break;
                        }
                    }
                    if (isF == false) {
                        isDelete = false;
                    }
                }
                tLen = cacheModels.size();
            }
            if (runAdapter != null) {
                if (toChangeCount == 0) {
                    runAdapter.setData(cacheModels);
                    toChangeCount++;
                } else {
                    runAdapter.setData(cacheModels, 100);
                }
            }
            totalTxt.setText("同时缓存个数" + cacheModels.size());

            if(cacheModels!=null&&cacheModels.size()>0){
                hideErrorLayout();
                xLayout.setVisibility(View.VISIBLE);
            }else{
                showNullLayout();
                xLayout.setVisibility(View.GONE);
            }

            isPlay = len > 0;
            if (isPlay) {//显示
                pauseTxt.setText("全部暂停");
                pauseImage.setImageResource(R.drawable.play);
                //开始计时器
                //startTimer();
            } else {
                pauseTxt.setText("全部开始");
                pauseImage.setImageResource(R.drawable.pause);
            }
        }catch (Exception e){}
    }

    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }
    //开始计时器
    private void startTimer(){
        if(timer==null){
            Log.e("TAG","计时器启动...");
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                if(isEdit==false&&handler!=null) {//不在编辑状态的时候才刷新&&
                    handler.sendEmptyMessage(0);
                }
                }
            },1000,2000);
        }
    }
    //点击编辑
    public void onEdit(boolean boo){
        if(runAdapter.dataList==null||runAdapter.dataList.size()<=0){
            if(mListener!=null) {
                mListener.onItemClick(null);
            }
            if(bottomLayout!=null){
                bottomLayout.setVisibility(View.GONE);
            }
            return;
        }
        if(bottomLayout==null){
            isSelect=false;
            return;
        }
        isEdit=boo;
        try {
            if (boo) {
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
                isSelect=false;
            }
            if(runAdapter!=null) {
                runAdapter.showAll(boo);
            }
        }catch (Exception e){}
    }

    @Override
    public void onChangeTotal(int total) {
        try {
            if(runAdapter.dataList==null){
                toChangeCount=0;
                return;
            }
            CacheModel item = runAdapter.dataList.get(total);
            if (item == null) {
                return;
            }
            if (item.getStatus() == 1) {//下载状态 -> 暂停
                CacheManager.getInstance().getLocalDownLoader().stopTask(Arrays.asList(item));//  .getDownHandler().stopRunTask(item);// stopAll(item.getStreamId());// .stopByUrl(item,false);
                updatePlay();
            } else {
                DownModel downModel = new DownModel();
                downModel.setShortId(item.shortId);
                downModel.setName(item.getName());
                downModel.setConver(item.getConver());
                downModel.setUrl(item.getUrl());
                CacheManager.getInstance().downByUrl(downModel);

                isPlay = true;
                updatePlay();
            }
        }catch (Exception e){}
        finally {
            toChangeCount=0;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }


}
