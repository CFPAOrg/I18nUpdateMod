package org.cfpa.i18nupdatemod.report;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.URI;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ReportKey {
    private static KeyBinding reportKey = new KeyBinding("key.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");
    private static boolean showed = false;

    public ReportKey() {
        ClientRegistry.registerKeyBinding(reportKey);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Pre e) {
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (showed) {
            if (!Keyboard.isKeyDown(reportKey.getKeyCode())) {
                showed = false;
            }
            return;
        }
        if (guiScreen instanceof GuiContainer && Keyboard.getEventKey() == reportKey.getKeyCode()) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
            Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                ItemStack stack = slotUnderMouse.getStack();
                if (!stack.isEmpty()) {
                    String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
                    String url = "https://wj.qq.com/s/2135580/0e03/";
                    try {
                        copyToClipboard(text);
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception urlException) {
                        urlException.printStackTrace();
                    }
                    showed = true;
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
}
