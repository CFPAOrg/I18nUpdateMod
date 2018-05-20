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
import org.cfpa.i18nupdatemod.download.MainDownloader;
import org.cfpa.i18nupdatemod.key.ReportKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    public void construct(FMLConstructionEvent event) {
        //resourceDownloader();
        applyOption();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ReportKey();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new NoticeCommand());
    }

    public void resourceDownloader() {
        Minecraft mc = Minecraft.getMinecraft();

        //记录开始时间
        long startTime = System.currentTimeMillis();
        try {
            MainDownloader.downloadResource("http://ys-i.ys168.com/604554341/TKfTkKq2K6K4T5IK1MON/Minecraft-Mod-Language-Modpack.zip", "Minecraft-Mod-Language-Modpack.zip", mc.getResourcePackRepository().getDirResourcepacks().toString());
            logger.info("下载成功！");
        } catch (IOException e) {
            logger.error("下载失败！");
            e.printStackTrace();
        }

        //记录结束时间
        long endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;
        logger.info("花费时间：" + excTime + "秒");
    }

    public void applyOption() {
        // 应用修改
        createOptionFile();

        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;

        // 因为这时候资源包已经加载了，所以需要重新读取，重新加载
        ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
        resourcePackRepository.updateRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntriesAll = resourcePackRepository.getRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
        Iterator<String> it = gameSettings.resourcePacks.iterator();

        // 此时情况是这样的
        // entry 为修改后的条目
        // repositoryEntries 为游戏应当加载的条目
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
                logger.error(e.getStackTrace().toString());
                logger.error("无法创建配置文件");
            }
        }
        if (option.exists() && !mc.gameSettings.resourcePacks.contains("Minecraft-Mod-Language-Modpack.zip")) {
            mc.gameSettings.resourcePacks.add("Minecraft-Mod-Language-Modpack.zip");
        }
    }
}
