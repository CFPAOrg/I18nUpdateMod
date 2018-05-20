import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.junit.Test;

import java.io.File;

public class TestDownloader {

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
}
