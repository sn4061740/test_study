package demo.com.downmanager;

public interface IDownListenner {

    void onStart();
    void onProgress(int progress);//进度
    void onCurrentSize(long curSize);//当前大小
    void onSpeed(long speed);
    void onSuccess();
    void onError(Throwable errorMsg);
}
