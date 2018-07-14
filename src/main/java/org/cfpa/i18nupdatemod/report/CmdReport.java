package org.cfpa.i18nupdatemod.report;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cfpa.i18nupdatemod.config.MainConfig;

import java.awt.*;
import java.net.URI;

public class CmdReport extends CommandBase {
    @Override
    public String getName() {
        return "lang_report";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ItemStack stack = Minecraft.getMinecraft().player.inventory.getCurrentItem();
        if (!stack.isEmpty()) {
            String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
            String url = MainConfig.key.reportURL;
            try {
                GuiScreen.setClipboardString(text);
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception urlException) {
                urlException.printStackTrace();
            }
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("请将要反馈的物品拿在手上"));
        }
    }
}
