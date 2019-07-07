package org.cfpa.i18nupdatemod.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import org.cfpa.i18nupdatemod.I18nConfig;

import java.io.*;
import java.util.*;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

public class AssetMap {
    private Map<String, List<String>> map;

    private static AssetMap INSTANCE;

    public static AssetMap instance() {
        if (INSTANCE == null) {
            INSTANCE = new AssetMap();
        }
        return INSTANCE;
    }

    private AssetMap() {
        // 优先从资源包中读取 json 文件
        try {
            File f = new File(
                    Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString() + File.separator +
                            I18nConfig.download.i18nLangPackName + File.separator +
                            "assets" + File.separator + "i18nmod" + File.separator + "asset_map" + File.separator + "asset_map.json"
            );
            InputStreamReader in = new InputStreamReader(new FileInputStream(f));
            this.map = loadJson(in);
            return;
        } catch (Exception ignore) {
        }

        // 加载 jar 包中的 json 文件
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStreamReader in = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("assets/i18nmod/asset_map/asset_map.json")));
        this.map = loadJson(in);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> loadJson(Reader in) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(in, Map.class);
    }

    private Map<String, List<String>> loadJson(File f) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        } catch (Exception e) {
            logger.error("error loading json", e);
        }
        return loadJson(in);
    }

    public Set<String> getAssetDomains(Set<String> modidSet) {
        Set<String> assetDomains = new HashSet<>();
        modidSet.stream().map(this::get).forEach(assetDomains::addAll);
        assetDomains.addAll(this.get("<DEFAULT>"));
        // 避免遗漏，加入所有未知映射关系的asset
        assetDomains.addAll(this.get("<UNKNOWN>"));
        return assetDomains;
    }

    public Collection<String> get(String modid) {
        Collection<String> ret = this.map.get(modid);

        return ret == null ? new HashSet<>() : ret;
    }

    public void update(File assetMap) {
        this.map = loadJson(assetMap);
    }

}
