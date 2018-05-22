import org.cfpa.i18nupdatemod.HashChecker;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestHashChecker {
    @Test
    public void testHashChecker() throws Throwable {
        String dir = System.getProperty("user.dir");
        dir = dir + File.separator + "run" + File.separator + "test";
        File f = new File(dir, "test.zip");
        HashChecker.init();
        if (!HashChecker.checkMD5(f, "e581d71b2e4a9fd90764e4cd13fc1b68")) {
            DownloadManager downloader = new DownloadManager("https://covertdragon.team/test/test.zip", "test.zip", dir);
            downloader.start();
            DownloadWindow handler = new DownloadWindow(downloader);
            handler.showWindow();
            while (!downloader.isDone()) Thread.sleep(50);
        }
        Assert.assertEquals(HashChecker.checkMD5(f, "e581d71b2e4a9fd90764e4cd13fc1b68"), true);
    }
}
