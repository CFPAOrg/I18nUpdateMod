package org.cfpa.i18nupdatemod.notice;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NoticeShower {
    private static List<String> strings;
    private Runnable task;

    public NoticeShower() {
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

    public NoticeShower(Runnable task) {
        this();
        this.task = task;
    }

    private void onDone() {
        if (task != null) {
            task.run();
        }
        FMLCommonHandler.instance().showGuiScreen(new NoticeGui(strings));
    }

    private static void catching(Throwable e) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("获取公告失败。"));
        I18nUpdateMod.logger.error("获取公告失败：", e);
    }
}
