package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.RCBean;
import com.xcore.presenter.CollectPresenter;
import com.xcore.presenter.view.CollectView;
import com.xcore.ui.fragment.RecodeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecodeActivity extends MvpActivity<CollectView,CollectPresenter> implements CollectView {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_recode;
    }

    private TabLayout tabLayout;
    private ViewPager mViewPager;

    private int currentPage=0;
    private boolean isEditBoo=false;
    final List<Fragment> fragments=new ArrayList<>();
    Map<String,Integer> pageIndexs=new HashMap<>();
    Map<String,Boolean> loadPages=new HashMap<>();

    @Override
    protected void initViews(Bundle savedInstanceState) {
        hideNullLayout();

        setTitle("历史记录");
        setEdit("编辑",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecodeFragment fragment= (RecodeFragment) fragments.get(currentPage);
                isEditBoo=!isEditBoo;
                if(isEditBoo){
                    setEdit("取消");
                }else{
                    setEdit("编辑");
                }
//                if(overCacheFragment!=null) {
//                    overCacheFragment.onEdit(isEditBoo);
//                }
                if(fragment!=null) {
                    fragment.onEdit(isEditBoo);
                }
            }
        });
        currentPage=0;
        mViewPager= findViewById(R.id.mViewPager);
        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(mViewPager);

        RecodeFragment fragment1=new RecodeFragment();//.newInstance("1","");
        fragment1.setType(1);
        fragment1.setiRecodeListener(recodeListener);
        fragments.add(fragment1);
        RecodeFragment fragment2= new RecodeFragment();//RecodeFragment.newInstance("1","");
        fragment2.setType(2);
        fragment2.setiRecodeListener(recodeListener);
        fragments.add(fragment2);
        RecodeFragment fragment3= new RecodeFragment();//RecodeFragment.newInstance("1","");
        fragment3.setType(3);
        fragment3.setiRecodeListener(recodeListener);
        fragments.add(fragment3);

        pageIndexs.put("1",1);
        pageIndexs.put("2",1);
        pageIndexs.put("3",1);

        loadPages.put("1",false);
        loadPages.put("2",false);
        loadPages.put("3",false);

        ViewFragmentAdapter adapter=
                new ViewFragmentAdapter(getSupportFragmentManager(),fragments,
                        Arrays.asList("今天","七日内","更早"));
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                try {
                    isEditBoo = false;
                    setEdit("编辑");
                    RecodeFragment recodeFragment = (RecodeFragment) fragments.get(currentPage);
                    if (recodeFragment != null) {
                        currentPage = position;
                        //recodeListener.onRefresh(currentPage + 1,false);
                        recodeFragment.onEdit(isEditBoo);
                    }
                }catch (Exception ex){}
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {
//        int pageIndex=pageIndexs.get((currentPage+1)+"");
//        presenter.getRecode(currentPage+1,pageIndex);
//        pageIndex++;
//        pageIndexs.put(currentPage+"",pageIndex);
    }

    @Override
    public CollectPresenter initPresenter() {
        return new CollectPresenter();
    }

    @Override
    public void onCollect(RCBean rcBean) {
    }
    @Override
    public void onRecode(RCBean rcBean,int curPage) {
        try {
            loadPages.put(curPage + "", true);
            RecodeFragment fragment = (RecodeFragment) fragments.get(curPage - 1);
            if (fragment != null) {
                fragment.update(rcBean);
            }
        }catch (Exception ex){}
    }

    IRecodeListener recodeListener=new IRecodeListener() {
        @Override
        public void onRefresh(int status,boolean isRefresh) {
            try {
                isEditBoo = false;
                setEdit("编辑");
                if (isRefresh) {
                    presenter.getRecode(status, 1);
                    pageIndexs.put(status + "", 2);
                    return;
                }
                int pageIndex = pageIndexs.get(status + "");
                presenter.getRecode(status, pageIndex);
                pageIndex++;
                pageIndexs.put(status + "", pageIndex);
            }catch (Exception ex){}
        }
        @Override
        public void onDelete(List<String> arr) {
            try {
                isEditBoo = false;
                setEdit("编辑");
                presenter.deleteCollect(arr, 1);
            }catch (Exception ex){}
        }
        @Override
        public void onItemClick() {
            isEditBoo=false;
            setEdit("编辑");
        }
    };


    public interface IRecodeListener{
        void onRefresh(int status,boolean isRefresh);
        void onDelete(List<String> arr);
        void onItemClick();
    }

    class ViewFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments=new ArrayList<>();
        List<String> titles=new ArrayList<>();

        public ViewFragmentAdapter(FragmentManager fm, List<Fragment> fs, List<String> titls) {
            super(fm);
            this.fragments=fs;
            this.titles=titls;
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
