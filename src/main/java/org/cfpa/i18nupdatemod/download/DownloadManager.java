package org.cfpa.i18nupdatemod.download;

import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {
    private Thread downloadThread;
    private MainDownloader downloader;
    private DownloadStatus status = DownloadStatus.DOWNLOADING;

    /**
     * 下载管理器
     * @param urlIn 下载目标的URL地址
     * @param fileNameIn 存储文件的名字
     * @param dirIn 存储文件的地址
     */
    public DownloadManager(String urlIn, String fileNameIn, String dirIn) {
        try {
            downloader = new MainDownloader(urlIn, fileNameIn, dirIn);
        } catch (IOException e) {
            catching(e);
        }
    }

    /**
     * 开始下载
     */
    public void start() {
        downloadThread = new Thread(() -> {
            try {
                downloader.downloadResource();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, "I18n-Download-Thread");
        downloadThread.start();
    }

    private void catching(Throwable e) {
        I18nUpdateMod.logger.error("下载失败", e);
        status = DownloadStatus.FAIL;
        downloader.done = true;
    }

    /**
     * 获得下载的状态
     * SUCCESS：下载成功
     * DOWNLOADING：正在下载
     * FAIL：下载遇到错误
     * @return 下载状态
     */
    public DownloadStatus getStatus() {
        if (status == DownloadStatus.DOWNLOADING && downloader.done) {
            status = DownloadStatus.SUCCESS;
        }
        return status;
    }

    /**
     * 下载是否结束
     * @return 下载是否结束
     */
    public boolean isDone() {
        return downloader.done;
    }

    /**
     * 获得下载完成百分比
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
        public float completePercentage = 0.0F;

        public MainDownloader(String urlIn, String fileName, String dirPlace) throws IOException {
            this.url = new URL(urlIn);
            this.fileName = fileName;
            this.dirPlace = dirPlace;
        }

        public void downloadResource() throws Throwable {
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
            File saveDir = new File(dirPlace);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(getData);
            fos.close();
            inputStream.close();
            done = true;
        }

        private byte[] readInputStream(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[64];
            int len;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                downloadedSize += len;
                completePercentage = (float) downloadedSize / (float) size;
            }
            bos.close();
            return bos.toByteArray();
        }
    }
}
