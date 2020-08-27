package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.resources.IPackFinder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Date;
import java.util.Locale;

@Mod("i18nupdate")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class I18nUpdateMod {

    public static final Logger LOGGER = LogManager.getLogger();

    public I18nUpdateMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            try{
                if(MCPVersion.getMCVersion().toLowerCase().startsWith("1.15")){
                    Minecraft.getInstance().getResourcePackList().addPackFinder((IPackFinder)Class.forName("org.cfpa.i18nupdatemod.mc115.I18nUpdateModPackFinder").getField("RESOUCE").get(null));
                }
                else if(MCPVersion.getMCVersion().toLowerCase().startsWith("1.16")){
                    Minecraft.getInstance().getResourcePackList().addPackFinder((IPackFinder)Class.forName("org.cfpa.i18nupdatemod.mc115.I18nUpdateModPackFinder").getField("RESOUCE").get(null));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
//            if (isChinese())
                Minecraft.getInstance().gameSettings.language = "zh_cn";
        }
    }

    @SubscribeEvent
    public static void onClientStarting(FMLClientSetupEvent event) {
//        if (isChinese())
            Minecraft.getInstance().getLanguageManager().setCurrentLanguage(new Language("zh_cn", "CN", "简体中文", false));
        String path = System.getProperty("user.home") + "/.i18n/"+MCPVersion.getMCVersion();
        File filename = new File(path + "/update.txt");
        try {
            if (filename.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
                BufferedReader br = new BufferedReader(reader);
                String line = "";
                line = br.readLine();
                br.close();
                if (line == null || new Date().getTime() - Long.parseLong(line) < 7 * 24 * 60 * 60) {
                    return;
                }
            }
            FileDownloadManager t = new FileDownloadManager("https://ae01.alicdn.com/kf/H0733a1a38d6d4406b0b8e304d0b1f83bU.jpg", "i18n.zip", path);
            t.setSuccessTask(() -> {
                try {
                    File writename = new File(path + "/update.txt");
                    if (!writename.exists())
                        writename.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(writename));
                    out.write(String.valueOf(new Date().getTime()));
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start("dl i18n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isChinese() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage().toLowerCase().equals("zh");
    }
}
