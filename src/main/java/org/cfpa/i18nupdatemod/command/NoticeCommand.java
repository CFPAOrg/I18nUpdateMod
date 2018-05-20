package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.cfpa.i18nupdatemod.gui.NoticeGui;

public class NoticeCommand extends CommandBase {
    private final String NOTICE_NAME = "lang";
    private final String NOTICE_HELP = "输入指令，显示通知";

    @Override
    public String getName() {
        return NOTICE_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return NOTICE_HELP;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().displayGuiScreen(new NoticeGui());
    }
}
