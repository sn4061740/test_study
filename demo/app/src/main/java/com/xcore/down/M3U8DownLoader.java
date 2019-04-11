package com.xcore.down;

import android.text.TextUtils;

import com.jay.HttpServerManager;
import com.jay.config.Status;
import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DBHandler;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class M3U8DownLoader {
    DBHandler dbHandler;
    public M3U8DownLoader(DBHandler handler){
        this.dbHandler=handler;
    }
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public void init(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String playCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.PLAY_IS_CACHE);
                    if(!TextUtils.isEmpty(playCacheStr)){
                        MainApplicationContext.IS_PLAYING_TO_CACHE=true;
                    }
                    String idleCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.IDLE_IS_CACHE);
                    if(!TextUtils.isEmpty(idleCacheStr)){
                        MainApplicationContext.IS_IDLE_CACHE=true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    //初始化用户设置的清晰度
                    String qPos = CacheManager.getInstance().getLocalHandler().get("quality_info");
                    if (qPos != null && qPos.length() > 0) {
                        MainApplicationContext.CURRENT_QUALITY = qPos;
                    }
                }catch (Exception ex){}
                try {//初始化是否打开硬件解码
                    String openMediaStr = CacheManager.getInstance().getLocalHandler().get("media_open");
                    if(!TextUtils.isEmpty(openMediaStr)) {//设置过了
                        if (openMediaStr.equals("0")) {//不开启
                            MainApplicationContext.mediaCodecEnabled = false;
                        } else {
                            MainApplicationContext.mediaCodecEnabled = true;
                        }
                    }
                }catch (Exception ex){}
                try{
                    //初始化用户设置的下载配置 仅wifi
                    String openMediaStr = CacheManager.getInstance().getLocalHandler().get("only_wifi");
                    if(!TextUtils.isEmpty(openMediaStr)) {
                        if (openMediaStr.equals("0")){//设置了不开启
                            MainApplicationContext.onlyWifiDownBoo = false;
                        } else{
                            MainApplicationContext.onlyWifiDownBoo = true;
                        }
                    }
                }catch (Exception ex){}
                String root=MainApplicationContext.M3U8_PATH;
                List<TaskModel> taskModels= dbHandler.getTasks();
                if(taskModels!=null&&taskModels.size()>0) {
                    for (TaskModel taskModel : taskModels) {
                        String rPath=root+taskModel.getTaskId();
                        File file=new File(rPath);
                        long curSize=0;
                        if(file.isDirectory()){
                            File[] files=file.listFiles();
                            for(File f:files){
                                if(f.getName().contains(".ts")){
                                    curSize+=f.length();
                                }
                            }
                        }
                        com.jay.config.Config config=new com.jay.config.Config();
                        config.settId(taskModel.getTaskId());
                        config.setmId(taskModel.getShortId());
                        config.setmName(taskModel.getTitle());
                        config.setmConver(taskModel.getConver());
                        config.setmUrl(taskModel.getUrl());
                        config.setKey(taskModel.getvKey());
                        config.setcSize(curSize);
                        config.setmSize(Long.valueOf(taskModel.getFileSize()));
                        double pro=curSize*1.0/config.getmSize()*100;
                        config.setProgress((int) pro);
                        config.setPath(MainApplicationContext.M3U8_PATH+"/"+config.gettId()+"/"+config.gettId()+".ini");
                        config.setStatus(Status.STOP);
                        if(config.getProgress()>=100){
                            config.setStatus(Status.COMPLETE);
                        }
                        //保存文件信息
                        config.save();
                        //删除数据库信息,不删除本地文件
                        dbHandler.delete(taskModel);
                    }
                }
                //HttpServerManager.getInstance().getM3u8DownManager().setDownMax(3);
                HttpServerManager.getInstance().getM3u8DownManager().start();
//                HttpServerManager.getInstance().getM3u8DownManager().setDownMax(5);
//                Config config=MainApplicationContext.getConfig();
//                String oldDbStr=config.get("oldDB");//老数据库没有数据了
//                if(oldDbStr.length()>0){
//                    return;
//                }
//                //初始化数据  老数据
//                List<com.xcore.cache.CacheModel> deletes=new ArrayList<>();//要删除的数据
//
//                List<com.xcore.cache.CacheModel> cacheModels=dbHandler.getDowns();
//                if(cacheModels!=null&&cacheModels.size()>0) {
//                    for (com.xcore.cache.CacheModel cacheModel : cacheModels) {
//                        int percent = Integer.valueOf(cacheModel.getPercent());
//                        if (percent >= 50) {
//                            //判断文件存不存在,如果不存在就把下载信息设置为0
//                            String sPath = MainApplicationContext.SD_PATH;
//                            String sName = cacheModel.getStreamId();//得到文件夹名
//                            String sUrl = cacheModel.getUrl();//得到种子名
//                            int vIndex=sUrl.lastIndexOf("/");
//                            sUrl=sUrl.substring(vIndex+1);
//                            sUrl=sUrl.replace(".Torrent",".xv");
//                            sUrl=sUrl.replace(".torrent",".xv");
//                            String fileName = sPath + sName + "/" + sUrl;
//                            File f = new File(fileName);
//                            if (f.exists()) {
//                                M3U8TaskModel model=new M3U8TaskModel();
//                                model.setKey("xvxvxvxv");
//                                model.setUrl(fileName);
//                                model.setStatus(M3U8TaskModel.TaskStatus.COMPLETE);
//                                model.setTaskId(cacheModel.getShortId());
//                                model.setConver(cacheModel.getConver());
//                                model.setOldTask(true);
//                                try {
//                                    long tSize = Long.valueOf(cacheModel.getTotalSize());
//                                    model.setFileSize(tSize);
//                                }catch (Exception ex){}
//
//                                model.setTitle(cacheModel.getName());
//                                model.setShortId(cacheModel.getShortId());
//                                model.setPrent(percent);
//                                M3u8DownTaskManager.getInstance().init(model);
//                            }
//                        }else{
//                            deletes.add(cacheModel);
//                        }
//                    }
//                    if(deletes.size()>0){
//                        CacheManager.getInstance().getLocalDownLoader().deleteDb(deletes);
//                    }
//                }else{
//                    config.put("oldDB","1");
//                    config.save();
//                }
            }
        });
    }
//    //更新
//    public void update(CacheModel cacheModel){
//        final TaskModel model=new TaskModel();
//        model.setConver(cacheModel.getConver());
//        model.setPercent(cacheModel.getPrent()+"");
//        model.setTaskId(cacheModel.getTaskId());
//        model.setUrl(cacheModel.getUrl());
//        model.setTitle(cacheModel.getTitle());
//        update(model);
//    }
    //更新
//    public void update(final TaskModel model){
//        cachedThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                dbHandler.update(model);
//            }
//        });
//    }
//    public void delete(CacheModel cacheModel){
//        final TaskModel model=new TaskModel();
//        model.setTaskId(cacheModel.getTaskId());
//        cachedThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                dbHandler.delete(model);
//            }
//        });
//    }
}
