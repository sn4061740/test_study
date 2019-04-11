package com.xcore.down;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import com.jay.HttpServerManager;
import com.jay.config.Config;
import com.jay.config.Status;
import com.jay.down.M3u8DownManager;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.ui.other.TipsDialogView;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class M3u8CacheActivity extends BaseActivity {
    final List<Fragment> fragments = new ArrayList<>();
    private TabLayout tabLayout;
    private CustomScrollViewPager mViewPager;
    private TextView txtDisk;
    private String shortId="";

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, M3u8CacheActivity.class);
        context.startActivity(intent);
    }
    public static void toActivity(Context context,boolean isRun,String sId) {
        Intent intent = new Intent(context, M3u8CacheActivity.class);
        intent.putExtra("isRun",isRun);
        intent.putExtra("shortId",sId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cache_m3u8;
    }

    private boolean isRun = false;

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        setEdit("左滑删除");
        TextView editView=findViewById(R.id.edit_txt);
        if(editView!=null){
            editView.setText("往左滑删除");
            editView.setTextSize(10);
        }
        setTitle("我的缓存");

        Intent intent = getIntent();
        isRun = intent.getBooleanExtra("isRun", false);
        shortId=intent.getStringExtra("shortId");

        txtDisk=findViewById(R.id.txt_disk);
        mViewPager = findViewById(R.id.mViewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setEnabled(false);

        TextView speaceTxt=findViewById(R.id.clear_speace);
        findViewById(R.id.clear_speace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TipsDialogView(M3u8CacheActivity.this,"确定清理空间吗",TipsEnum.TO_CLEAR).show();
            }
        });
        speaceTxt.setVisibility(View.GONE);
        findViewById(R.id.spaceLayout).setVisibility(View.GONE);

        //updateStatus();
        initData();
    }

    private void updateStatus(){
        long memorySize = SystemUtils.getAvailableInternalMemorySize();
        final String valStr = SystemUtils.FormetFileSize(memorySize);

        String sPath = MainApplicationContext.M3U8_PATH;
        final String fStr = SystemUtils.getAutoFolderOrFileSize(sPath);
        txtDisk.post(new Runnable() {
            @Override
            public void run() {
                txtDisk.setText("已用空间:" + fStr + " 可用空间:" + valStr);
            }
        });
    }

    M3u8Fragment runFragment;
    M3u8Fragment overFragment;
    List<CacheModel> overList = new ArrayList<>();
    List<CacheModel> runList = new ArrayList<>();

    @Override
    protected void initData() {
        try {
            M3u8DownManager m3u8DownManager = HttpServerManager.getInstance().getM3u8DownManager();
            if (m3u8DownManager == null) {
                return;
            }
            Map<String, Config> configs = m3u8DownManager.getConfigs();
            List<CacheModel> runningList = new ArrayList<>();
            for (Config config : configs.values()) {
                CacheModel cacheModel = new CacheModel();
                cacheModel.setTitle(config.getmName());
                cacheModel.setPrent(config.getProgress());
                cacheModel.setUrl(config.getmUrl());
                cacheModel.setConver(config.getmConver());
                cacheModel.setTaskId(config.gettId());
                cacheModel.setShortId(config.getmId());
                cacheModel.setTotalSize(config.getmSize());
                cacheModel.setCurSize(config.getcSize());
                cacheModel.setStatus(config.getStatus());
                cacheModel.setV1(config.getKey());
                if (cacheModel.getShortId().equals(shortId)) {
                    cacheModel.setSelected(true);
                }
                if (cacheModel.getPrent() >= 100) {
                    cacheModel.setStatus(Status.COMPLETE);
                    overList.add(cacheModel);
                } else {
                    runningList.add(cacheModel);
//                if(model.getStatus()==M3U8TaskModel.TaskStatus.RUNNING){
//                    runningList.add(cacheModel);
//                }else {
//                    runList.add(cacheModel);
//                }
                }
            }
            runList.addAll(0, runningList);
            runFragment = new M3u8Fragment();
            runFragment.setCacheModels(runList);
            runFragment.setListener(im3u8Listener);
            runFragment.setIsRun(true);
            fragments.add(runFragment);

            overFragment = new M3u8Fragment();
            overFragment.setCacheModels(overList);
            overFragment.setIsRun(false);
            fragments.add(overFragment);

            ViewFragmentAdapter adapter =
                    new ViewFragmentAdapter(getSupportFragmentManager(), fragments,
                            Arrays.asList("正在缓存", "已缓存"));
            mViewPager.setAdapter(adapter);

            if (isRun) {
                mViewPager.setCurrentItem(1);
            }
        }catch (Exception ex){}
    }

    IM3u8Listener im3u8Listener=new IM3u8Listener() {
        @Override
        public void onComplete(int postion) {
//            CacheModel cacheModel=runList.remove(postion);
//            cacheModel.setStatus(Status.COMPLETE);
//            overList.add(cacheModel);
//
//            HttpServerManager.getInstance().removeListener(cacheModel.getTaskId());
//
////            runFragment.setCacheModels(runList);
//            overFragment.setCacheModels(overList);
//            overFragment.update();
            //runFragment.updateStatus();
//            runFragment.update();
        }
        @Override
        public void onComplete(int position,CacheModel cacheModel) {

                CacheModel cModel = new CacheModel();
                cModel.setStatus(Status.COMPLETE);
                cModel.setConver(cacheModel.getConver());
                cModel.setTitle(cacheModel.getTitle());
                cModel.setCurSize(cacheModel.getTotalSize());
                cModel.setPrent(100);
                cModel.setShortId(cacheModel.getShortId());
                cModel.setTotalSize(cacheModel.getTotalSize());
                cModel.setUrl(cacheModel.getUrl());
                cModel.setTaskId(cacheModel.getTaskId());
                overList.add(cModel);

            if (overFragment != null) {
                overFragment.setCacheModels(overList);
                overFragment.update();
            }
        }
    };

    class ViewFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        public ViewFragmentAdapter(FragmentManager fm, List<Fragment> fs, List<String> titls) {
            super(fm);
            this.fragments = fs;
            this.titles = titls;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return this.titles.get(position);
        }
    }
}