package org.cfpa.i18nupdatemod;

public interface IDownloadManager {

    void cancel();

    void background();

    boolean isDone();

    DownloadStatus getStatus();

    float getCompletePercentage();

    String getTaskTitle();

    public void start(String threadName);

}
