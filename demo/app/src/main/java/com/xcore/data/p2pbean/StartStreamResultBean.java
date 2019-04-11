package com.xcore.data.p2pbean;

import com.xcore.data.CmModelBean;
import java.io.Serializable;

public class StartStreamResultBean extends CmModelBean {

    private StreamInfo data;

    public StreamInfo getStream() {
        return data;
    }
    public void setStream(StreamInfo stream) {
        this.data = stream;
    }

    public static class StreamInfo implements Serializable{
        private String id;
        private int downloadSpeed;
        private int btStatus;
        private String selectedFilePath;
        private long downloadedBytes;
        private long totalBytes;
        private int percent;
        private int connCountDown;
        private int connCountTotal;
        private int pieceCount;
        private int pieceSize;


        private long serverPayloadSpeed;//从服务器下载的总的有效总速度
        private long serverTotalSpeed;//从服务器下载的总速度
        private long totalServerPayloadBytes;//从服务器下载的文件型数据总量
        private long totalServerBytes;//从服务器下载的所有数据总量
        private long totalPayloadBytesDown;//本次启动后总的下载的数据块类型的数据量(包括了服务器的数据,以能可能丢弃的数据)
        private long totalBytesDown;//本次启动后总的所有数据的下载数量


        @Override
        public String toString() {
            return "StreamInfo{" +
                    "id='" + id + '\'' +
                    ", downloadSpeed=" + downloadSpeed +
                    ", btStatus=" + btStatus +
                    ", selectedFilePath='" + selectedFilePath + '\'' +
                    ", downloadedBytes=" + downloadedBytes +
                    ", totalBytes=" + totalBytes +
                    ", percent=" + percent +
                    ", connCountDown=" + connCountDown +
                    ", connCountTotal=" + connCountTotal +
                    ", pieceCount=" + pieceCount +
                    ", pieceSize=" + pieceSize +
                    ", serverPayloadSpeed=" + serverPayloadSpeed +
                    ", serverTotalSpeed=" + serverTotalSpeed +
                    ", totalServerPayloadBytes=" + totalServerPayloadBytes +
                    ", totalServerBytes=" + totalServerBytes +
                    ", totalPayloadBytesDown=" + totalPayloadBytesDown +
                    ", totalBytesDown=" + totalBytesDown +
                    '}';
        }

        public long getServerPayloadSpeed() {
            return serverPayloadSpeed;
        }

        public void setServerPayloadSpeed(long serverPayloadSpeed) {
            this.serverPayloadSpeed = serverPayloadSpeed;
        }

        public long getServerTotalSpeed() {
            return serverTotalSpeed;
        }

        public void setServerTotalSpeed(long serverTotalSpeed) {
            this.serverTotalSpeed = serverTotalSpeed;
        }

        public long getTotalServerPayloadBytes() {
            return totalServerPayloadBytes;
        }

        public void setTotalServerPayloadBytes(long totalServerPayloadBytes) {
            this.totalServerPayloadBytes = totalServerPayloadBytes;
        }

        public long getTotalServerBytes() {
            return totalServerBytes;
        }

        public void setTotalServerBytes(long totalServerBytes) {
            this.totalServerBytes = totalServerBytes;
        }

        public long getTotalPayloadBytesDown() {
            return totalPayloadBytesDown;
        }

        public void setTotalPayloadBytesDown(long totalPayloadBytesDown) {
            this.totalPayloadBytesDown = totalPayloadBytesDown;
        }

        public long getTotalBytesDown() {
            return totalBytesDown;
        }

        public void setTotalBytesDown(long totalBytesDown) {
            this.totalBytesDown = totalBytesDown;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDownloadSpeed() {
            return downloadSpeed;
        }

        public void setDownloadSpeed(int downloadSpeed) {
            this.downloadSpeed = downloadSpeed;
        }

        public int getBtStatus() {
            return btStatus;
        }

        public void setBtStatus(int btStatus) {
            this.btStatus = btStatus;
        }

        public String getSelectedFilePath() {
            return selectedFilePath;
        }

        public void setSelectedFilePath(String selectedFilePath) {
            this.selectedFilePath = selectedFilePath;
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public void setDownloadedBytes(long downloadedBytes) {
            this.downloadedBytes = downloadedBytes;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public void setTotalBytes(long totalBytes) {
            this.totalBytes = totalBytes;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }

        public int getConnCountDown() {
            return connCountDown;
        }

        public void setConnCountDown(int connCountDown) {
            this.connCountDown = connCountDown;
        }

        public int getConnCountTotal() {
            return connCountTotal;
        }

        public void setConnCountTotal(int connCountTotal) {
            this.connCountTotal = connCountTotal;
        }

        public int getPieceCount() {
            return pieceCount;
        }

        public void setPieceCount(int pieceCount) {
            this.pieceCount = pieceCount;
        }

        public int getPieceSize() {
            return pieceSize;
        }

        public void setPieceSize(int pieceSize) {
            this.pieceSize = pieceSize;
        }
    }

}
