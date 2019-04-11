package com.xcore.utils;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.Arrays;
import java.util.List;

public class RefreshUtil {
    static final List<String> refArr= Arrays.asList(
            "水淫洞探索，探索瀑布爆发瞬间~",
            "嫩滑豆腐，似吃麻婆被砍数刀~",
            "咪咪大不是罪,是发育期潮吹累",
            "我性爱的床戏,是你发骚的开端",
            "你知我长短,我懂你深浅^_^",
            "趁我睡.掀我被.脱我裤子舔我妹",
            "我人实际,日久生情",
            "名花有无主,必要松松口",
            "我的眼睛和下面只会为你湿",
            "英雄不问出路,流水不分出处",
            "三寸之舌,吞精兵两亿",
            "游过尖山，进入横洞"
            );

    //刷新显示
    public static void refreshHeader(){
        try {
            int v= NumberUtils.getRandom(refArr.size());
            String xv=refArr.get(v);
            ClassicsHeader.REFRESH_HEADER_REFRESHING =xv;
            ClassicsHeader.REFRESH_HEADER_FINISH=xv;
            ClassicsHeader.REFRESH_HEADER_PULLING=xv;
            ClassicsHeader.REFRESH_HEADER_RELEASE=xv;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
