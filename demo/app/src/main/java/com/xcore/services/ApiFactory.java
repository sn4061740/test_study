package com.xcore.services;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.PostRequest;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.TCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.ui.Config;
import com.xcore.utils.SystemUtils;

import java.io.File;
import java.util.List;

public class ApiFactory {
    private ApiService apiService=new ApiService();
    private LogService logService=new LogService();

    static Config config;

    public ApiFactory(){}
    private static ApiFactory instance;
    public static ApiFactory getInstance(){
        if(instance==null){
            instance=new ApiFactory();
        }
        if(token_verify()==true){
            MainApplicationContext.stopHeart();
            ApiSystemFactory.getInstance().refreshToken();
        }
        return instance;
    }
    public static boolean token_verify(){//
        if(config==null){
            config= MainApplicationContext.getConfig();
        }
        String tokenExpiresIn=config.get(config.TOKEN_EXPIRES_IN);
        String currentExpiresIn= config.get(config.CURRENT_EXPIRES_IN);
        if(tokenExpiresIn==null||
                tokenExpiresIn.length()<=0||
                currentExpiresIn==null||
                currentExpiresIn.length()<=0){
            return false;
        }
        int x=30;
        if(BaseCommon.isTestServer==false){
            x=300;
        }
        long exTime=x*1000;
        long curTime=System.currentTimeMillis();
        long expires_in=Long.valueOf(tokenExpiresIn);
        long current_in=Long.valueOf(currentExpiresIn);
        if(expires_in<=0||current_in<=0){
            return false;
        }
        long xT=current_in+expires_in*1000-exTime;
        boolean b=xT<=curTime;
        return b;
    }

    public <T> void getMyShareList(int pageIndex,TCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/appuser/getShareRecord";
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        apiService.<T>post(url,callback,params,null);
    }
    public <T> void getUserInfo(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/appuser/getUserInfo";
        apiService.<T>post(url,callback,null,null);
    }
    public <T> void getTags(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/categories/getMovueCategories";
        apiService.<T>get(url,callback);
    }
    public <T> void getHomeData(TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getHomeDataV1";
        apiService.<T>post(url,callback,null,null);
    }
    public <T>void getTypeByData(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieSearch";
        apiService.<T>post(url,callback,params,null);
    }

    public <T> void getActress(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/actor/getMovieByActor";
        apiService.<T>post(url,callback,params,null);
    }
    public <T> void getTag(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getMovieBytags";
        apiService.<T>post(url,callback,params,null);
    }
    public <T> void getMovieBytagsList(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getMovieBytagsList";
        apiService.<T>post(url,callback,params,null);
    }

    public <T> void getBoxTime(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/openbox/boxNextTime";
        apiService.<T>get(url,callback);
    }
    public <T> void getOpenBox(TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/openbox/openbox";
        apiService.<T>get(url,callback);
    }
    public <T>void getMovieDetail(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/DetailV1";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getPlayPath(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/Play";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getPlayPathV1(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/PlayV1";
        apiService.<T>post(url,callback,params,null);
    }


    public <T>void addCollect(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/addCollection";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void removeCollect(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/deleteCollection";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getDownPath(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/addCache";
        apiService.<T>post(url,callback,params,null);
    }

    public <T>void getQualtys(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getSharpness";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getCacheV1(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/addCacheV1";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getLikeOrNo(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/praise";
        apiService.<T>post(url,callback,params,null);
    }
    public <T> void getFeedbackTypes(TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/getGuestBookType";
        apiService.<T>get(url,callback);
    }
    public <T>void addFeedback(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/addBuestBook";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getFeedbackList(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/getBuestBook";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getFinds(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/findingsV1";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getNotices(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getAnnouncement";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getNvStarRecommend(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/actor/getRecommendActor";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getThems(TCallback<T> callback, HttpParams params){
        String url=BaseCommon.API_URL+"api/v1/themes/getThemesV1";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getThemeById(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/themes/getThemesByThemeID";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getThemeByIdCover(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/themes/getThemesById";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getHotDic(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getSearchKey";
        apiService.<T>get(url,callback);
    }
    public <T>void getSearchByKey(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieKeyword";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getAllTags(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getTagsRoot";
        apiService.<T>get(url,callback);
    }
    public <T>void getAllTagsList(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/tags/getTagsByTagsRoot";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getViplevel(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/viplevel/getViplevel";
        apiService.<T>get(url,callback);
    }
    public <T>void updateUserInfo(HttpParams params,TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/appuser/saveUserInfo";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getRecode(int status,int pageIndex,int pageSize,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/GetPlayRecord";
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("PageSize",pageSize);
        params.put("status",status);
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getCollect(int pageIndex,int pageSize,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/GetCollection";
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("PageSize",pageSize);
        apiService.<T>post(url,callback,params,null);
    }

    public <T>void deleteRC(int type,List<String> arr,TCallback<T> callback){
        String url=BaseCommon.API_URL;
        if(type==0){
            url+="api/v1/movie/deleteCollectionV1";
        }else{
            url+="api/v1/movie/DeletePlayRecord";
        }
        String value="";
        for(int i=0;i<arr.size();i++){
            if(i<arr.size()-1) {
                value += arr.get(i) + ",";
            }else{
                value+=arr.get(i);
            }
        }
        HttpParams params=new HttpParams();
        params.put("shortIdArray",value);
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void updateUserPass(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/appuser/savePassword";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void getRecommends(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/getRecommendDataV1";
        apiService.<T>post(url,callback,null,null);
    }
    public <T>void getNoticeAlert(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/GetAlert";
        apiService.get(url,callback);
    }
    public <T>void joinView(String className,String paramsStr,TCallback callback){
        HttpParams params=new HttpParams();
        params.put("shortId",className);
        params.put("key",paramsStr);
        String url="api/v1/system/joinView";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void outView(String className,String paramsStr,TCallback callback){
        HttpParams params=new HttpParams();
        params.put("shortId",className);
        String url="api/v1/system/outView";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void toCrashLog(String msg,TCallback<T> callback){
        HttpParams params=new HttpParams();
        params.put("key",msg);
        String url="api/v1/system/collapse";
        logService.post(url,callback,params,null);
    }
    public <T>void toError(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/ARR";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void toUploadSingle(String base64,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/fileImage";
        HttpParams params=new HttpParams();
        params.put("key",base64);
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void toPlayInfo(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/playSpeedRecordAddV1";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void toUpdatePlayInfo(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/playSpeedRecordUpdate";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void toCacheInfo(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/updateCacheLength";
        apiService.<T>post(url,callback,params,null);
    }
    public <T>void addCacheInfo(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/CacheSpeedRecordAdd";
        logService.<T>post(url,callback,params,null);
    }
    public <T>void updateCacheInfo(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/CacheSpeedRecordUpdate";
        logService.<T>post(url,callback,params,null);
    }
    public <T> void toHeart(TCallback<T> callback){
        String url="api/v1/system/heartbeat";
        logService.get(url,callback);
    }
    public <T> void getNvStars(HttpParams httpParams,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/actor/getactor";
        apiService.post(url,callback,httpParams,null);
    }
    public <T> void getCompanyTags(HttpParams httpParams,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/GetMovieCompanyData";
        apiService.post(url,callback,httpParams,null);
    }
    public <T>void getCompanyInfos(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieCompany";
        apiService.post(url,callback,params,null);
    }
    public <T>void toServerSocket(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/AddTestUrlRecord";
        logService.post(url,callback,params,null);
    }
    public <T>void getCommentByShortId(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/filmReview/getFilmReviewByMovie ";
        apiService.post(url,callback,params,null);
    }
    public <T>void getDianZ(String shortId,int status,int type,TCallback<T> callback){
        String url="";
        if(type==0){
            url=BaseCommon.API_URL+"api/v1/filmReview/praise";
        }else{
            url=BaseCommon.API_URL+"api/v1/filmReview/cancelPraise";
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        params.put("status",status);
        apiService.post(url,callback,params,null);
    }
    public <T>void addComment(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/filmReview/filmReviewAdd";
        apiService.post(url,callback,params,null);
    }
    public <T>void uploadFile(File file, TCallback<T> callback){
        if(!file.exists()){
            return;
        }
        String url=BaseCommon.API_URL+"Expansion/LogFile";
        url=UrlUtils.getUrl(url);
        PostRequest<T> postRequest=OkGo.<T>post(url);
        Context context=MainApplicationContext.context;
        Config config=MainApplicationContext.getConfig();
        String v=config.getKey();
        if(TextUtils.isEmpty(v)){
            return;
        }
        postRequest.tag(context)
                .isMultipart(true)
                .params("AppDeviceCode",v)
                .params("channel",BaseCommon.CHANNEL)
                .params("log",file);
        postRequest.execute(callback);
    }
    public <T>void testSpeedByFile(HttpParams params,TCallback<T> callback){
        String url="api/v1/system/AddDownSpeedTestRecord";
        logService.post(url,callback,params,null);
    }
    public <T>void saveShare(TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/appuser/SaveQRcode";
        apiService.get(url,callback);
    }
    public <T>void clickedAdv(String shortId,TCallback<T> callback){
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        Config config=MainApplicationContext.getConfig();
        String code=config.getKey();
        params.put("DeviceNo",code);
        String url="/api/v1/system/clickad";
        logService.post(url,callback,params,null);
    }
    public <T>void addown(String shortId,TCallback<T> callback){
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        Config config=MainApplicationContext.getConfig();
        String code=config.getKey();
        params.put("DeviceNo",code);
        String url="/api/v1/system/addown";
        logService.post(url,callback,params,null);
    }
    public <T>void checkEgg(TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/colorEgg";
        apiService.post(url,callback,null,null);
    }
    public <T>void getEgg(String key,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/exchangecolorEgg";
        HttpParams params=new HttpParams();
        params.put("key",key);
        apiService.post(url,callback,params,null);
    }
    public <T>void actCode(String code,String shortId,TCallback<T> callback){
        String url=BaseCommon.API_URL+"Home/exchange";
        HttpParams params=new HttpParams();
        params.put("code",code);
        Config config= MainApplicationContext.getConfig();
        params.put("shortId",config.getKey());
        params.put("yzm",shortId);
        apiService.post(url,callback,params,null);
    }

}
