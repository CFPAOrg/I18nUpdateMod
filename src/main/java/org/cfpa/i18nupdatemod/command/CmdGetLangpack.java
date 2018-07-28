package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.cfpa.i18nupdatemod.I18nUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

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
        // 参数为空，警告
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.empty"));
            return;
        }

        // 参数存在，进行下一步判定
        if (Minecraft.getMinecraft().getResourceManager().getResourceDomains().contains(args[0])) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_start", args[0]));

            // 同名资源包存在，直接返回
            if (!cerateTempLangpack(args[0])) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.error_create_folder"));
                return;
            }
        }
        // 参数不存在，警告
        else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.not_found", args[0]));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        // 如果输入参数为空，返回整个列表
        if (args.length == 0) {
            return new ArrayList<>(Minecraft.getMinecraft().getResourceManager().getResourceDomains());
        }

        // 如果输入不为空，从头字符串检索，进行输出
        List<String> availableArgs = new ArrayList<>();
        for (String modid : Minecraft.getMinecraft().getResourceManager().getResourceDomains()) {
            if (modid.indexOf(args[0]) == 0) {
                availableArgs.add(modid);
            }
        }
        return availableArgs;
    }

    /**
     * 构建资源包文件夹
     *
     * @param modid 想要下载的模组资源 id
     * @return 是否构建成功
     */
    private boolean cerateTempLangpack(String modid) {
        // 构建文件夹
        File tempDir = new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang", modid, modid));
        if (tempDir.exists() || !tempDir.mkdirs()) {
            return false;
        }

        // 构建 pack.mcmeta
        File tempPackFile = new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_tmp_resource_pack" + File.separator + "pack.mcmeta", modid));
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

    /**
     * 处理中英文文件，弄成混编，方便玩家翻译
     *
     * @param modid 想要下载的模组资源 id
     * @return 是否处理成功
     */
    private boolean handleLangpack(String modid) {
        try {
            // 临时文件
            List<String> tmpFile = new ArrayList<>();

            // 读取中英文文件
            List<String> en_us = FileUtils.readLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "en_us.lang", modid, modid)), StandardCharsets.UTF_8);
            List<String> zh_cn = FileUtils.readLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn.lang", modid, modid)), StandardCharsets.UTF_8);

            // 处理成 HashMap
            HashMap<String, String> chineseMap = I18nUtils.listToMap(zh_cn);

            // 接下来，替换
            for (String s : en_us) {
                // 临时变量，记录是否已经存在汉化
                boolean isExist = false;

                // 遍历查找
                for (String key : chineseMap.keySet()) {
                    // 存在！
                    if (s.indexOf(key) == 0) {
                        // 替换，写入临时变量。记住替换字符串需要转义，防止发生 Illegal group reference 错误
                        tmpFile.add(s.replaceAll("=.*$", "=" + Matcher.quoteReplacement(chineseMap.get(key))));
                        // 别忘记标记存在
                        isExist = true;
                        break;
                    }
                }
                // 只有不存在时，才添加源字符串
                if (!isExist) {
                    tmpFile.add(s);
                }
            }

            // 写入文件
            FileUtils.writeLines(new File(String.format(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn.lang", modid, modid)), "UTF-8", tmpFile, "\n", false);

            return true;
        } catch (IOException ioe) {
            return false;
        }
    }
}