package org.cfpa.i18nupdatemod;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.ICrashCallable;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class I18nUtils {
    public I18nUtils() {
        throw new UnsupportedOperationException("no instance");
    }

    /**
     * 将语言换成中文
     */
    static void setupLang() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gameSettings = mc.gameSettings;
        // 强行修改为简体中文
        if (!gameSettings.language.equals("zh_cn")) {
            mc.getLanguageManager().currentLanguage = "zh_cn";
            gameSettings.language = "zh_cn";
        }
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
     * 依据等号切分字符串，将 list 处理成 Map
     *
     * @param listIn 想要处理的字符串 list
     * @return 处理好的 Map
     */
    public static Map<String, String> listToMap(List<String> listIn) {
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
    @Nullable
    public static String readToken() {
        File tokenFile = new File(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "config"
                + File.separator + "TOKEN.txt");
        try {
            List<String> token = FileUtils.readLines(tokenFile, "UTF-8");
            if (token.size() != 0) {
                return token.get(0);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    static String getLocalRepositoryFolder(String path) throws IllegalArgumentException {
        String OS = System.getProperty("os.name").toLowerCase();
        String folder;
        if (path.equals("Auto")) {
            if (OS.contains("mac") || OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                String userHome = System.getProperty("user.home");
                if (userHome != null) {
                    folder = new File(userHome, ".I18nUpdateMod").getPath();
                } else {
                    throw new IllegalArgumentException("User home is null.");
                }
            } else if (OS.contains("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    folder = new File(appData, "I18nUpdateMod").getPath();
                } else {
                    throw new IllegalArgumentException("AppData path is null.");
                }
            } else {
                throw new IllegalArgumentException("Can't find out what path should be used.");
            }
        } else {
            File f = new File(path);
            if (f.isAbsolute()) {
                folder = f.getPath();
            } else {
                folder = new File(Minecraft.getMinecraft().mcDataDir, f.getPath()).getPath();
            }
        }
        return folder;
    }

    public static void copyDir(Path sourceDir, Path targetDir) throws IOException {
        Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
    }

    static class CopyDir extends SimpleFileVisitor<Path> {
        private Path sourceDir;
        private Path targetDir;

        CopyDir(Path sourceDir, Path targetDir) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            if (!newDir.toFile().exists()) {
                Files.createDirectory(newDir);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    static class InvalidPathConfigurationException extends CustomModLoadingErrorDisplayException {

        public InvalidPathConfigurationException() {
            super("InvalidConfiguration", null);
        }

        @Override
        public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
        }

        @Override
        public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
            final List<String> text = fontRenderer.listFormattedStringToWidth("本地仓库路径无效，请更改配置文件中的本地资源包仓库地址。", errorScreen.width - 80);
            int yOffset = 50;
            for (String sentence : text) {
                errorScreen.drawCenteredString(fontRenderer, sentence, errorScreen.width / 2, yOffset, 0xFFFFFF);
                yOffset += 10;
            }
        }
    }
}
