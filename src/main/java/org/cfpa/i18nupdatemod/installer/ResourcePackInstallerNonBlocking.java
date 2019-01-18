package org.cfpa.i18nupdatemod.installer;

import net.minecraft.client.Minecraft;
import org.cfpa.i18nupdatemod.I18nConfig;
import org.cfpa.i18nupdatemod.download.DownloadInfoHelper;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadWindow;

public class ResourcePackInstallerNonBlocking extends ResourcePackInstaller {
    @Override
    public void install() {
        super.install();
        if (updateResourcePack) {
            DownloadManager downloader = new DownloadManager(I18nConfig.download.langPackURL, I18nConfig.download.langPackName, Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
            DownloadWindow window = new DownloadWindow(downloader);
            window.showWindow();
            downloader.start("I18n-Download-Thread");
            downloader.setSuccessTask(() -> {
                setResourcesRepository();
                Minecraft.getMinecraft().getLanguageManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
                DownloadInfoHelper.info.add("资源包更新成功。");
            });
        }
    }
}
