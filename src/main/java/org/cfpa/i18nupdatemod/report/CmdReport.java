package org.cfpa.i18nupdatemod.report;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
    @SideOnly(Side.CLIENT)
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ItemStack stack = Minecraft.getMinecraft().player.inventory.getCurrentItem();
        if (!stack.isEmpty()) {
            String text = String.format("模组ID：%s\n非本地化名称：%s\n显示名称：%s", stack.getItem().getCreatorModId(stack), stack.getItem().getUnlocalizedName(), stack.getDisplayName());
            String url = "https://wj.qq.com/s/2135580/0e03/";
            try {
                copyToClipboard(text);
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception urlException) {
                urlException.printStackTrace();
            }
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("请将要反馈的物品拿在手上"));
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
