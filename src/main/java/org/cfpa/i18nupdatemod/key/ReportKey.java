package org.cfpa.i18nupdatemod.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.gui.ReportGui;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ReportKey {
    private static KeyBinding reportKey = new KeyBinding("key.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");

    public ReportKey() {
        ClientRegistry.registerKeyBinding(reportKey);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    @SideOnly(Side.CLIENT)
    public static void onKeyPress(GuiScreenEvent.KeyboardInputEvent.Pre e) {
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (guiScreen instanceof GuiContainer && Keyboard.getEventKey() == reportKey.getKeyCode()) {
            GuiContainer guiContainer = (GuiContainer) guiScreen;
            Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
            if (slotUnderMouse != null) {
                ItemStack stack = slotUnderMouse.getStack();
                if (!stack.isEmpty()) {
                    System.out.println(stack.getItem().getUnlocalizedName());
                    System.out.println(stack.getDisplayName());
                    Minecraft.getMinecraft().displayGuiScreen(new ReportGui());
                }
            }
        }
    }
}
