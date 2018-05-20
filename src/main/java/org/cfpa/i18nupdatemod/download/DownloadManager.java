package org.cfpa.i18nupdatemod.download;

import org.cfpa.i18nupdatemod.I18nUpdateMod;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class DownloadManager {
    private Thread downloadThread;
    private MainDownloader downloader;
    private DownloadStatus status = DownloadStatus.DOWNLOADING;

    public DownloadManager(String urlIn, String fileNameIn, String dirIn) {
        try {
            downloader = new MainDownloader(urlIn, fileNameIn, dirIn);
        } catch (IOException e) {
            catching(e);
        }
    }

    public void start() {
        downloadThread = new Thread(() -> {
            try {
                downloader.downloadResource();
            } catch (Throwable e) {
                catching(e);
            }
        }, "I18n-Download-Thread");
        downloadThread.start();
    }

    private void catching(Throwable e) {
        I18nUpdateMod.logger.error("下载失败", e);
        status = DownloadStatus.FAIL;
        downloader.done = true;
    }

    public DownloadStatus getStatus() {
        if (status == DownloadStatus.DOWNLOADING && downloader.done) {
            status = DownloadStatus.SUCCESS;
        }
        return status;
    }

    public boolean isDone() {
        return downloader.done;
    }

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
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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
