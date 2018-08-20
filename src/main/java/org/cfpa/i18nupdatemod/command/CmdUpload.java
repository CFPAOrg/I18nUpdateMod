package org.cfpa.i18nupdatemod.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cfpa.i18nupdatemod.I18nUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdUpload extends CommandBase {
    private boolean haveResponse;

    @Override
    public String getName() {
        return "lang_upload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "上传汉化文件到 weblate";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        List<String> modidList = getLangpackName();
        String key = I18nUtils.readToken();

        // 参数为空，警告
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.empty"));
            return;
        }

        // 参数存在，进行下一步判定
        if (modidList.contains(args[0])) {
            if (key == null) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.no_token", args[0]));
                return;
            }

            new Thread(() -> {
                try {
                    postFile(args[0], key);
                } catch (Exception e) {
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.upload_error"));
                    e.printStackTrace();
                }
            }, "I18n-Thread-File-Upload").start();
        }
        // 参数不存在，警告
        else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.not_found", args[0]));
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
                Matcher matcher = Pattern.compile("resourcepacks" + Matcher.quoteReplacement(File.separator) + "(.*?)_tmp_resource_pack").matcher(i.toString());
                while (matcher.find()) {
                    File transFile = new File(i.toString() + File.separator + "assets" + File.separator + matcher.group(1) + File.separator + "lang" + File.separator + "zh_cn.lang");
                    if (transFile.exists() && transFile.isFile()) {
                        outputModid.add(matcher.group(1));
                    }
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
     */
    private void postFile(String modid, String key) {
        // API 地址
        String apiURL = "https://weblate.exz.me/api/translations/langpack/" + modid + "/zh_cn/file/";

        // 将要上传的文件预处理
        File tmpFile;
        try {
            tmpFile = handleFile(modid);
            if (tmpFile == null) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.handle_error"));
                return;
            }
        } catch (IOException ioe) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.handle_error"));
            return;
        }

        // 建立连接
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        // Post 请求，同时设置验证和接收数据格式
        HttpPost httpPost = new HttpPost(apiURL);
        httpPost.addHeader("Authorization", "Token " + key);
        httpPost.addHeader("Accept", "application/json");

        // 装填表单本体
        httpPost.setEntity(
                MultipartEntityBuilder.create()
                        .addTextBody("overwrite", "true")
                        .addBinaryBody("file", tmpFile)
                        .build()
        );

        // 执行，并在执行完毕后，关闭连接
        CloseableHttpResponse response;
        try {
            // 先来一句提醒
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.excute_ready"));

            // 开始拨动线程检查
            haveResponse = false;
            new Thread(() -> {
                // 计数器
                int count = 0;
                while (!haveResponse) {
                    try {
                        // 阻塞
                        Thread.sleep(5 * 1000);
                        // 计数
                        count = count + 5;
                        // 二次检查显示信息
                        if (!haveResponse) {
                            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.uploading", count));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "I18n-Upload-Check-Thread").start();

            // 执行上传！
            response = closeableHttpClient.execute(httpPost);

            // 依据返回结果进行不同显示
            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.200"));
                    try {
                        Gson gson = new Gson();
                        POJOResult result = gson.fromJson(IOUtils.readLines(response.getEntity().getContent(), "UTF-8").get(0), new TypeToken<POJOResult>() {
                        }.getType());

                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_title"));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_count", result.getCount()));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_total", result.getTotal()));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_accepted", result.getAccepted()));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_not_found", result.getNot_found()));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.result_skipped", result.getSkipped()));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    break;
                case 400:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.400"));
                    break;
                case 401:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.401"));
                    break;
                case 403:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.403"));
                    break;
                case 404:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.404"));
                    break;
                case 429:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.429"));
                    break;
                case 524:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.524"));
                    break;
                default:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.other", response.getStatusLine().getStatusCode()));
                    break;
            }

            // 关闭提醒功能
            haveResponse = true;

            // 关闭网络连接
            closeableHttpClient.close();
        } catch (IOException ioe) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.upload_error"));
        }
    }

    /**
     * 将准备上传的语言文件进行处理
     *
     * @param modid 上传的模组资源 domain
     * @return 处理后的文件对象
     * @throws IOException 读取文件可能发生的 IO 错误
     */
    @Nullable
    private File handleFile(String modid) throws IOException {
        // 英文，中文，临时文件
        File rawChineseFile = new File(String.format(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "resourcepacks" + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn.lang", modid, modid));
        File rawEnglishFile = new File(String.format(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "resourcepacks" + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "en_us.lang", modid, modid));
        File handleChineseFile = new File(String.format(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "resourcepacks" + File.separator + "%s_tmp_resource_pack" + File.separator + "assets" + File.separator + "%s" + File.separator + "lang" + File.separator + "zh_cn_tmp.lang", modid, modid));

        // 文件存在，才进行处理
        if (rawEnglishFile.exists() && rawChineseFile.exists()) {
            // 读取处理成 HashMap
            HashMap<String, String> zh_cn = I18nUtils.listToMap(FileUtils.readLines(rawChineseFile, StandardCharsets.UTF_8));
            HashMap<String, String> en_us = I18nUtils.listToMap(FileUtils.readLines(rawEnglishFile, StandardCharsets.UTF_8));

            // 未翻译处进行剔除
            List<String> tmpFile = new ArrayList<>();
            for (String key : zh_cn.keySet()) {
                if (en_us.get(key) != null && !en_us.get(key).equals(zh_cn.get(key))) {
                    tmpFile.add(key + '=' + zh_cn.get(key));
                }
            }

            // 文件写入
            FileUtils.writeLines(handleChineseFile, "UTF-8", tmpFile, "\n", false);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.handle_success"));
            return handleChineseFile;
        } else {
            return null; // 不存在返回 null
        }
    }
}
