package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CmdUpload extends CommandBase {
    @Override
    public String getName() {
        return "lang_upload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "上传汉化文件到 webalte";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        List<String> modidList = getLangpackName();

        // 参数为空，警告
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.empty"));
        }

        // 参数存在，进行下一步判定
        if (modidList.contains(args[0])) {
            //TODO
        }
        // 参数不存在，警告
        else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.not_found", args[0]));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> modidList = getLangpackName();

        // 如果输入参数为空，返回整个列表
        if (args.length == 0) {
            return modidList;
        }

        // 如果输入不为空，从头字符串检索，进行输出
        List<String> availableArgs = new ArrayList<>();
        for (String modid : modidList) {
            if (modid.indexOf(args[0]) == 0) {
                availableArgs.add(modid);
            }
        }
        return availableArgs;
    }

    /**
     * 检索资源包列表，获取语言文件地址
     *
     * @return 获取的语言文件列表
     */
    private List<String> getLangpackName() {
        List<String> outputModid = new ArrayList<>();
        File resourcepacksDir = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString());
        if (resourcepacksDir.exists() && resourcepacksDir.isDirectory()) {
            for (File i : resourcepacksDir.listFiles()) {
                File transFile = new File(i.toString() + File.separator + "assets" + File.separator + i.toString().substring(15, i.toString().length() - 10) + File.separator + "lang" + File.separator + "zh_cn.lang");
                System.out.println(transFile.toString());
                if (transFile.exists() && transFile.isFile()) {
                    outputModid.add(transFile.toString());
                }
            }
        }
        return outputModid;
    }

    /**
     * POST 表单提交，将文件上传到 weblate 对应仓库
     *
     * @param modid 上传的模组资源 domain
     * @param key   weblate 的上传密匙
     * @return 是否成功上传
     * @throws Exception 可能发生的各种网络错误
     */
    private boolean postFile(String modid, String key) throws Exception {
        URL apiURL = new URL("https://weblate.exz.me/api/translations/langpack/" + modid + "/zh_cn/file/");

        HttpsURLConnection conn = (HttpsURLConnection) apiURL.openConnection();

        conn.setConnectTimeout(10 * 1000);  // 超时：10 秒
        conn.setRequestMethod("POST");  // POST
        conn.setRequestProperty("Accept", "application/json"); // 接收 json 数据
        conn.setRequestProperty("Authorization", key);   // 验证密匙
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false); // POST 不使用缓存

        //TODO 表单提交文件

        return false;
    }
}
