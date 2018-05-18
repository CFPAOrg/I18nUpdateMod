package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.download.MainDownloader;


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
        resourceDownloader();
        applyOption();
    }

    public void resourceDownloader() {
        Minecraft mc = Minecraft.getMinecraft();

        //记录开始时间
        long startTime = System.currentTimeMillis();
        try {
            MainDownloader.downloadResource("https://media.forgecdn.net/files/2557/405/Minecraft-Mod-Language-Modpack.zip", "Minecraft-Mod-Language-Modpack.zip", mc.getResourcePackRepository().getDirResourcepacks().toString());
            logger.info("下载成功！");
        } catch (Exception e) {
            logger.error("下载失败！");
        }

        //记录结束时间
        long endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;
        logger.info("花费时间：" + excTime);
    }

    public void applyOption() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings setting = mc.gameSettings;

        setting.loadOptions();
        setting.language = "zh_cn";
        setting.resourcePacks.add(0, "Minecraft-Mod-Language-Modpack.zip");
        setting.saveOptions();

        logger.info("成功加载！");
    }
}
