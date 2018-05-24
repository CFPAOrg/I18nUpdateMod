package org.cfpa.i18nupdatemod;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.download.HashChecker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class I18nUtils {
    public I18nUtils() {
        throw new UnsupportedOperationException("no instance");
    }

    public static boolean hashCheck() {
        String hashExpected;
        try {
            URL url = new URL("https://covertdragon.team/i18n/hash");
            URLConnection connection = url.openConnection();
            hashExpected = IOUtils.readLines(connection.getInputStream(), StandardCharsets.UTF_8).get(0);
        } catch (Throwable e) {
            I18nUpdateMod.logger.warn("获取Hash信息失败！");
            e.printStackTrace();
            return false;
        }
        try {
            return HashChecker.checkMD5(new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), "Minecraft-Mod-Language-Modpack.zip"), hashExpected);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadResources() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 因为这时候资源包已经加载了，所以需要重新读取，重新加载
        ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
        resourcePackRepository.updateRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntriesAll = resourcePackRepository.getRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
        Iterator<String> it = gameSettings.resourcePacks.iterator();

        /*
         此时情况是这样的
         entry 为修改后的条目
         repositoryEntries 为游戏应当加载的条目
        */
        while (it.hasNext()) {
            String packName = it.next();
            for (ResourcePackRepository.Entry entry : repositoryEntriesAll) {
                if (entry.getResourcePackName().equals(packName)) {
                    // packFormat 为 3，或者 incompatibleResourcePacks 条目中有的资源包才会加入
                    if (entry.getPackFormat() == 3 || gameSettings.incompatibleResourcePacks.contains(entry.getResourcePackName())) {
                        repositoryEntries.add(entry);
                        break;
                    }
                    // 否则移除
                    it.remove();
                    I18nUpdateMod.logger.warn("移除资源包 {}，因为它无法兼容当前版本", entry.getResourcePackName());
                }
            }
        }
        resourcePackRepository.setRepositories(repositoryEntries);
    }

    public static void setupResourcesPack() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 在gameSetting中加载资源包
        if (!gameSettings.resourcePacks.contains("Minecraft-Mod-Language-Modpack.zip")) {
            mc.gameSettings.resourcePacks.add("Minecraft-Mod-Language-Modpack.zip");
            I18nUtils.reloadResources();
        }
    }
}

