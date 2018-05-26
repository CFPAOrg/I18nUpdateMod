package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.io.File;
import java.io.FileOutputStream;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ShowNoticeFirst {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPlayerFirstJoin(RenderGameOverlayEvent.Post event) throws InterruptedException {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || fileIsExist()) {
            return;
        }
        new NoticeShower(ShowNoticeFirst::createFile);
    }

    private static void createFile() {
        Minecraft mc = Minecraft.getMinecraft();
        File file = new File(mc.mcDataDir.getPath() + File.separator + "config" + File.separator + I18nUpdateMod.MODID + ".cfg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.write("你看到这个文件时候，说明你已经不是第一次使用该模组了", fos, "UTF-8");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean fileIsExist() {
        Minecraft mc = Minecraft.getMinecraft();
        File file = new File(mc.mcDataDir.getPath() + File.separator + "config" + File.separator + I18nUpdateMod.MODID + ".cfg");
        return file.exists();
    }
}
