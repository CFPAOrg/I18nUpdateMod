package org.cfpa.i18nupdatemod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DownloadInfoHelper {
    public static Queue<String> info = new ConcurrentLinkedQueue<>();
}
