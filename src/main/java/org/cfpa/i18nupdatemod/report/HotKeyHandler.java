package org.cfpa.i18nupdatemod.report;

import mezz.jei.Internal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.I18nUtils;
import org.cfpa.i18nupdatemod.config.MainConfig;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class HotKeyHandler {
    private static final KeyBinding mainKey = new KeyBinding("key.main_key.desc", Keyboard.KEY_LCONTROL, "key.category.i18nmod");
    private static final KeyBinding reportKey = new KeyBinding("key.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");
    private static final KeyBinding weblateKey = new KeyBinding("key.weblate_key.desc", Keyboard.KEY_L, "key.category.i18nmod");
    private static final KeyBinding mcmodKey = new KeyBinding("key.mcmod_key.desc", Keyboard.KEY_M, "key.category.i18nmod");
    private static boolean showed = false;

    private HotKeyHandler() {/*NO Instance*/}

    public static void register() {
        ClientRegistry.registerKeyBinding(mainKey);
        ClientRegistry.registerKeyBinding(reportKey);
        ClientRegistry.registerKeyBinding(weblateKey);
        ClientRegistry.registerKeyBinding(mcmodKey);
    }

    @SubscribeEvent
    public static void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Pre e) {
        // 最开始，检测是否启用国际化配置
        if (MainConfig.internationalization.openI18n && !I18nUtils.isChinese()) {
            return;
        }

        // 获取当前屏幕数据
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

        // 取消重复显示
        if (showed) {
            if (!Keyboard.isKeyDown(reportKey.getKeyCode()) && !Keyboard.isKeyDown(weblateKey.getKeyCode()) && !Keyboard.isKeyDown(mcmodKey.getKeyCode())) {
                showed = false;
            }
            return;
        }

        // 原版判定
        if (guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
            Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                showed = keyHandler(slotUnderMouse.getStack());
                return;
            }
        }
        // JEI 支持
        if (Loader.isModLoaded("jei")) {
            showed = keyHandler(Internal.getRuntime().getIngredientListOverlay().getStackUnderMouse());
        }
    }

    /**
     * 获取物品信息，并打开浏览器
     *
     * @param stack 物品
     * @return 是否成功
     */
    public static boolean openReport(ItemStack stack) {
        String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
        String url = MainConfig.key.reportURL;
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
    public static boolean openWeblate(ItemStack stack) {
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
    public static boolean openMcmod(ItemStack stack) {
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
    public static boolean keyHandler(ItemStack stack) {
        if (stack != null && stack != ItemStack.EMPTY && stack.getItem() != Items.AIR) {
            // 问题报告界面的打开
            if (Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == reportKey.getKeyCode()) {
                return openReport(stack);
                // Weblate 翻译界面的打开
            } else if (Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == weblateKey.getKeyCode()) {
                return openWeblate(stack);
                // mcmod 百科界面的打开
            } else if (Keyboard.isKeyDown(mainKey.getKeyCode()) && Keyboard.getEventKey() == mcmodKey.getKeyCode()) {
                return openMcmod(stack);
            }
        }
        return false;
    }
}
