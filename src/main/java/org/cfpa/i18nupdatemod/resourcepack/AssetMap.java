package org.cfpa.i18nupdatemod.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

public class AssetMap {
    private Map<String, ArrayList<String>> map;

    private static AssetMap INSTANCE;

    public static AssetMap instance() {
        if (INSTANCE == null) {
            INSTANCE = new AssetMap();
        }
        return INSTANCE;
    }

    private AssetMap() {
        // TODO 读取资源包里的json文件
        // 加载jar包中的json文件
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStreamReader in = new InputStreamReader(classLoader.getResourceAsStream("assets/i18nmod/asset_map/asset_map.json"));
        this.map = loadJson(in);
    }

    @SuppressWarnings("unchecked")
    private Map<String, ArrayList<String>> loadJson(Reader in) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(in, Map.class);
    }

    private Map<String, ArrayList<String>> loadJson(File f) {
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
        this.map=loadJson(assetMap);
    }

}
