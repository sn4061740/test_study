package com.xcore.ui.fragment;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.xcore.R;
import com.xcore.base.LazyLoadBaseFragment;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class ReferralFragment extends LazyLoadBaseFragment {

    public ReferralFragment() {
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_referral;
    }

    ViewPager viewPager;

    @Override
    protected void initView(View rootView) {

        viewPager = rootView.findViewById(R.id.viewpager);

        Fragment f1=new TagFragment();

        Fragment f2=new TagFragment();
        List<Fragment> fs= new ArrayList<>();
        fs.add(f1);
        fs.add(f2);

        TabLayout tabLayout = rootView.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        Fragment rFragment = RecommendFragment.newInstance("","");
        adapter.addFragment(rFragment, "推荐");

        adapter.addFragment(new TagFragment(),"标签");

        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.e("TAG","切换page:"+position);
//                if(pageCurrent==position){
//                    return;
//                }
//                pageCurrent=position;
//                String sId=cList.get(pageCurrent).getShortId();
//                t2=sId;
////                boolean boo=loads.get(sId);
////                if(boo==false) {//没有加载过才加载
//                refreshData();
////                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    //界面可见的时候调用
    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

