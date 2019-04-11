package com.xcore.cache;

import com.xcore.down.M3U8DownLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 缓存管理
 */
public class CacheManager {
    public static String PLAY_IS_CACHE="play_is_cache";//播放中的时候是否缓存
    public static String IDLE_IS_CACHE="idle_is_cache";//空闲的时候是否缓存


    private CacheManager(){}
    private static CacheManager instance;
    public static CacheManager getInstance(){
        if(instance==null){
            instance=new CacheManager();
        }
        return instance;
    }
    private LocalHandler localHandler;
    private DBHandler dbHandler;
//    private DownHandler downHandler;
    private LocalDownLoader localDownLoader;
    private M3U8DownLoader m3U8DownLoader;

    public DBHandler getDbHandler() {
        return dbHandler;
    }

    public LocalHandler getLocalHandler() {
        return localHandler;
    }

//    public DownHandler getDownHandler(){
//        return this.downHandler;
//    }

    public LocalDownLoader getLocalDownLoader() {
        return localDownLoader;
    }

    public M3U8DownLoader getM3U8DownLoader() {
        return m3U8DownLoader;
    }

    /**
     * 初始化缓存数据
     */
    public void init(){
        //初始化缓存数据
        localHandler=new LocalHandler();
        dbHandler=new DBHandler();
//        downHandler=new DownHandler(dbHandler);
        localDownLoader=new LocalDownLoader(dbHandler);
        m3U8DownLoader=new M3U8DownLoader(dbHandler);
    }

    public void initDown(){
//        this.downHandler.initDown();
        localDownLoader.init();
        m3U8DownLoader.init();
    }

    /**
     * 下载
     * @param downModel
     */
    public void downByUrl(DownModel downModel){
//        this.downHandler.downByUrl(downModel);
        this.localDownLoader.down(downModel);
    }

//    /**
//     * 得到下载的list
//     * @return
//     */
//    public List<CacheModel> getDataList(){
//        Map<String,CacheModel> mapData= this.downHandler.getDataMap();
//        List<CacheModel> list=new ArrayList<CacheModel>();
//        for (Map.Entry<String, CacheModel> entry : mapData.entrySet()) {
//            CacheModel cacheModel = entry.getValue();
//            list.add(cacheModel);
//        }
//        return list;
//    }

}
