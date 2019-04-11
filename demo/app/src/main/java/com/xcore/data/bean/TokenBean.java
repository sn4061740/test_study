package com.xcore.data.bean;

import android.util.Log;

import com.common.BaseCommon;
import com.xcore.data.BaseBean;

public class TokenBean extends BaseBean{
    String access_token;
    String token_type;
    long expires_in;//秒
    String refresh_token;
    long token_time=System.currentTimeMillis();//得到token 的时间
    String error;
    String error_description;

    @Override
    public String toString() {
        return "TokenBean{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_time=" + token_time +
                ", error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                '}';
    }

    public boolean token_verify(){//
        int x=30;
        if(BaseCommon.isTestServer==false){
            x=300;
        }
        long exTime=x*1000;//提前5分钟刷新token
        long curTime=System.currentTimeMillis();

        long xT=getToken_time()+expires_in*1000-exTime;//要刷新时候的时间
        boolean b=xT<=curTime;
        return b;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getToken_time() {
        return token_time;
    }

    public void setToken_time(long token_time) {
        this.token_time = token_time;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
