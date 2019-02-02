package org.cfpa.i18nupdatemod.installer;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import org.cfpa.i18nupdatemod.I18nConfig;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;
import static org.cfpa.i18nupdatemod.I18nUtils.isChinese;

public abstract class ResourcePackInstaller {
    public boolean updateResourcePack = false;

    private boolean online() {
        try {
            return InetAddress.getByName(new URL(I18nConfig.download.langPackURL).getHost()).isReachable(2000);
        } catch (Throwable e) {
            return false;
        }
    }

    private boolean intervalDaysCheck() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), I18nConfig.download.langPackName);
        try {
            return (System.currentTimeMillis() - f.lastModified()) > (I18nConfig.download.maxDay * 24 * 3600 * 1000);
        } catch (Throwable e) {
            logger.error("检查文件日期失败", e);
            return false;
        }
    }

    void setResourcesRepository() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 在gameSetting中加载资源包
        if (!gameSettings.resourcePacks.contains(I18nConfig.download.langPackName)) {
            if (I18nConfig.priority) {
                mc.gameSettings.resourcePacks.add(I18nConfig.download.langPackName);
            } else {
                List<String> packs = new ArrayList<>(10);
                packs.add(I18nConfig.download.langPackName); // 资源包的 index 越小优先级越低(在资源包 gui 中置于更低层)
                packs.addAll(gameSettings.resourcePacks);
                gameSettings.resourcePacks = packs;
            }
        }
        reloadResources();
    }

    private boolean checkLength() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), I18nConfig.download.langPackName);
        try {
            URL url = new URL(I18nConfig.download.langPackURL);
            return url.openConnection().getContentLengthLong() == f.length();
        } catch (Throwable e) {
            logger.error("检查文件大小失败", e);
            return false;
        }
    }

    private void reloadResources() {
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

    private boolean isResourcePackExist() {
        File f = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), I18nConfig.download.langPackName);
        return f.exists();
    }

    @OverridingMethodsMustInvokeSuper
    public void install() {
        if (!I18nConfig.download.shouldDownload || I18nConfig.internationalization.openI18n && !isChinese()) {
            return;
        }

        if (!intervalDaysCheck()) {
            I18nUpdateMod.logger.info("未到下次更新时间，跳过检测和下载阶段");
            setResourcesRepository();
        } else if ((!online()) && isResourcePackExist()) {
            I18nUpdateMod.logger.info("检测到网络不可用，跳过下载阶段");
            setResourcesRepository();
        } else if (checkLength()) {
            I18nUpdateMod.logger.info("检测到资源包最新，跳过下载阶段");
            setResourcesRepository();
        } else {
            updateResourcePack = true;
        }
    }
}
