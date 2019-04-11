package demo.com.downmanager;

import com.hdl.m3u8.M3U8DownloadTask;
import com.hdl.m3u8.bean.OnDownloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownManager {
    private static DownManager instance=new DownManager();
    public static DownManager getInstance() {
        return instance;
    }

    Map<String,M3U8DownloadTask> taskMaps=new HashMap<>();
    //对每个任务调用的所有监听
    Map<String,List<IDownListenner>> listennerMaps=new HashMap<>();

    /**
     * 开始下载
     * @param downUrl  下载url
     */
    public void start(String downUrl){
        String taskId=MD5Utils.MD5Encode(downUrl);
        M3U8DownloadTask task= taskMaps.get(taskId);
        if(task==null){
            task=new M3U8DownloadTask(taskId);
        }
        task.download(downUrl,new OnDownTaskListener(taskId));
    }

    /**
     * 添加任务监听
     * @param taskId
     * @param iDownListenner
     */
    public synchronized void addTaskListenner(String taskId,IDownListenner iDownListenner){
        List<IDownListenner> downListenners= listennerMaps.get(taskId);
        if(downListenners==null){
            downListenners=new ArrayList<>();
        }
        downListenners.add(iDownListenner);
    }

    public void stop(String taskId){
        M3U8DownloadTask task= taskMaps.get(taskId);
        task.stop();
    }

    public void remove(String taskId,IDownListenner iDownListenner){
        List<IDownListenner> downListenners= listennerMaps.get(taskId);
        if(downListenners!=null) {
            downListenners.remove(iDownListenner);
        }
    }


    class OnDownTaskListener implements OnDownloadListener{
        private String taskId;
        private long lastLeng=0;

        public OnDownTaskListener(String taskId){
            this.taskId=taskId;
        }
        @Override
        public void onDownloading(long itemFileSize, int totalTs, int curTs) {
             double v=curTs*1.0/totalTs*100;
             int progress= (int) v;

            List<IDownListenner> downListenners= listennerMaps.get(taskId);
            if(downListenners!=null){
                for(IDownListenner downListenner:downListenners){
                    downListenner.onProgress(progress);
                }
            }
        }
        @Override
        public void onSuccess() {
            List<IDownListenner> downListenners= listennerMaps.get(taskId);
            if(downListenners!=null){
                for(IDownListenner downListenner:downListenners){
                    downListenner.onSuccess();
                }
            }
        }
        @Override
        public void onProgress(long curLength) {
            long curSize=curLength-lastLeng;
            if(curSize>0){
                List<IDownListenner> downListenners= listennerMaps.get(taskId);
                if(downListenners!=null){
                    for(IDownListenner downListenner:downListenners){
                        downListenner.onSpeed(curSize);
                        downListenner.onCurrentSize(curLength);
                    }
                }
            }
        }
        @Override
        public void onStart() {
            List<IDownListenner> downListenners= listennerMaps.get(taskId);
            if(downListenners!=null){
                for(IDownListenner downListenner:downListenners){
                    downListenner.onStart();
                }
            }
        }
        @Override
        public void onError(Throwable errorMsg) {
            List<IDownListenner> downListenners= listennerMaps.get(taskId);
            if(downListenners!=null){
                for(IDownListenner downListenner:downListenners){
                    downListenner.onError(errorMsg);
                }
            }
        }
    }

}
