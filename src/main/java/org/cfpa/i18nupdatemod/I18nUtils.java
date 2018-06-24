package org.cfpa.i18nupdatemod;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import org.cfpa.i18nupdatemod.config.MainConfig;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class I18nUtils {
    public I18nUtils() {
        throw new UnsupportedOperationException("no instance");
    }

    /**
     * 用来判断下载文件是否超过了时间阈值
     *
     * @return 文件是否超过了阈值
     */
    public static boolean intervalDaysCheck() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), MainConfig.download.langPackName);
        try {
            // I18nUpdateMod.logger.info(System.currentTimeMillis() - f.lastModified());
            // I18nUpdateMod.logger.info(MainConfig.download.maxDay * 24 * 3600 * 1000);
            return (System.currentTimeMillis() - f.lastModified()) > (MainConfig.download.maxDay * 24 * 3600 * 1000);
        } catch (Throwable e) {
            logger.error("检查文件日期失败", e);
            return false;
        }
    }

    /**
     * 对比远程文件和本地文件大小，精确到 Byte
     *
     * @return 远程文件是否和本地文件大小匹配
     */
    public static boolean checkLength() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), MainConfig.download.langPackName);
        try {
            URL url = new URL(MainConfig.download.langPackURL);
            return url.openConnection().getContentLengthLong() == f.length();
        } catch (Throwable e) {
            logger.error("检查文件大小失败", e);
            return false;
        }
    }

    /**
     * 重新加载资源包
     * @see ResourcePackRepository
     */
    public static void reloadResources() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 因为这时候资源包已经加载了，所以需要重新读取，重新加载
        ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
        resourcePackRepository.updateRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntriesAll = resourcePackRepository.getRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
        Iterator<String> it = gameSettings.resourcePacks.iterator();

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
                    logger.warn("移除资源包 {}，因为它无法兼容当前版本", entry.getResourcePackName());
                }
            }
        }

        resourcePackRepository.setRepositories(repositoryEntries);
    }

    /**
     * 安装下载好的资源包
     */
    public static void setupResourcesPack() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 在gameSetting中加载资源包
        if (!gameSettings.resourcePacks.contains(MainConfig.download.langPackName)) {
            mc.gameSettings.resourcePacks.add(MainConfig.download.langPackName);
        }
        reloadResources();
    }

    /**
     * 将语言换成中文
     */
    public static void setupLang() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 强行修改为简体中文
        if (!gameSettings.language.equals("zh_cn")) {
            mc.getLanguageManager().currentLanguage = "zh_cn";
            gameSettings.language = "zh_cn";
        }
    }

    /**
     * 检测资源包是否存在
     *
     * @return 资源包是否存在
     */
    public static boolean isResourcePackExist() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), MainConfig.download.langPackName);
        return f.exists();
    }

    /**
     * 检测与待下载主机的连通性
     *
     * @return 是否能连通到待下载主机
     */
    public static boolean online() {
        try {
            return InetAddress.getByName(new URL(MainConfig.download.langPackURL).getHost()).isReachable(2000);
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 检测 Java 虚拟机实例语言
     *
     * @return 是否为简体中文语言
     */
    public static boolean isChinese() {
        return System.getProperty("user.language").equals("zh");
    }
}

