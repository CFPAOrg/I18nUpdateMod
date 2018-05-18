package org.cfpa.i18nupdatemod.download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainDownloader {
    public static void downloadResource(String urlIn, String fileName, String dirPlace) throws IOException {
        // URL 转换
        URL url = new URL(urlIn);

        // 建立链接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 超时、代理设置
        connection.setConnectTimeout(5 * 1000);
        connection.setRequestProperty("", "");

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
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
