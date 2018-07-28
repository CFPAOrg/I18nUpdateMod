package org.cfpa.i18nupdatemod.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cfpa.i18nupdatemod.I18nUpdateMod;

@Config(modid = I18nUpdateMod.MODID, name = "i18n_update_mod", category = "i18n_mod")
public class MainConfig {
    @Config.Name("公告配置")
    public static Notice notice = new Notice();
    @Config.Name("资源包下载配置")
    public static Download download = new Download();
    @Config.Name("问题反馈配置")
    public static Key key = new Key();
    @Config.Name("启用国际化配置")
    public static Internationalization internationalization = new Internationalization();

    public static class Notice {
        @Config.Name("是否显示通知")
        @Config.Comment("默认玩家每次重启游戏会加载一次公告，可以通过该配置禁用")
        @Config.RequiresMcRestart
        public boolean showNoticeConfig = true;

        @Config.Name("是否显示参与汉化按钮")
        @Config.Comment("默认首次显示的公告左侧会有参与汉化的按钮，可以通过配置禁用")
        @Config.RequiresMcRestart
        public boolean showWeblateButton = true;

        @Config.Name("公告链接")
        @Config.Comment("专为整合作者设计，你只需要提供一个纯网页版txt文件(必须是UTF-8格式编码！)链接，即可加载此公告")
        public String noticeURL = "http://downloader.meitangdehulu.com/Notice.txt";
    }

    public static class Download {
        @Config.Name("git仓库链接")
        @Config.Comment("虽然我不清楚修改此处有什么用，但是我加一个吧，万一有人需要呢")
        @Config.RequiresMcRestart
        public String repositoryURL = "https://github.com/CFPAOrg/Minecraft-Mod-Language-Package.git";

        @Config.Name("资源包名称")
        @Config.Comment("用来自定义下载得到的资源包名称")
        @Config.RequiresMcRestart
        public String langPackName = "Minecraft-Mod-Language-Modpack.zip";

        @Config.Name("是否开启强制中文功能")
        @Config.RequiresMcRestart
        @Config.Comment("默认开启，会在启动时将游戏语言强制设定为中文")
        public boolean setupChinese = true;

        @Config.Name("是否开启资源包下载功能")
        @Config.RequiresMcRestart
        @Config.Comment("默认开启，关闭后此模组不再尝试检查、下载资源包、以及切换语言")
        public boolean shouldDownload = true;

        @Config.Name("是否为第一次使用")
        @Config.RequiresMcRestart
        @Config.Comment("默认开启，关闭后此模组不再尝试检查、下载资源包、以及切换语言")
        public boolean isFirst = true;
    }

    public static class Key {
        @Config.Name("自定义反馈按键打开网址")
        @Config.Comment("可能会有人想自定义")
        @Config.RequiresMcRestart
        public String reportURL = "http://issues.cfpa.team";
    }

    public static class Internationalization {
        @Config.Name("启用国际化")
        @Config.Comment("启用后，将依据系统语言来选择开启或关闭资源包下载、公告显示、键位指令注册")
        @Config.RequiresMcRestart
        public boolean openI18n = false;
    }

    // 用于 GUI 界面配置调节的保存
    @Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(I18nUpdateMod.MODID)) {
                ConfigManager.sync(I18nUpdateMod.MODID, Config.Type.INSTANCE);
                I18nUpdateMod.logger.info("配置文件修改已经保存");
            }
        }
    }
}
