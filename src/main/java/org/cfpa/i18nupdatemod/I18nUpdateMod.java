package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.command.NoticeCommand;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.cfpa.i18nupdatemod.key.ReportKey;

import static org.cfpa.i18nupdatemod.I18nUtils.checkLength;
import static org.cfpa.i18nupdatemod.I18nUtils.setupResourcesPack;


@Mod(modid = I18nUpdateMod.MODID, name = I18nUpdateMod.NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.12]", version = I18nUpdateMod.VERSION)
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "1.0.0";

    public static final Logger logger = LogManager.getLogger(MODID);

    @Mod.Instance
    public static I18nUpdateMod INSTANCE;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) throws InterruptedException {
        // 如果文件已经可用则直接跳过下载
        if (checkLength()) {
            logger.info("检测到资源包可用，跳过下载阶段");
            setupResourcesPack();
        } else {
            // 开始下载资源包并弹出进度窗口
            DownloadManager downloader = new DownloadManager("http://p985car2i.bkt.clouddn.com/Minecraft-Mod-Language-Modpack.zip", "Minecraft-Mod-Language-Modpack.zip", Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
            DownloadWindow window = new DownloadWindow(downloader);
            window.showWindow();
            downloader.start();

            // 阻塞主线程，以保证资源包在preInit阶段被安装
            while (!downloader.isDone()) Thread.sleep(50);

            // 如果下载成功就安装资源包
            if (downloader.getStatus() == DownloadStatus.SUCCESS) {
                setupResourcesPack();
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ReportKey();
        ClientCommandHandler.instance.registerCommand(new NoticeCommand());
    }
}
