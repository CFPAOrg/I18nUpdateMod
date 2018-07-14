package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CmdReload extends CommandBase {
    @Override
    public String getName() {
        return "lang_reload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "快速重载语言文件，用于测试汉化";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().getLanguageManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_reload.success"));
    }
}
