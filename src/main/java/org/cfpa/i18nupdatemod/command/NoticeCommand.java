package org.cfpa.i18nupdatemod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.cfpa.i18nupdatemod.notice.NoticeShower;

public class NoticeCommand extends CommandBase {

    @Override
    public String getName() {
        return "lang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "输入指令，显示通知";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        NoticeShower.showNotice();
    }
}
