package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NoticeShower {
    private static List<String> strings;

    public NoticeShower() {
        new Thread(() -> {
            try {
                URL url = new URL("http://p985car2i.bkt.clouddn.com/Notice.txt");
                strings = IOUtils.readLines(url.openStream(), StandardCharsets.UTF_8);
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new NoticeGui(strings)));
            } catch (Throwable e) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("获取公告失败。"));
                I18nUpdateMod.logger.error("获取公告失败：", e);
            }
        }, "I18n_NOTICE_PENDING_THREAD").start();
    }
}
