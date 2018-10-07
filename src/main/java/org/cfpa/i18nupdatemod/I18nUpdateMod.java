package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.command.*;
import org.cfpa.i18nupdatemod.config.MainConfig;
import org.cfpa.i18nupdatemod.download.DownloadInfoHelper;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.DownloadWindow;
import org.cfpa.i18nupdatemod.hotkey.HotKeyHandler;

import static org.cfpa.i18nupdatemod.I18nUtils.*;


@Mod(modid = I18nUpdateMod.MODID, name = I18nUpdateMod.NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.12]", version = I18nUpdateMod.VERSION, dependencies = "after:defaultoptions")
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(MODID);

    // 通知变量
    public static boolean showNotice = false;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) throws InterruptedException {
        // 最开始，检查是否开资源包下载配置
        if (!MainConfig.download.shouldDownload) {
            return;
        }

        // 其次，检测是否启用国际化配置
        if (MainConfig.internationalization.openI18n && !isChinese()) {
            return;
        }

        DownloadInfoHelper.init();

        // 首先检测文件是否超过阈值
        if (!intervalDaysCheck()) {
            logger.info("未到下次更新时间，跳过检测和下载阶段");
            setupResourcesPack();
        }
        // 如果离线且文件可用则跳过下载
        else if ((!online()) && isResourcePackExist()) {
            logger.info("检测到网络不可用，跳过下载阶段");
            setupResourcesPack();
        }
        // 如果文件已经可用则直接跳过下载
        else if (checkLength()) {
            logger.info("检测到资源包最新，跳过下载阶段");
            setupResourcesPack();
        } else {
            // 决定显示通知
            showNotice = true;

            // 开始下载资源包并弹出进度窗口
            DownloadManager downloader = new DownloadManager(MainConfig.download.langPackURL, MainConfig.download.langPackName, Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
            DownloadWindow window = new DownloadWindow(downloader);
            window.showWindow();
            downloader.start("I18n-Download-Thread");

            // 阻塞主线程
            while (downloader.getStatus() == DownloadStatus.DOWNLOADING) Thread.sleep(50);

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
        // 国际化检查
        if (MainConfig.internationalization.openI18n && !isChinese()) {
            return;
        }

        // 命令注册
        ClientCommandHandler.instance.registerCommand(new CmdNotice());
        ClientCommandHandler.instance.registerCommand(new CmdReport());
        ClientCommandHandler.instance.registerCommand(new CmdReload());
        ClientCommandHandler.instance.registerCommand(new CmdGetLangpack());
        ClientCommandHandler.instance.registerCommand(new CmdUpload());
        ClientCommandHandler.instance.registerCommand(new CmdToken());

        // 键位检查
        if (!MainConfig.key.closedKey) {
            HotKeyHandler hotKeyHandler = new HotKeyHandler();
            hotKeyHandler.register();
            MinecraftForge.EVENT_BUS.register(new HotKeyHandler());
        }
    }
}
