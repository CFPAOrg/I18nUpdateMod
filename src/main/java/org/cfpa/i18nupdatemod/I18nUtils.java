package org.cfpa.i18nupdatemod;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import org.apache.commons.io.FileUtils;
import org.cfpa.i18nupdatemod.config.MainConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

public class I18nUtils {
    public I18nUtils() {
        throw new UnsupportedOperationException("no instance");
    }
    /**
     * 重新加载资源包
     *
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
     * 检测系统语言
     *
     * @return 是否为简体中文语言
     */
    public static boolean isChinese() {
        return System.getProperty("user.language").equals("zh");
    }

    /**
     * 依据等号切分字符串，将 list 处理成 hashMap
     *
     * @param listIn 想要处理的字符串 list
     * @return 处理好的 HashMap
     */
    public static HashMap<String, String> listToMap(List<String> listIn) {
        HashMap<String, String> mapOut = new HashMap<>();

        // 抄袭原版加载方式
        Splitter I18N_SPLITTER = Splitter.on('=').limit(2);

        // 遍历拆分
        for (String s : listIn) {
            if (!s.isEmpty() && s.charAt(0) != '#') {
                String[] splitString = Iterables.toArray(I18N_SPLITTER.split(s), String.class);

                if (splitString != null && splitString.length == 2) {
                    String s1 = splitString[0];
                    String s2 = splitString[1];
                    mapOut.put(s1, s2);
                }
            }
        }
        return mapOut;
    }

    /**
     * 从文件中获取 Token
     *
     * @return 得到的 Token
     */
    public static String readToken() {
        File tokenFile = new File(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "config" + File.separator + "TOKEN.txt");
        try {
            List<String> token = FileUtils.readLines(tokenFile, "UTF-8");
            if (token != null) {
                return token.get(0);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}

