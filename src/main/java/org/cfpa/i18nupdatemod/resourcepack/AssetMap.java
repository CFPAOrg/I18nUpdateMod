package org.cfpa.i18nupdatemod.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

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
        // TODO AssetMap更新
        // TODO Asset Map检查（可能没必要做，鸽了
        // 加载jar包中的json文件
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStreamReader in = new InputStreamReader(classLoader.getResourceAsStream("assets/i18nmod/asset_map/asset_map.json"));
        this.map = loadJson(in);
    }

    private Map<String, ArrayList<String>> loadJson(Reader in) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(in, Map.class);
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

}
