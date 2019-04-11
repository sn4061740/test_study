package demo.com.downmanager;

import com.hdl.m3u8.bean.OnDownloadListener;

public class DownListener implements OnDownloadListener {
    private String id;

    public String getId() {
        return id;
    }

    public DownListener(String id){
        this.id=id;
    }

    @Override
    public void onDownloading(long itemFileSize, int totalTs, int curTs) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onProgress(long curLength) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onError(Throwable errorMsg) {

    }
}
