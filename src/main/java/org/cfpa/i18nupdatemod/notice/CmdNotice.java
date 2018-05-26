package org.cfpa.i18nupdatemod.notice;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class CmdNotice extends CommandBase {
    public static final List<String> tabChoose = new ArrayList<>();

    @Override
    public String getName() {
        return "lang_notice";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        new NoticeShower();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
