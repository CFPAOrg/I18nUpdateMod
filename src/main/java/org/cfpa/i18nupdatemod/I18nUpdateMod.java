package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.command.*;
import org.cfpa.i18nupdatemod.config.MainConfig;
import org.cfpa.i18nupdatemod.git.*;
import org.cfpa.i18nupdatemod.hotkey.HotKeyHandler;

import java.io.File;

import static org.cfpa.i18nupdatemod.I18nUtils.*;


@Mod(modid = I18nUpdateMod.MODID, name = I18nUpdateMod.NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.12]", version = I18nUpdateMod.VERSION)
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "@VERSION";

    public static final Logger logger = LogManager.getLogger(MODID);

    // 通知变量
    public static boolean showNotice = false;
    public String dirResourcepacks = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString();

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) throws InterruptedException {
        // 最开始，检查是否开资源包下载配置
        if (!MainConfig.download.shouldDownload) {
            return;
        }

        // 其次，检测是否启用国际化配置
        if (MainConfig.internationalization.openI18n && !isChinese()) {
            return;
        } else {
            // 决定显示通知
            showNotice = true;

            //检测是否为第一次使用模组
            if (MainConfig.download.isFirst) {
                //开始clone

                String cfgPath = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().getParent() + File.separator + "config";
                Create.createDir(System.getProperty("java.io.tmpdir") + File.separator + "Minecraft-Mod-Language-Package-1.12.2");
                Clone.cloneRepository(MainConfig.download.repositoryURL, System.getProperty("java.io.tmpdir") + File.separator + "Minecraft-Mod-Language-Package-1.12.2");
                new Modify(cfgPath, "        B:是否为第一次使用=true", "        B:是否为第一次使用=false");
            } else {//开始pull
                try {
                    System.out.println("Pulling......");
                    Pull.pull();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String zipFile = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + MainConfig.download.langPackName;
            ZipCompressorByAnt zca = new ZipCompressorByAnt(zipFile);
            zca.compress(System.getProperty("java.io.tmpdir") + File.separator + "Minecraft-Mod-Language-Package-1.12.2" + File.separator + "project");
            setupResourcesPack();


            // 变化语言为中文
            if (MainConfig.download.setupChinese) {
                setupLang();
            }

        }
    }
    @Mod.EventHandler
    public void init (FMLInitializationEvent event){
        if (MainConfig.internationalization.openI18n && !isChinese()) {
            return;
        }
        HotKeyHandler.register();
        ClientCommandHandler.instance.registerCommand(new CmdNotice());
        ClientCommandHandler.instance.registerCommand(new CmdReport());
        ClientCommandHandler.instance.registerCommand(new CmdReload());
        ClientCommandHandler.instance.registerCommand(new CmdGetLangpack());
        ClientCommandHandler.instance.registerCommand(new CmdUpload());
        ClientCommandHandler.instance.registerCommand(new CmdToken());
    }
}
