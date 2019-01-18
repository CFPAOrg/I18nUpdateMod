package org.cfpa.i18nupdatemod.download;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DownloadInfoHelper {
    public static Queue<String> info = new ConcurrentLinkedQueue<>();

    public static void init() {
        // 消息通知线程
        new Thread(() -> {
            while (true) {
                if (Minecraft.getMinecraft().player != null) {
                    while (!info.isEmpty()) {
                        String theInfo = info.remove();
                        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("[I18nUpdateMod] " + theInfo)));
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "I18n-download-info-Thread").start();
    }
}
