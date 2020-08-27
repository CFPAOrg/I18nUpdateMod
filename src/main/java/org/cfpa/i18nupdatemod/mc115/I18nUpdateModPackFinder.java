package org.cfpa.i18nupdatemod.mc115;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraftforge.versions.mcp.MCPVersion;

import java.io.File;
import java.util.Map;

public final class I18nUpdateModPackFinder implements IPackFinder {

    public static final I18nUpdateModPackFinder RESOUCE = new I18nUpdateModPackFinder("Resource Pack", new File(System.getProperty("user.home") + "/.i18n/"+ MCPVersion.getMCVersion() +"/i18n.zip"));

    private final File loaderDirectory;

    private I18nUpdateModPackFinder(String type, File loaderDirectory) {

        this.loaderDirectory = loaderDirectory;
    }
    public static void init(){
        Minecraft.getInstance().getResourcePackList().addPackFinder(RESOUCE);

    }
    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap (Map<String, T> packs, IFactory<T> factory) {
        final String packName = "i18n";
        final T packInfo = ResourcePackInfo.createResourcePack(packName, true, () -> new FilePack(loaderDirectory), factory, ResourcePackInfo.Priority.TOP);
        if (packInfo != null) {
            packs.put(packName,packInfo);
        }
    }
}