package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ShowNoticeFirst {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPlayerFirstJoin(RenderGameOverlayEvent.Post event) throws InterruptedException {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || fileIsExist()) {
            return;
        }
        showNotice();
    }

    private static List<String> strings;

    public static void showNotice() {
        new Thread(() -> {
            try {
                createFile(); // 创建证明文件
                Thread.sleep(1000); // 手榴弹，都给我延时 1 秒丢出去
                URL url = new URL("http://p985car2i.bkt.clouddn.com/Notice.txt");
                strings = IOUtils.readLines(url.openStream(), StandardCharsets.UTF_8);
                onDone();
            } catch (Throwable e) {
                catching(e);
            }
        }, "I18n_NOTICE_PENDING_THREAD").start();
    }

    private static void onDone() {
        Minecraft.getMinecraft().displayGuiScreen(new NoticeGui(strings));
    }

    private static void catching(Throwable e) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("获取公告失败。"));
        I18nUpdateMod.logger.error("获取公告失败：", e);
    }

    private static void createFile() {
        Minecraft mc = Minecraft.getMinecraft();
        File file = new File(mc.mcDataDir.getPath() + File.separator + "config" + File.separator + I18nUpdateMod.MODID + ".txt");
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
        File file = new File(mc.mcDataDir.getPath() + File.separator + "config" + File.separator + I18nUpdateMod.MODID + ".txt");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
