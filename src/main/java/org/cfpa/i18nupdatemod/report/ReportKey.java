package org.cfpa.i18nupdatemod.report;

import mezz.jei.Internal;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.runtime.JeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.I18nUtils;
import org.cfpa.i18nupdatemod.config.MainConfig;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.URI;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ReportKey {
    private static KeyBinding reportKey = new KeyBinding("key.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");
    private static KeyBinding weblateKey = new KeyBinding("key.weblate_key.desc", Keyboard.KEY_L, "key.category.i18nmod");
    private static boolean showed = false;

    public ReportKey() {
        ClientRegistry.registerKeyBinding(reportKey);
        ClientRegistry.registerKeyBinding(weblateKey);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Pre e) {
        // 最开始，是否启用国际化配置
        if (MainConfig.internationalization.openI18n) {
            if (!I18nUtils.isChinese()) {
                return;
            }
        }

        // 获取当前屏幕数据
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

        // 用于取消重复显示
        if (showed) {
            if (!Keyboard.isKeyDown(reportKey.getKeyCode()) && !Keyboard.isKeyDown(weblateKey.getKeyCode())) {
                showed = false;
            }
            return;
        }

        // 当当前 GUI 为继承自原版 GuiContainer 的事物的时候
        if (guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
            Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                ItemStack stack = slotUnderMouse.getStack();
                if (!stack.isEmpty()) {
                    // 问题报告界面的打开
                    if (Keyboard.getEventKey() == reportKey.getKeyCode()) {
                        showed = openBrowse(stack);
                    }
                    // Weblate 翻译界面的打开
                    if (Keyboard.getEventKey() == weblateKey.getKeyCode()) {
                        showed = openWeblate(stack);
                    }
                }
            }
        }

        // 当前 GUI 为 JEI 时候
        if (Loader.isModLoaded("jei")) {
            JeiRuntime runtime = Internal.getRuntime();
            IngredientListOverlay ingredientListOverlay = runtime.getItemListOverlay();
            ItemStack stack = ingredientListOverlay.getStackUnderMouse();
            if (stack != null) {
                // 问题报告界面的打开
                if (Keyboard.getEventKey() == reportKey.getKeyCode()) {
                    showed = openBrowse(stack);
                }
                // Weblate 翻译界面的打开
                if (Keyboard.getEventKey() == weblateKey.getKeyCode()) {
                    showed = openWeblate(stack);
                }
            }
        }
    }

    // 感谢：https://blog.csdn.net/xietansheng/article/details/70478266
    public static void copyToClipboard(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    // 获取物品信息，并且打开浏览器
    public static boolean openBrowse(ItemStack stack) {
        String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
        String url = MainConfig.key.reportURL;
        try {
            copyToClipboard(text);
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception urlException) {
            urlException.printStackTrace();
        }
        return true;
    }

    // 获取物品信息，并打开weblate对应界面
    public static boolean openWeblate(ItemStack stack) {
        String displayName = stack.getDisplayName();
        String assetsName = stack.getItem().getRegistryName().getResourceDomain();
        // TODO：特殊字符的 URL 转义
        String url = String.format("https://weblate.sayori.pw/translate/langpack/%s/zh_cn/?q=%s&search=substring&source=on&target=on", assetsName, displayName);
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception urlException) {
            urlException.printStackTrace();
        }
        return true;
    }
}
