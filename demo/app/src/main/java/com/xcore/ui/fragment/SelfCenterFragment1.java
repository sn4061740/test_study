package com.xcore.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jay.HttpServerManager;
import com.jay.config.Status;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpFragment1;
import com.xcore.cache.CacheModel;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.AdvBean;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.RCBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.down.M3u8CacheActivity;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.MePresenter;
import com.xcore.presenter.view.MeView;
import com.xcore.ui.Config;
import com.xcore.ui.LoginType;
import com.xcore.ui.activity.ActiveCodeActivity;
import com.xcore.ui.activity.CollectActivity;
import com.xcore.ui.activity.FeedbackActivity;
import com.xcore.ui.activity.GuideActivity;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.NoticeDetailActivity;
import com.xcore.ui.activity.RecodeActivity;
import com.xcore.ui.activity.SettingActivity;
import com.xcore.ui.activity.SpreadShareActivity;
import com.xcore.ui.activity.UpdateUserHeadActivity;
import com.xcore.ui.activity.UpgradeActivity;
import com.xcore.ui.adapter.DownAdapter;
import com.xcore.ui.adapter.SelfAdapter;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.JumpUtils;
import com.xcore.utils.LogUtils;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenpengfei on 2017/3/21.
 */
public class SelfCenterFragment1 extends MvpFragment1<MeView,MePresenter> implements MeView,View.OnClickListener{
    private SelfAdapter hositoryAdapter;
    private DownAdapter cacheAdapter;
    private SelfAdapter collectAdapter;

    private ImageView item_avatar;//头像

    private Button gfggButton;
    private Button yjfkButton;
    private Button djtqButton;
    private Button gfqButton;

    private TextView vipInfo;//等级信息
    private ImageView vipImg;//vip res
    private TextView uname;//昵称
    private TextView cacheCountTxt;//缓存次数
    private TextView playCountTxt;//播放次数
    private TextView codeTxt;//邀请码

    private RefreshLayout refreshLayout;

    private LinearLayout toQcode;//二维码
    private CardView advLayout;//广告布局
    private ImageViewExt conver;//广告封面

    private LinearLayout cacheList;//缓存列表
    private LinearLayout collectList;//我的收藏
    private LinearLayout historyList;//历史记录

    private TextView hCountTxt;
    private TextView cCountTxt;
    private TextView dCountTxt;
    private TextView dayCountLabel;

    private RecyclerView collectRecyclerView;
    private RecyclerView cacheRecyclerView;
    private RecyclerView hositoryRecyclerView;

    private PlayerBean playerBean;

    private String toUrl="";

    private Config config;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_self;
    }

    @Override
    protected void initView(View view) {
        config=MainApplicationContext.getConfig();

        collectRecyclerView=view.findViewById(R.id.collectRecyclerView);
        cacheRecyclerView=view.findViewById(R.id.cacheRecyclerView);
        hositoryRecyclerView=view.findViewById(R.id.hositoryRecyclerView);
        refreshLayout=view.findViewById(R.id.refreshLayout);

        this.initButton(view);
        view.findViewById(R.id.setting).setOnClickListener(this);
        view.findViewById(R.id.upgradeLayout).setOnClickListener(this);

        view.findViewById(R.id.activiCodeTxt).setOnClickListener(this);

        dayCountLabel=view.findViewById(R.id.day_countLabel);
        vipImg=view.findViewById(R.id.vipImg);
        vipInfo=view.findViewById(R.id.vipInfo);
        item_avatar=view.findViewById(R.id.item_avatar);
        uname=view.findViewById(R.id.uname);
        cacheCountTxt=view.findViewById(R.id.cacheCountTxt);
        playCountTxt=view.findViewById(R.id.playCountTxt);
        codeTxt=view.findViewById(R.id.codeTxt);

        codeTxt.setOnClickListener(this);

        hCountTxt=view.findViewById(R.id.hCountTxt);
        cCountTxt=view.findViewById(R.id.cCountTxt);
        dCountTxt=view.findViewById(R.id.dCountTxt);

        toQcode=view.findViewById(R.id.toQcode);
        conver=view.findViewById(R.id.conver);
        advLayout=view.findViewById(R.id.advLayout);
        advLayout.setVisibility(View.GONE);

        cacheList=view.findViewById(R.id.cacheList);
        cacheList.setOnClickListener(this);

        collectList=view.findViewById(R.id.collectList);
        collectList.setOnClickListener(this);

        historyList=view.findViewById(R.id.historyList);
        historyList.setOnClickListener(this);

        conver.setOnClickListener(this);
        toQcode.setOnClickListener(this);

        collectAdapter=new SelfAdapter(getContext());
        collectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        collectRecyclerView.setAdapter(collectAdapter);

        hositoryAdapter=new SelfAdapter(getContext());
        hositoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        hositoryRecyclerView.setAdapter(hositoryAdapter);

        cacheAdapter=new DownAdapter(getContext());
        cacheRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
        LinearLayoutManager.HORIZONTAL,false));
        cacheRecyclerView.setAdapter(cacheAdapter);
        cacheAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheModel>() {
            @Override
            public void onItemClick(CacheModel item, int position) {
//            Intent intent=new Intent(getContext(),CacheActivity.class);
//            intent.putExtra("shortId",item.getShortId());
//            startActivity(intent);
            boolean isRun=item.getComplete()==1;
            M3u8CacheActivity.toActivity(getContext(),isRun,item.getShortId());
            }
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });
        refreshLayout.setEnableLoadMore(false);

        item_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(getContext(), UpdateUserHeadActivity.class);
            startActivity(intent);
            }
        });
    }
    //初始化总数
    void initCount(){
//        hCountTxt.setText("目前观看过0部");
//        cCountTxt.setText("目前已收藏0部");
//        dCountTxt.setText("目前已缓存0部");
//
//        try {
//            List<CacheCountBean> cacheCountBeans = new ArrayList<>();
//            //得到总数
//            DBHandler dbHandler = CacheManager.getInstance().getDbHandler();
//            if (dbHandler != null) {
//                List<CacheCountBean> cacheBeans = dbHandler.getCacheCount();
//                if (cacheBeans.size() > 0) {
//                    cacheCountBeans.addAll(cacheBeans);
//                }
//                CacheCountBean c=new CacheCountBean();
//                c.vt=CacheType.CACHE_DOWN;
//                c.count=M3u8DownTaskManager.getInstance().getTaskMaps().size()+"";// CacheManager.getInstance().getLocalDownLoader().getDataList().size()+"";
//                cacheCountBeans.add(c);
//
////                List<CacheCountBean> downs = dbHandler.getDownCount();
////                if (downs.size() > 0) {
////                    cacheCountBeans.addAll(downs);
////                }
//            }
//            if (cacheCountBeans.size() <= 0) {
//                return;
//            }
//            for (CacheCountBean item : cacheCountBeans) {
//                if (item.vt.equals(CacheType.CACHE_RECODE)) {
//                   // hCountTxt.setText("目前观看过" + item.count + "部");
//                } else if (item.vt.equals(CacheType.CACHE_COLLECT)) {
//                    //cCountTxt.setText("目前已收藏" + item.count + "部");
//                } else if (item.vt.equals(CacheType.CACHE_DOWN)) {
//                    dCountTxt.setText("目前已缓存" + item.count + "部");
//                }
//            }
//        }catch (Exception e){}
    }

    //初始化观看记录
    void initHository(){
//        try {
//            List<CacheBean> cacheBeanList = CacheManager.getInstance().getDbHandler().query(1, CacheType.CACHE_RECODE);
////            Log.e("TAG", cacheBeanList.toString());
//            if (cacheBeanList != null && cacheBeanList.size() > 0) {
//                hositoryAdapter.setData(cacheBeanList);
//                hositoryRecyclerView.setVisibility(View.VISIBLE);
//            } else {
//                hositoryRecyclerView.setVisibility(View.GONE);
//            }
//        }catch (Exception e){}

        presenter.getRecode(1);
    }

    //初始化缓存
    void initCache(){
        try {
            List<CacheModel> cacheModels =new ArrayList<>();
            List<CacheModel> overModels =new ArrayList<>();

            Map<String, com.jay.config.Config> configs=HttpServerManager.getInstance().getM3u8DownManager().getConfigs();
            if(configs.size()>0){
                for(com.jay.config.Config cf:configs.values()){
                    CacheModel cacheBean=new CacheModel();
                    cacheBean.setShortId(cf.getmId());
                    cacheBean.setConver(cf.getmConver());
                    cacheBean.setName(cf.getmName());
                    cacheBean.setPercent(cf.getProgress()+"");
                    if(cf.getStatus()==Status.COMPLETE){
                        cacheBean.setComplete(1);
                        overModels.add(cacheBean);
                    }else{
                        cacheBean.setComplete(0);
                        cacheModels.add(cacheBean);
                    }
                }
            }
//             Collection<M3U8TaskModel> m3U8TaskModels= M3u8DownTaskManager.getInstance().getTaskMaps().values();
//             if(m3U8TaskModels!=null&&m3U8TaskModels.size()>0) {
//                 for (M3U8TaskModel model:m3U8TaskModels){
//                     CacheModel cacheBean=new CacheModel();
//                     cacheBean.setShortId(model.getShortId());
//                     cacheBean.setConver(model.getConver());
//                     cacheBean.setName(model.getTitle());
//                     cacheBean.setPercent(model.getPrent()+"");
//                     if(model.getStatus()==M3U8TaskModel.TaskStatus.COMPLETE){
//                         cacheBean.setComplete(1);
//                         overModels.add(cacheBean);
//                     }else{
//                         cacheBean.setComplete(0);
//                         cacheModels.add(cacheBean);
//                     }
//                 }
//             }
            cacheModels.addAll(overModels);
            cacheAdapter.setData(cacheModels);
//            dCountTxt.setText("目前已缓存" + m3U8TaskModels.size() + "部");
            dCountTxt.setText("目前已缓存" + configs.size() + "部");
        }catch (Exception e){}
    }

    //初始化收藏
    void initCollect(){
//        try {
//            List<CacheBean> cacheBeanList = CacheManager.getInstance().getDbHandler().query(1, CacheType.CACHE_COLLECT);
//            if (cacheBeanList != null && cacheBeanList.size() > 0) {
//                collectAdapter.setData(cacheBeanList);
//                collectRecyclerView.setVisibility(View.VISIBLE);
//            } else {
//                collectRecyclerView.setVisibility(View.GONE);
//            }
//        }catch (Exception e){}
        presenter.getCollect();
    }

    //初始化按钮信息
    private void initButton(View view){
        gfggButton=view.findViewById(R.id.btn_gfgg);
        Drawable[] gfggDs= gfggButton.getCompoundDrawables();
        gfggDs[1].setBounds(0,0,gfggButton.getResources().getDimensionPixelSize(R.dimen._30),
                gfggButton.getResources().getDimensionPixelSize(R.dimen._30));
        gfggButton.setCompoundDrawables(gfggDs[0],gfggDs[1],gfggDs[2],gfggDs[3]);
        gfggButton.setOnClickListener(this);

        yjfkButton=view.findViewById(R.id.btn_yjfk);
        Drawable[] yjfkDs= yjfkButton.getCompoundDrawables();
        yjfkDs[1].setBounds(0,0,yjfkButton.getResources().getDimensionPixelSize(R.dimen._30),
                yjfkButton.getResources().getDimensionPixelSize(R.dimen._30));
        yjfkButton.setCompoundDrawables(yjfkDs[0],yjfkDs[1],yjfkDs[2],yjfkDs[3]);
        yjfkButton.setOnClickListener(this);

        djtqButton=view.findViewById(R.id.btn_djtq);
        Drawable[] djtqDs= djtqButton.getCompoundDrawables();
        djtqDs[1].setBounds(0,0,djtqButton.getResources().getDimensionPixelSize(R.dimen._30),
                djtqButton.getResources().getDimensionPixelSize(R.dimen._30));
        djtqButton.setCompoundDrawables(djtqDs[0],djtqDs[1],djtqDs[2],djtqDs[3]);
        djtqButton.setOnClickListener(this);

        if(MainApplicationContext.testClientLog==true){
            djtqButton.setText("上报信息");
        }

        gfqButton=view.findViewById(R.id.btn_gfq);
        Drawable[] gfqDs= gfqButton.getCompoundDrawables();
        gfqDs[1].setBounds(0,0,gfqButton.getResources().getDimensionPixelSize(R.dimen._30),
                gfqButton.getResources().getDimensionPixelSize(R.dimen._30));
        gfqButton.setCompoundDrawables(gfqDs[0],gfqDs[1],gfqDs[2],gfqDs[3]);
        gfqButton.setOnClickListener(this);
    }

    private void refreshData(){
        presenter.getUserInfo();

        try {
            //初始化本地缓存
            initCount();
            initHository();
            initCache();
            initCollect();
        }catch (Exception e){}
    }

    @Override
    public MePresenter initPresenter() {
        return new MePresenter();
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.setting:
            intent=new Intent(getContext(), SettingActivity.class);
            break;
            case R.id.codeTxt:
                String guest2=config.get("USER_GUEST");
                if("1".equals(guest2)||guest2.length()<=0){//是游客直接跳登录
                    LoginActivity.toActivity(getActivity(),LoginType.LOGIN);
                }
                break;
            case R.id.upgradeLayout:
                intent=new Intent(getContext(), UpgradeActivity.class);
                //把得到的参数传过去
                String pStr=new Gson().toJson(playerBean);
                intent.putExtra("user",pStr);
                break;
            case R.id.conver:
                //跳网页
//                intent = new Intent( Intent.ACTION_VIEW );
//                intent.setData( Uri.parse( toUrl) ); //这里面是需要调转的rul
//                intent = Intent.createChooser( intent, null );
                AdvBean advBean= playerBean.getData().getPlayerAdv();
                JumpUtils.to(getActivity(),advBean);
                break;
            case R.id.toQcode:
                boolean pwdBoo1=MainApplicationContext.toGuestLogin(getActivity());
                if(pwdBoo1){
                    return;
                }
                intent = new Intent(getContext(), SpreadShareActivity.class);
                break;
            case R.id.cacheList:
//                intent=new Intent(getContext(), CacheActivity.class);
                M3u8CacheActivity.toActivity(getContext());
//                AActivity.toActivity(getContext());
                break;
            case R.id.collectList:
                intent=new Intent(getContext(), CollectActivity.class);
                break;
            case R.id.historyList:
                intent=new Intent(getContext(), RecodeActivity.class);
                break;
            case R.id.btn_gfgg:
                intent=new Intent(getContext(),NoticeDetailActivity.class);
                break;
            case R.id.btn_yjfk:
                intent=new Intent(getContext(),FeedbackActivity.class);
                break;
            case R.id.btn_djtq:
                if(MainApplicationContext.testClientLog==true){
                    presenter.show1();
                    LogUtils.uploadLog(handler);
                }else {
                    intent = new Intent(getContext(), GuideActivity.class);
                }
                break;
            case R.id.btn_gfq://跳网页
                if(playerBean!=null){
                    UserInfo userInfo= playerBean.getData();
                    if(userInfo!=null) {
                        intent = new Intent(Intent.ACTION_VIEW);//https://www.potato.im/1avco1
                        intent.setData(Uri.parse(userInfo.getGroupConnection())); //这里面是需要调转的rul
                        intent = Intent.createChooser(intent, null);
                    }
                }
                break;
            case R.id.activiCodeTxt://点击激活码兑换
                if(playerBean!=null&&playerBean.getData()!=null) {
                    intent = new Intent(getActivity(), ActiveCodeActivity.class);
                }
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            presenter.hide();
            if(msg.what==0){
                toast("上报成功");
            }else if(msg.what==1){
                toast("上报失败,请重新上报");
            }else if(msg.what==2){
                toast("文件不存在");
            }else if(msg.what==3){
                toast("压缩文件出错了");
            }
        }
    };


    @Override
    public void onRecode(final RCBean rcBean) {
        try {
            if(getActivity()==null||getActivity().isFinishing()){
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hCountTxt.setText("目前观看过0部");
                    if (rcBean == null) {
                        hositoryRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    List<RCBean.RCData> rcDatas = rcBean.getList();
                    if (rcDatas == null || rcDatas.size() <= 0) {
                        hositoryRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    for (RCBean.RCData data : rcDatas) {
                        data.settType(CacheType.CACHE_RECODE);
                    }
                    hositoryAdapter.setData(rcDatas);
                    hositoryRecyclerView.setVisibility(View.VISIBLE);
                    hCountTxt.setText("目前观看过" + rcBean.getTotalCount() + "部");
                }
            });
        }catch (Exception ex){}
    }

    @Override
    public void onCollect(final RCBean rcBean) {
        try {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cCountTxt.setText("目前已收藏0部");
                    if (rcBean == null) {
                        collectRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    List<RCBean.RCData> rcDatas = rcBean.getList();
                    if (rcDatas == null || rcDatas.size() <= 0) {
                        collectRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    for (RCBean.RCData data : rcDatas) {
                        data.settType(CacheType.CACHE_COLLECT);
                    }
                    collectAdapter.setData(rcDatas);
                    collectRecyclerView.setVisibility(View.VISIBLE);
                    cCountTxt.setText("目前已收藏" + rcBean.getTotalCount() + "部");
                }
            });
        }catch (Exception ex){}
    }

    //更新信息
    private void updateInfo(UserInfo userInfo){
        try {
            //设置头像
            if(!TextUtils.isEmpty(userInfo.getHeadUrl())) {
                CacheFactory.getInstance().getImage(getContext(), item_avatar, userInfo.getHeadUrl());
            }

            vipImg.setImageResource(userInfo.getRes(userInfo.getVip()));
            vipInfo.setText(userInfo.getVipStr(userInfo.getVip()));
            uname.setText(userInfo.getName());
            playCountTxt.setText(userInfo.getPlayStr());
            cacheCountTxt.setText(userInfo.getCacheStr());

            if (userInfo.getShareCode().length() > 0) {
                codeTxt.setText("邀请码:" + userInfo.getShareCode());
            } else {
                codeTxt.setText("登录/注册");
            }
            advLayout.setVisibility(View.GONE);
            AdvBean advBean=userInfo.getPlayerAdv();
            if(advBean!=null){
                advLayout.setVisibility(View.VISIBLE);
                conver.loadUrl(advBean.getImagePath());
//                conver.loadRadius(advBean.getImagePath());
                toUrl=advBean.getToUrl();
            }
            if (userInfo.isSuperVIP()) {
//            dayCountLabel.setVisibility(View.GONE);
            } else {
                dayCountLabel.setVisibility(View.VISIBLE);
            }
            if (userInfo.getUnread() != null && userInfo.getUnread() > 0) {
                String vInfoStr = "您的反馈有回复了,快去查看吧!!!";
                MainApplicationContext.showips(vInfoStr, getActivity(), TipsEnum.TO_FEED_BACK);
            }

            String vRecord = userInfo.getAppUserVIPLevelUpgradeRecord();
            if (vRecord == null || "".equals(vRecord) || vRecord.isEmpty()) {
                return;
            }
            int lUpgrade = Integer.valueOf(vRecord);
            String vStr = userInfo.getVipStr(lUpgrade);
            String vInfoStr = "恭喜升级到" + vStr;
            MainApplicationContext.showips(vInfoStr, getActivity(), TipsEnum.TO_TIPS);
        }catch (Exception ex){}
        finally {
            RefreshUtil.refreshHeader();
        }
    }
    @Override
    public void onResult(PlayerBean playerBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            RefreshUtil.refreshHeader();
        }
        try {
            this.playerBean = playerBean;
            //更新信息
            UserInfo userInfo = playerBean.getData();
            updateInfo(userInfo);

            //MainApplicationContext.isGuest = userInfo.tourist;
        }catch (Exception ex){}
        finally {
            RefreshUtil.refreshHeader();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshData();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //refreshData();
    }

}
