import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.cfpa.i18nupdatemod.download.FileDownloadManager;
import org.junit.Test;

import java.io.File;

public class TestDownloader {
    /*
    @Test
    public void testDownloader() throws Throwable {
        String dir = System.getProperty("user.dir");
        dir = dir + File.separator + "run" + File.separator + "test";
        DownloadManager downloader = new DownloadManager("https://covertdragon.team/test/test.zip", "test.zip", dir);
        downloader.start();
        while (!downloader.isDone()) {
            System.out.println(downloader.getCompletePercentage() * 100 + "% Done");
            Thread.sleep(50);
        }
    }
    */
    /*
    @Test
    public void testDownloadWindow() throws InterruptedException {
        String dir = System.getProperty("user.dir");
        dir = dir + File.separator + "run" + File.separator + "test";
        DownloadManager downloader = new DownloadManager("https://covertdragon.team/test/test.zip", "test.zip", dir);
        downloader.start();
        DownloadWindow handler = new DownloadWindow(downloader);
        handler.showWindow();
        while (!downloader.isDone()) Thread.sleep(50);
    }
    */
}
