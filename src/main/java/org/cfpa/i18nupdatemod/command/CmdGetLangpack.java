package org.cfpa.i18nupdatemod.command;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.cfpa.i18nupdatemod.download.DownloadManager;
import org.cfpa.i18nupdatemod.download.DownloadStatus;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdGetLangpack extends CommandBase {
    @Override
    public String getName() {
        return "lang_get";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "快速重载语言文件，用于测试汉化";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.empty"));
        }

        if (Minecraft.getMinecraft().getResourceManager().getResourceDomains().contains(args[0])) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_start", args[0]));

            if (!cerateTempLangpack(args[0])) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.error_create_folder"));
                return;
            }

            DownloadManager langpackChinese = new DownloadManager(String.format("https://raw.githubusercontent.com/CFPAOrg/Minecraft-Mod-Language-Package/1.12.2/project/assets/%s/lang/zh_cn.lang", args[0]), "zh_cn.lang", String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang", args[0], args[0]));
            DownloadManager langpackEnglish = new DownloadManager(String.format("https://raw.githubusercontent.com/CFPAOrg/Minecraft-Mod-Language-Package/1.12.2/project/assets/%s/lang/en_us.lang", args[0]), "en_us.lang", String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang", args[0], args[0]));
            langpackChinese.start("I18n-Download-Chinese-Thread");
            langpackEnglish.start("I18n-Download-English-Thread");

            // 开始下载
            new Thread(() -> {
                int timeRecord = 0;
                while (true) {
                    try {
                        Thread.sleep(5000);
                        if (langpackChinese.getStatus() == DownloadStatus.DOWNLOADING || langpackEnglish.getStatus() == DownloadStatus.DOWNLOADING) {
                            timeRecord = timeRecord + 5;
                        } else if (langpackChinese.getStatus() == DownloadStatus.SUCCESS && langpackEnglish.getStatus() == DownloadStatus.SUCCESS) {
                            if (handleLangpack(args[0])) {
                                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_stop"));
                            } else {
                                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.error_handle"));
                            }
                            break;
                        } else {
                            langpackChinese.cancel();
                            langpackEnglish.cancel();
                            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.error_stop"));
                            break;
                        }
                    } catch (InterruptedException ignore) {
                    }
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_downloading", timeRecord));
                    if (timeRecord > 60) {
                        langpackChinese.cancel();
                        langpackEnglish.cancel();
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.error_timeout"));
                        return;
                    }
                }
            }, "I18n_LANGPACK_THREAD").start();
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.not_found", args[0]));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 0) {
            return new ArrayList<>(Minecraft.getMinecraft().getResourceManager().getResourceDomains());
        }

        List<String> availableArgs = new ArrayList<>();
        for (String modid : Minecraft.getMinecraft().getResourceManager().getResourceDomains()) {
            if (modid.indexOf(args[0]) == 0) {
                availableArgs.add(modid);
            }
        }
        return availableArgs;
    }

    private boolean cerateTempLangpack(String modid) {
        // 构建文件夹
        File tempDir = new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang", modid, modid));
        if (tempDir.exists() || !tempDir.mkdirs()) {
            return false;
        }

        // 构建 pack.mcmeta
        File tempPackFile = new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "pack.mcmeta", modid));
        String metaText = String.format("{\"pack\":{\"pack_format\":3,\"description\":\"临时汉化资源包，仅包含 %s 模组中英文文件\"}}", modid);

        // 判定文件是否存在
        if (tempPackFile.exists()) {
            return false;
        }

        // 写入数据
        try {
            FileUtils.write(tempPackFile, metaText, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            return false;
        }

        // 走到了这一步，恭喜你
        return true;
    }

    // 处理中英文文件，弄成混编，方便玩家翻译
    private boolean handleLangpack(String modid) {
        try {
            List<String> en_us = FileUtils.readLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "en_us.lang", modid, modid)), StandardCharsets.UTF_8);
            List<String> zh_cn = FileUtils.readLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn.lang", modid, modid)), StandardCharsets.UTF_8);

            FileUtils.writeLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_模组临时汉化资源包" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn.lang", modid, modid)), "UTF-8", handleMap(listToMap(en_us), listToMap(zh_cn)), "\n", false);

            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    // 将 list 处理成 hashMap
    private HashMap<String, String> listToMap(List<String> listIn) {
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

    // 将两个 map 混编
    private List<String> handleMap(HashMap<String, String> enMap, HashMap<String, String> zhMap) {
        List<String> listOut = new ArrayList<>();
        for (String key : enMap.keySet()) {
            if (zhMap.containsKey(key)) {
                listOut.add(key + '=' + zhMap.get(key));
            } else {
                listOut.add(key + '=' + enMap.get(key));
            }
        }
        return listOut;
    }
}