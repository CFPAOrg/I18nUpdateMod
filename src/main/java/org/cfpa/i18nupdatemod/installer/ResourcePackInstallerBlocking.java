package org.cfpa.i18nupdatemod.installer;

import net.minecraft.client.Minecraft;
import org.cfpa.i18nupdatemod.I18nConfig;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.DownloadWindow;

public class ResourcePackInstallerBlocking extends ResourcePackInstaller {

    @Override
    public void install() {
        super.install();
        if (updateResourcePack) {
            DownloadManager downloader = new DownloadManager(I18nConfig.download.langPackURL, I18nConfig.download.langPackName, Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
            DownloadWindow window = new DownloadWindow(downloader);
            window.showWindow();
            downloader.start("I18n-Download-Thread");
            while (downloader.getStatus() == DownloadStatus.DOWNLOADING) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignore) {
                }
            }
            if (downloader.getStatus() == DownloadStatus.SUCCESS) {
                setResourcesRepository();
            }
        }
    }
}
