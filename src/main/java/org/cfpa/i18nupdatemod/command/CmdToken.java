package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;

public class CmdToken extends CommandBase {
    @Override
    public String getName() {
        return "token";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "存储并校验 wbelate 上的 token";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        // 参数为空，警告
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.empty"));
            return;
        }

        // 不为空，开始测试
        new Thread(() -> {
            try {
                CloseableHttpResponse response = getTest(args[0]);
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.200"));
                        if (writeToken(args[0])) {
                            return;
                        } else {
                            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.error_write"));
                        }
                        return;
                    case 400:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.400"));
                        return;
                    case 401:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.401"));
                        return;
                    case 403:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.403"));
                        return;
                    case 404:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.404"));
                        return;
                    case 429:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.429"));
                        return;
                    default:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_token.other", response.getStatusLine().getStatusCode()));
                }
            } catch (IOException ioe) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_upload.upload_error"));
            }
        }, "I18n-Thread-Token-Test").start();
    }

    /**
     * GET 进行一次验证
     *
     * @param key weblate 的上传密匙
     * @return 校验完毕后返回的数据
     * @throws IOException 可能发生的 IO 错误
     */
    private CloseableHttpResponse getTest(String key) throws IOException {
        // API 地址
        String apiURL = "https://weblate.exz.me/api/";

        // 建立连接
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        // Get 请求，同时设置验证和接收数据格式
        HttpGet httpGet = new HttpGet(apiURL);
        httpGet.addHeader("Authorization", "Token " + key);
        httpGet.addHeader("Accept", "application/json");

        // 执行
        CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        closeableHttpClient.close();

        return response;
    }

    /**
     * 将正确的 token 存储为文件
     *
     * @param key 传入的正确 token
     * @return 是否写入成功
     */
    private Boolean writeToken(String key) {
        File tokenFile = new File(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "config" + File.separator + "TOKEN.txt");

        // 删除文件
        if (tokenFile.exists()) {
            FileUtils.deleteQuietly(tokenFile);
        }

        // 写入文件
        try {
            if (tokenFile.createNewFile()) {
                FileUtils.write(tokenFile, key, "UTF-8");
                return true;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
