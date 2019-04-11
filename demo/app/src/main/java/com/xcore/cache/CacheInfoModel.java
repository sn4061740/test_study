package com.xcore.cache;

public class CacheInfoModel {
    public int time;//总时间
    public String shortId="";
    public long totalDownSize=0;//总下载
    public long httpTotalDownSize=0;//http总下载
    public long httpTotalSpeed=0;//http 总速度
    public long totalSpeed=0;//总速度
    public int perent=0;//总进度
    public long totalSize=0;//总大小
    public long totalBytesDown=0;//本次总下载

    //得到 time 时间内的 http 平均速度
    public double getHttpAvgSpeed(){
        if(time==0){
            time=1;
        }
        double v=httpTotalSpeed*1.0/time/1024;
        return v;
    }
    public double getAvgSpeed(){
        if(time==0){
            time=1;
        }
        double v=totalSpeed*1.0/time/1024;
        return v;
    }
}
