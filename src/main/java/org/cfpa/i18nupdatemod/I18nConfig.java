package org.cfpa.i18nupdatemod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = I18nUpdateMod.MODID, name = "i18n_update_mod", category = "i18n_mod")
public class I18nConfig {
    @Config.Name("公告配置")
    public static Notice notice = new Notice();
    @Config.Name("资源包下载配置")
    public static Download download = new Download();
    @Config.Name("按键配置")
    public static Key key = new Key();
    @Config.Name("启用国际化配置")
    public static Internationalization internationalization = new Internationalization();
    @Config.Name("优先加载资源包")
    @Config.Comment("是否将资源包设为最高优先级")
    @Config.RequiresMcRestart
    public static boolean priority = true;

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
        @Config.Name("更新检测间隔（天）")
        @Config.RequiresMcRestart
        @Config.Comment("通过修改此处设定更新检测间隔，单位为天。设置为0表示每次启动游戏都检测")
        @Config.RangeInt(min = 0, max = 30)
        public int maxDay = 3;

        @Config.Name("本地资源包仓库地址")
        @Config.Comment("默认为AppData文件夹")
        @Config.RequiresMcRestart
        public String localRepoPath = "AppData";

        @Config.Name("远程资源包仓库地址列表")
        @Config.Comment("按列表中的顺序尝试从远程仓库获取更新")
        @Config.RequiresMcRestart
        public String[] remoteRepoURL = {
                "https://git.dev.tencent.com/dtid_07bf67d98cf2ab4b/Minecraft-Mod-Language-Package.git",
                "https://github.com/CFPAOrg/Minecraft-Mod-Language-Package.git" };

        @Config.Name("生成的资源包名称")
        @Config.Comment("用来自定义模组生成的资源包名称")
        @Config.RequiresMcRestart
        public String i18nLangPackName = "I18n-Mod-Language-Pack";

        @Config.Name("下载条名称")
        @Config.Comment("用来自定义下载过程中小窗口的名字")
        @Config.RequiresMcRestart
        public String dlWindowsName = "汉化资源包更新进度条";

        @Config.Name("超时时间（秒）")
        @Config.RequiresMcRestart
        @Config.Comment("超过多少时间，取消主线程阻塞，转为后台下载")
        @Config.RangeInt(min = 1)
        public int maxTime = 60;

        @Config.Name("是否开启强制中文功能")
        @Config.RequiresMcRestart
        @Config.Comment("默认开启，会在启动时将游戏语言强制设定为中文")
        public boolean setupChinese = true;

        @Config.Name("是否开启资源包下载功能")
        @Config.RequiresMcRestart
        @Config.Comment("默认开启，关闭后此模组不再尝试更新本地仓库和安装资源包")
        public boolean shouldDownload = true;
    }

    public static class Key {
        @Config.Name("自定义反馈按键打开网址")
        @Config.Comment("可能会有人想自定义")
        @Config.RequiresMcRestart
        public String reportURL = "http://issues.cfpa.team";

        @Config.Name("是否关闭所有键位")
        @Config.Comment("为腐竹设计，防止玩家乱改按键导致问题")
        @Config.RequiresMcRestart
        public Boolean closedKey = false;
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
