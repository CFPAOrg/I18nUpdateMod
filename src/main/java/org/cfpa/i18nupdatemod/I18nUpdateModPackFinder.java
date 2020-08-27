package org.cfpa.i18nupdatemod;

import net.minecraft.resources.*;
import net.minecraft.resources.ResourcePackInfo.IFactory;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class I18nUpdateModPackFinder implements IPackFinder {

    public static final I18nUpdateModPackFinder RESOUCE = new I18nUpdateModPackFinder("Resource Pack", new File(System.getProperty("user.home") + "/.i18n/1.16/i18n.zip"));

    private final File loaderDirectory;

    private I18nUpdateModPackFinder(String type, File loaderDirectory) {

        this.loaderDirectory = loaderDirectory;
    }

    @Override
    public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> packs, IFactory<T> factory) {
        final String packName = "i18n";
        final T packInfo = ResourcePackInfo.createResourcePack(packName, true, () -> new FilePack(loaderDirectory), factory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.field_232625_a_);
        System.out.println(packInfo);
        if (packInfo != null) {
            packs.accept(packInfo);
        }
    }

    private Supplier<IResourcePack> getAsPack(File file) {

        return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
    }

}