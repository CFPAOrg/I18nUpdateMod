package org.cfpa.i18nupdatemod.hotkey;

import mezz.jei.Internal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nConfig;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.I18nUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class HotKeyHandler {
    private final KeyBinding mainKey = new KeyBinding("key.i18nmod.main_key.desc", Keyboard.KEY_LCONTROL, "key.category.i18nmod");
    private final KeyBinding reportKey = new KeyBinding("key.i18nmod.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");
    private final KeyBinding weblateKey = new KeyBinding("key.i18nmod.weblate_key.desc", Keyboard.KEY_L, "key.category.i18nmod");
    private final KeyBinding mcmodKey = new KeyBinding("key.i18nmod.mcmod_key.desc", Keyboard.KEY_M, "key.category.i18nmod");
    private final KeyBinding reloadKey = new KeyBinding("key.i18nmod.reload_key.desc", Keyboard.KEY_R, "key.category.i18nmod");

    private boolean showed = false;

    public HotKeyHandler() {
        ClientRegistry.registerKeyBinding(mainKey);
        ClientRegistry.registerKeyBinding(reportKey);
        ClientRegistry.registerKeyBinding(weblateKey);
        ClientRegistry.registerKeyBinding(mcmodKey);
        ClientRegistry.registerKeyBinding(reloadKey);
    }

    // 在打开 GUI 情况下的按键触发
    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        // 检测配置
        if (I18nConfig.key.closedKey || (I18nConfig.internationalization.openI18n && !I18nUtils.isChinese())) {
            return;
        }

        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

        // 取消重复显示
        if (showed) {
            try {
                if (!Keyboard.isKeyDown(reportKey.getKeyCode()) && !Keyboard.isKeyDown(weblateKey.getKeyCode()) && !Keyboard.isKeyDown(mcmodKey.getKeyCode()) && !Keyboard.isKeyDown(reloadKey.getKeyCode())) {
                    showed = false;
                }
            } catch (IndexOutOfBoundsException ex) {
                showed = false;
            }
            return;
        }

        // 重载汉化
        if (reloadLocalization()) {
            showed = true;
            return;
        }

        // 原版判定
        if (guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
            Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                showed = handleKey(slotUnderMouse.getStack());
                return;
            }
        }

        // JEI 支持
        if (Loader.isModLoaded("jei")) {
            try {
                showed = handleKey(Internal.getRuntime().getItemListOverlay().getStackUnderMouse());
            } catch (Throwable ex) {
                I18nUpdateMod.logger.warn("Unable to get JEI item.", ex);
            }
        }
    }

    // 非 GUI 情况下的按键触发
    @SubscribeEvent
    public void onKeyPressNoGui(InputEvent.KeyInputEvent e) {
        // 最开始，检测是否启用国际化配置
        if (I18nConfig.internationalization.openI18n && !I18nUtils.isChinese()) {
            return;
        }

        // 接下来检测是否关闭键位
        if (I18nConfig.key.closedKey) {
            return;
        }

        // 取消重复显示
        if (showed) {
            if (keyCodeCheck(reloadKey.getKeyCode()) && !Keyboard.isKeyDown(reloadKey.getKeyCode())) {
                showed = false;
            }
            return;
        }
        showed = reloadLocalization();
    }

    /**
     * 获取物品信息，并打开浏览器
     *
     * @param stack 物品
     * @return 是否成功
     */
    private boolean openReport(ItemStack stack) {
        String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
        String url = I18nConfig.key.reportURL;
        try {
            GuiScreen.setClipboardString(text);
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取物品信息，并打开 weblate 对应界面
     *
     * @param stack 物品
     * @return 是否成功
     */
    private boolean openWeblate(ItemStack stack) {
        String displayName, assetsName;

        // 先进行字符获取与转义
        try {
            displayName = URLEncoder.encode(stack.getDisplayName(), "UTF-8");
            assetsName = URLEncoder.encode(stack.getItem().getRegistryName().getResourceDomain(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        // 打开对应连接
        String url = String.format("https://weblate.sayori.pw/translate/langpack/%s/zh_cn/?q=%s&search=substring&source=on&target=on", assetsName, displayName);
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取物品信息，并打开 mcmod 对应界面
     *
     * @param stack 物品
     * @return 是否成功
     */
    private boolean openMcmod(ItemStack stack) {
        String modName, regName, displayName, url;
        int metadata, mcmodApiNum;

        // 先进行字符获取与转义
        try {
            modName = URLEncoder.encode(stack.getItem().getCreatorModId(stack), "UTF-8");
            regName = URLEncoder.encode(stack.getItem().getRegistryName().toString(), "UTF-8");
            displayName = URLEncoder.encode(stack.getDisplayName(), "UTF-8");
            metadata = stack.getMetadata();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 访问 mcmod 百科 api，获取物品对应 id
        try {
            URL apiUrl = new URL(String.format("https://api.mcmod.cn/getItem/?regname=%s&metadata=%d", regName, metadata));
            mcmodApiNum = Integer.parseInt(IOUtils.readLines(apiUrl.openStream(), StandardCharsets.UTF_8).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 通过获取的 id 判定生成什么连接
        // 有则去往对应物品，无则尝试进行搜索
        url = mcmodApiNum > 0 ? String.format("https://www.mcmod.cn/item/%d.html", mcmodApiNum) : String.format("https://www.mcmod.cn/s?key=%s+%s&i18nmod=true", modName, displayName);

        // 打开对应连接
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            I18nUpdateMod.logger.error("打开链接失败", e);
            return false;
        }
        return true;
    }

    /**
     * 获取输入按键，进行不同处理
     *
     * @param stack 物品
     * @return 是否成功
     */
    private boolean handleKey(ItemStack stack) {
        if (stack != null && stack != ItemStack.EMPTY && stack.getItem() != Items.AIR) {
            // 问题报告界面的打开
            if (keyCodeCheck(reportKey.getKeyCode()) && Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == reportKey.getKeyCode()) {
                return openReport(stack);
            }
            // Weblate 翻译界面的打开
            else if (keyCodeCheck(weblateKey.getKeyCode()) && Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == weblateKey.getKeyCode()) {
                return openWeblate(stack);
            }
            // mcmod 百科界面的打开
            else if (keyCodeCheck(mcmodKey.getKeyCode()) && Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == mcmodKey.getKeyCode()) {
                return openMcmod(stack);
            }
        }
        return false;
    }

    /**
     * 单独功能，快速重载语言文件
     *
     * @return 是否成功
     */
    private boolean reloadLocalization() {
        if (keyCodeCheck(reportKey.getKeyCode()) && Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == reloadKey.getKeyCode()) {
            Minecraft.getMinecraft().getLanguageManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_reload.success"));
            return true;
        }
        return false;
    }

    private boolean keyCodeCheck(int keyCode) {
        return 1 < keyCode && keyCode < 256;
    }
}
