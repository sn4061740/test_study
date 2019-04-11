package com.xcore.ui.test;

public class PlaySpeedModel {
    private long currentPos=0;//当前播放到的进度时间
    private double avgSpeed=0;//平均速度
    private int stopTime=0;//停留时长
    private String httpUrl="";//http 请求url
    private double httpAvg=0;//http 平均速度
    private long total=0;//总下载大小
    private long httpTotal=0;//http 总下载
    private String line="";//线路

    private int time=1;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time += time;
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(long currentPos) {
        this.currentPos = currentPos;
    }

    /**
     * 得到总的平均速度
     * @return
     */
    public double getAvgSpeed() {
        try {
            if(this.time==0){
                this.time=1;
            }
            double avg = avgSpeed * 1.0 / this.time / 1024;
            if(Double.isNaN(avg)){
                avg=0;
            }
            return avg;
        }catch (Exception ex){
        }
        return 0;
    }
    //设置总的 速度
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed += avgSpeed;
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    /**
     * 得到的速度是 KB
     * @return http 平均速度
     */
    public double getHttpAvg() {
        if(this.time==0){
            this.time=1;
        }
        double hAvg= httpAvg*1.0/this.time/1024;
        if(Double.isNaN(hAvg)){
            hAvg=0;
        }
        return hAvg;
    }

    /**
     * 设置 http 总速度
     * @param httpAvg
     */
    public void setHttpAvg(double httpAvg) {
        this.httpAvg += httpAvg;
    }

    public long getTotal() {
        return total;
    }
    //设置 tor 总大小
    public void setTotal(long total) {
        this.total = total;
    }

    public long getHttpTotal() {
        return httpTotal;
    }

    public void setHttpTotal(long httpTotal) {
        this.httpTotal = httpTotal;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
