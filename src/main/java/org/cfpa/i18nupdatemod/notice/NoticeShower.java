package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.gui.NoticeGui;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NoticeShower {
    private static List<String> strings;

    public static void showNotice() {
        new Thread(() -> {
            try {
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
}
