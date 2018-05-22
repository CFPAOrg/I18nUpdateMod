package org.cfpa.i18nupdatemod;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
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
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;


@Mod(modid = I18nUpdateMod.MODID, name = I18nUpdateMod.NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.12]", version = I18nUpdateMod.VERSION)
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "1.0.0";

    public static final Logger logger = LogManager.getLogger(MODID);

    @Mod.Instance
    public static I18nUpdateMod INSTANCE;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) throws InterruptedException, NoSuchAlgorithmException {
        // 初始化HashChecker
        HashChecker.init();

        // 如果文件已经可用则直接跳过下载
        if (hashCheck()) {
            logger.info("检测到资源包可用，跳过下载阶段");
            setupResourcesPack();
            return;
        }

        DownloadManager downloader = new DownloadManager("https://covertdragon.team/i18n/mmlp.zip", "Minecraft-Mod-Language-Modpack.zip", Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
        DownloadWindow window = new DownloadWindow(downloader);
        window.showWindow();
        downloader.start();
        // 阻塞主线程
        while (true) {
            if (downloader.isDone()) {
                break;
            }
            Thread.sleep(50);
        }
        if (downloader.getStatus() == DownloadStatus.SUCCESS) {
            setupResourcesPack();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ReportKey();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new NoticeCommand());
    }

    public void setupResourcesPack() {
        // 也许我们可以不改变options.txt只添加资源包？
        //createOptionFile();

        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;

        // 在gameSetting中加载资源包
        if (!gameSettings.resourcePacks.contains("Minecraft-Mod-Language-Modpack.zip")) {
            mc.gameSettings.resourcePacks.add("Minecraft-Mod-Language-Modpack.zip");
        }

        // 因为这时候资源包已经加载了，所以需要重新读取，重新加载
        ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
        resourcePackRepository.updateRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntriesAll = resourcePackRepository.getRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
        Iterator<String> it = gameSettings.resourcePacks.iterator();

        /*
         此时情况是这样的
         entry 为修改后的条目
         repositoryEntries 为游戏应当加载的条目
        */
        while (it.hasNext()) {
            String packName = it.next();
            for (ResourcePackRepository.Entry entry : repositoryEntriesAll) {
                if (entry.getResourcePackName().equals(packName)) {
                    // packFormat 为 3，或者 incompatibleResourcePacks 条目中有的资源包才会加入
                    if (entry.getPackFormat() == 3 || gameSettings.incompatibleResourcePacks.contains(entry.getResourcePackName())) {
                        repositoryEntries.add(entry);
                        break;
                    }
                    // 否则移除
                    it.remove();
                    logger.warn("移除资源包 {}，因为它无法兼容当前版本", entry.getResourcePackName());
                }
            }
        }
        resourcePackRepository.setRepositories(repositoryEntries);
    }

    private boolean hashCheck() {
        String hashExpected;
        try {
            URL url = new URL("https://covertdragon.team/i18n/hash");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            hashExpected = IOUtils.readLines(connection.getInputStream(), StandardCharsets.UTF_8).get(0);
        } catch (Throwable e) {
            logger.warn("获取Hash信息失败！");
            return false;
        }
        try {
            return HashChecker.checkMD5(new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), "Minecraft-Mod-Language-Modpack.zip"), hashExpected);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    public void createOptionFile() {
        Minecraft mc = Minecraft.getMinecraft();

        File option = new File(mc.mcDataDir, "options.txt");
        if (!option.exists()) {
            try {
                FileOutputStream optionFos = new FileOutputStream(option);
                OutputStreamWriter writer = new OutputStreamWriter(optionFos);
                writer.append("resourcePacks:[\"Minecraft-Mod-Language-Modpack.zip\"]");
                writer.close();
                optionFos.close();
            } catch (IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
                logger.error("无法创建配置文件");
            }
        }
        if (option.exists() && !mc.gameSettings.resourcePacks.contains("Minecraft-Mod-Language-Modpack.zip")) {
            mc.gameSettings.resourcePacks.add("Minecraft-Mod-Language-Modpack.zip");
        }
    }
    */
}
