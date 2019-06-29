package org.cfpa.i18nupdatemod.download;

import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadManager implements IDownloadManager {
    private Thread downloadThread;
    private MainDownloader downloader;
    private DownloadStatus status = DownloadStatus.IDLE;

    /**
     * 下载管理器
     *
     * @param urlIn      下载目标的URL地址
     * @param fileNameIn 存储文件的名字
     * @param dirIn      存储文件的地址
     */
    public FileDownloadManager(String urlIn, String fileNameIn, String dirIn) {
        try {
            downloader = new MainDownloader(urlIn, fileNameIn, dirIn);
        } catch (IOException e) {
            catching(e);
        }
    }

    public void setSuccessTask(Runnable successTask) {
        this.downloader.successTask = successTask;
    }

    /**
     * 开始下载
     *
     * @param threadName 线程名称
     */
    public void start(String threadName) {
        status = DownloadStatus.DOWNLOADING;
        downloadThread = new Thread(() -> {
            try {
                downloader.downloadResource();
            } catch (Throwable e) {
                catching(e);
            }
        }, threadName);
        downloadThread.start();
    }

    public void cancel() {
        downloader.done = true;
        status = DownloadStatus.CANCELED;
        downloader.alive = false;
    }

    public void background() {
        status = DownloadStatus.BACKGROUND;
    }

    private void catching(Throwable e) {
        I18nUpdateMod.logger.error("下载失败", e);
        DownloadInfoHelper.info.add("资源包更新失败。");
        status = DownloadStatus.FAIL;
        downloader.done = true;
    }

    /**
     * 获得下载的状态
     * SUCCESS：下载成功
     * DOWNLOADING：正在下载
     * FAIL：下载遇到错误
     * CANCELED：下载被玩家取消
     *
     * @return 下载状态
     */
    public DownloadStatus getStatus() {
        if ((status == DownloadStatus.DOWNLOADING || status == DownloadStatus.BACKGROUND) && downloader.done) {
            status = DownloadStatus.SUCCESS;
        }
        return status;
    }

    /**
     * 下载是否结束
     *
     * @return 下载是否结束
     */
    public boolean isDone() {
        return downloader.done;
    }

    /**
     * 获得下载完成百分比
     *
     * @return 下载完成的百分比
     */
    public float getCompletePercentage() {
        return downloader.completePercentage;
    }

    private static class MainDownloader {
        private URL url;
        private String fileName;
        private String dirPlace;
        private int size = 0;
        private int downloadedSize = 0;
        private boolean done = false;

        Runnable successTask;

        public float completePercentage = 0.0F;
        public boolean alive = true;

        MainDownloader(String urlIn, String fileName, String dirPlace) throws IOException {
            this.url = new URL(urlIn);
            this.fileName = fileName;
            this.dirPlace = dirPlace;
        }

        void downloadResource() throws Throwable {
            // 建立链接
            URLConnection connection = url.openConnection();

            // 超时
            connection.setConnectTimeout(10 * 1000);

            //获取文件总大小
            size = connection.getContentLength();

            // 开始获取输入流
            InputStream inputStream = connection.getInputStream();
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            if (getData != null) {
                File saveDir = new File(dirPlace);
                if (!saveDir.exists()) {
                    saveDir.mkdir();
                }
                File file = new File(saveDir + File.separator + fileName);
                FileOutputStream fos = new FileOutputStream(file);

                fos.write(getData);
                fos.close();
                inputStream.close();
            }
            done = true;
            if (successTask != null) {
                successTask.run();
            }
        }

        private byte[] readInputStream(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[64];
            int len;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                downloadedSize += len;
                completePercentage = (float) downloadedSize / (float) size;
                if (!alive) {
                    return null;
                }
            }
            bos.close();
            return bos.toByteArray();
        }
    }

	@Override
	public String getTaskTitle() {
		return "正在下载...";
	}
}
