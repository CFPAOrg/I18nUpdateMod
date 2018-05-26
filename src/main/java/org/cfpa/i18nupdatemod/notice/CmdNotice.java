package org.cfpa.i18nupdatemod.notice;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.cfpa.i18nupdatemod.notice.NoticeShower;

public class CmdNotice extends CommandBase {

    @Override
    public String getName() {
        return "lang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        NoticeShower.showNotice();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
