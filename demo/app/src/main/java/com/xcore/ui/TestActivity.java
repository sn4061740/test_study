package com.xcore.ui;

import android.media.ViviTV.player.widget.DolitVideoView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jay.HttpServerManager;
import com.jay.config.DownConfig;
import com.jay.down.listener.IDownListener;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.down.M3u8CacheActivity;
import com.xcore.down.TaskModel;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    DolitVideoView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        String SD_PATH=getApplicationContext().getExternalCacheDir()+"/videos/";
//        MainApplicationContext.M3U8_PATH =SD_PATH.replace("/videos/","/www/");

//        String root = MainApplicationContext.M3U8_PATH;
//        HttpServerManager.getInstance().init(root);

//        M3u8Utils.getInstance().startServer();
        //CacheManager.getInstance().initDown();

        findViewById(R.id.playBtn).setOnClickListener(onClickListener);
        findViewById(R.id.downBtn).setOnClickListener(onClickListener);
        findViewById(R.id.downStopBtn).setOnClickListener(onClickListener);
        findViewById(R.id.deleteBtn).setOnClickListener(onClickListener);
        findViewById(R.id.btn_view).setOnClickListener(onClickListener);
        findViewById(R.id.btn_addDB).setOnClickListener(onClickListener);
        findViewById(R.id.btn_selectDB).setOnClickListener(onClickListener);
        findViewById(R.id.btn_selectAllTable).setOnClickListener(onClickListener);

        player=findViewById(R.id.player);
        player.setIsHardDecode(false);
        player.setMediaCodecEnabled(true);

    }

    private void replaceStr(String str){
        String v="";
        if(str.length()<6){
            v=str.substring(2);
        }else {
            v = str.substring(2, str.length() - 2);
        }
        String s="";
        for(int i=0;i<v.length();i++){
            s+="*";
            if(s.length()>6){
                break;
            }
        }
        v=str.replace(v,s);
        Log.e("TAG","v="+v);
    }

    public String Ping(String str) {
        replaceStr("这里用113哈哈哈哈哈哈哈哈。。。。。。dsfaf");
        final String url=str;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resault = "";
                Process p;
                try {
                    //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
                    p = Runtime.getRuntime().exec("ping -c 3 -w 5 " + url);
                    int status = p.waitFor();
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer buffer = new StringBuffer();
//            String line = "";
//            while ((line = in.readLine()) != null){
//                buffer.append(line);
//            }
//            Log.e("TAG","Return ============" + buffer.toString());
                    if (status == 0) {
                        resault = "success";
                    } else {
                        resault = "faild";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("TAG",url+"结果="+resault);
            }
        }).start();
        return "";
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.playBtn:
                    play();
                break;
                case R.id.downBtn:
                    down();
                    break;
                case R.id.downStopBtn:
//                    M3U8TaskModel model= new M3U8TaskModel();
//                    model.setUrl("http://m01.xxxlutubexxx.com/m3u8/xsp1545724908-480.m3u8");
//                    M3u8DownTaskManager.getInstance().stopTask(model.getTaskId());
                    stopDown();
                    break;
                case R.id.deleteBtn:
//                    M3U8TaskModel model1= new M3U8TaskModel();
//                    model1.setUrl("http://m01.xxxlutubexxx.com/m3u8/xsp1545724908-480.m3u8");
//                    M3u8DownTaskManager.getInstance().deleteTask(model1.getTaskId());
                    break;
                case R.id.btn_view:
                    M3u8CacheActivity.toActivity(TestActivity.this);
                    break;
                case R.id.btn_addDB:
                    addDb();
                    break;
                case R.id.btn_selectDB:
                    selectDb();
                    break;
                case R.id.btn_selectAllTable:
                    String[] vList=new String[]{"m01.xxxlutubexxx.com:80","api01.1avapi.com:80"};
                    for(String vs:vList) {
                        Ping(vs);
                    }
                    break;
            }
        }
    };
    private void addDb(){
//        TaskModel taskModel=new TaskModel();
//        taskModel.setFileSize("10000023");
//        taskModel.setShortId("q");
//        taskModel.setTitle("测试");
//        taskModel.setPercent("100");
//        taskModel.setUrl("http://m01.xxxlutubexxx.com/m3u8/xsp1545724908-480.m3u8");
//        taskModel.setConver("ss");
//        taskModel.setvKey("b24w9AwfAspg3QfpWUGuTPZCi8E48v8P");
//        taskModel.setTaskId(taskModel.getTaskIdByUrl());

        //CacheManager.getInstance().getM3U8DownLoader().update(taskModel);
    }
    private void selectDb(){

    }
    private void play(){
        String url="http://m01.xxxxapixxx.com/m3u8/xsp1545998826-480.m3u8?code=dfasdfsdafsdffa";
        url="http://m01.xxxxapixxx.com/m3u8/xsp1550173745-hd.m3u8";
        player.setVideoPath("http://127.0.0.1:"+HttpServerManager.PORT+"/play?v1=b24w9AwfAspg3QfpWUGuTPZCi8E48v8P&uri={"+url+"}");
        player.start();
    }

    private void stopDown(){
        HttpServerManager.getInstance().stopAll();
    }

    private void down(){
//        M3U8TaskModel model= new M3U8TaskModel();
//        model.setUrl("http://m01.xxxlutubexxx.com/m3u8/xsp1545724908-480.m3u8");
//        model.setKey("b24w9AwfAspg3QfpWUGuTPZCi8E48v8P");
//        model.setShortId("86c");
//        M3u8DownTaskManager.getInstance().addTask(model);

//        M3u8DownLoaderManager.getInstance().startDown("");

//        DownConfig downConfig1 = new DownConfig();
////		https://media.w3.org/2010/05/sintel/trailer.mp4
//        // http://m01.xxxxapixxx.com/m3u8/xsp1545998826-480.m3u8?code=dfasdfsdafsdffa
//        downConfig1.setDownUrl("http://m01.xxxlutubexxx.com/m3u8/xsp1545724908-480.m3u8");
//        downConfig1.setV1("b24w9AwfAspg3QfpWUGuTPZCi8E48v8P");
//        downConfig1.setRoot(MainApplicationContext.M3U8_PATH);
//        downConfig1.setThreadNum(3);

//		HttpServerManager.getInstance().addListener(downConfig1.getId(), downListener1);
//		HttpServerManager.getInstance().startDown(downConfig1);

        DownConfig downConfig2 = new DownConfig();
        downConfig2.setDownUrl("http://m01.xxxxapixxx.com/m3u8/xsp1550173745-hd.m3u8");
        downConfig2.setV1("b24w9AwfAspg3QfpWUGuTPZCi8E48v8P");
        downConfig2.setRoot(MainApplicationContext.M3U8_PATH);
        downConfig2.setThreadNum(3);

        downConfig2.setmConver("xsp1550172875/bigV1.jpg?u=2019/2/15%2015:30:43");
        downConfig2.setmSize(217275536);
        downConfig2.setmName("Milla Fingers Her Milky Body");
        downConfig2.setmId("ala");

        HttpServerManager.getInstance().startDown(downConfig2);
        HttpServerManager.getInstance().addListener(downConfig2.getId(), downListener1);
    }


    IDownListener downListener1=new IDownListener() {
        @Override
        public void onStart() {
            System.out.println("MAIN ... 获取资源：：");
        }

        @Override
        public void onDownloadSize(long l) {
            System.out.println("MAIN ... 当前下载大小："+l);
        }

        @Override
        public void onProgress(int progress) {
            System.out.println("MAIN ... 进度：：" + progress);
        }

        @Override
        public void onSpeed(long speed) {
            System.out.println("MAIN  速度：：" + speed);
        }

        @Override
        public void onSuccess(long file) {
            System.out.println("MAIN  成功" + file);
        }

        @Override
        public void onDownError(int code, Throwable e) {
            System.out.println("MAIN 下载出错:"+code+"--"+e.getMessage());
        }

        @Override
        public void onStop() {
            System.out.println("MAIN 停止下载");
        }

        @Override
        public void onComplete() {
            System.out.println("MAIN 全部下载完成");
        }

        @Override
        public void onWait() {
            System.out.println("等待下载");
        }
    };
}
