package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.config.MainConfig;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.cfpa.i18nupdatemod.notice.CmdNotice;
import org.cfpa.i18nupdatemod.report.CmdReport;
import org.cfpa.i18nupdatemod.report.ReportKey;

import static org.cfpa.i18nupdatemod.I18nUtils.*;


@Mod(modid = I18nUpdateMod.MODID, name = I18nUpdateMod.NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.12]", version = I18nUpdateMod.VERSION)
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "@VERSION";

    public static final Logger logger = LogManager.getLogger(MODID);

    @Mod.Instance
    public static I18nUpdateMod INSTANCE;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) throws InterruptedException {
        // 首先检测文件是否超过阈值
        if (!intervalDaysCheck()) {
            logger.info("未到下次更新时间，跳过检测和下载阶段");
            setupResourcesPack();
        }
        // 如果文件已经可用则直接跳过下载
        else if (checkLength()) {
            logger.info("检测到资源包可用，跳过下载阶段");
            setupResourcesPack();
        } else {
            // 开始下载资源包并弹出进度窗口
            DownloadManager downloader = new DownloadManager(MainConfig.download.langPackURL, MainConfig.download.langPackName, Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
            DownloadWindow window = new DownloadWindow(downloader);
            window.showWindow();
            downloader.start();

            // 阻塞主线程，以保证资源包在preInit阶段被安装
            int i = MainConfig.download.maxTime * 20;
            while (!downloader.isDone() && i >= 0) {
                Thread.sleep(50);
                if (i == 0) {
                    // 如果超时就隐藏窗口到后台下载并停止阻塞主线程
                    window.hide();
                }
                i--;
            }

            // 如果下载成功就安装资源包
            if (downloader.getStatus() == DownloadStatus.SUCCESS) {
                setupResourcesPack();
            }
        }

        // 变化语言为中文
        if (MainConfig.download.setupChinese) {
            setupLang();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ReportKey();
        ClientCommandHandler.instance.registerCommand(new CmdNotice());
        ClientCommandHandler.instance.registerCommand(new CmdReport());
    }
}
