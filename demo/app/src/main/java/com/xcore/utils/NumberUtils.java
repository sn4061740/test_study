package com.xcore.utils;

import java.util.Random;

public class NumberUtils {

    /**
     * 返回两位小数的评论
     * @param v
     * @return
     */
    public static String to(Integer v){
        if(v==null){
            return 0+"条评论";
        }
        if(v>=10000){
            double v1=v*1.0/10000;
            return String.format("%.1f", v1)+"条评论";
        }
        return v+"条评论";
    }

    public static int getRandom(int n){
        Random random = new Random();
        int s = random.nextInt(n);
        return s;
    }
}
