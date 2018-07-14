package org.cfpa.i18nupdatemod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CmdGetLangpack extends CommandBase {
    @Override
    public String getName() {
        return "lang_get";
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
        if (args.length == 0) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.empty"));
        }

        if (Minecraft.getMinecraft().getResourceManager().getResourceDomains().contains(args[0])) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_start", args[0]));
            // TODO
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.right_stop"));
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("message.i18nmod.cmd_get_langpack.not_found", args[0]));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 0) {
            return new ArrayList<>(Minecraft.getMinecraft().getResourceManager().getResourceDomains());
        }

        List<String> availableArgs = new ArrayList<>();
        for (String modid : Minecraft.getMinecraft().getResourceManager().getResourceDomains()) {
            if (modid.indexOf(args[0]) == 0) {
                availableArgs.add(modid);
            }
        }
        return availableArgs;
    }
}