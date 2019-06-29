package org.cfpa.i18nupdatemod;

import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.cfpa.i18nupdatemod.download.FileDownloadManager;

public class Test {

	public static void main(String[] args) {
		FileDownloadManager downloader = new FileDownloadManager(I18nConfig.download.langPackURL, I18nConfig.download.langPackName, "/Users/liuxinyuan/Desktop/a");
		DownloadWindow window = new DownloadWindow(downloader);
        window.showWindow();
        downloader.start("I18n-Download-Thread");
        while (downloader.getStatus() == DownloadStatus.DOWNLOADING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
        }
        

	}

}
