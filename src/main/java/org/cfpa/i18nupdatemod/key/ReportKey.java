package org.cfpa.i18nupdatemod.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = I18nUpdateMod.MODID)
public class ReportKey {
    private static KeyBinding reportKey = new KeyBinding("key.report_key.desc", Keyboard.KEY_K, "key.category.i18nmod");

    public ReportKey() {
        ClientRegistry.registerKeyBinding(reportKey);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    @SideOnly(Side.CLIENT)
    public static void onKeyPress(RenderTooltipEvent.PostText event) {
        if (reportKey.isPressed()) {
            ItemStack stack = event.getStack();
            System.out.println(stack.getItem().getUnlocalizedName());
            System.out.println(stack.getDisplayName());
        }
    }
}
