package org.cfpa.i18nupdatemod.resourcepack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class AssetMap {
	Map<String, ArrayList<String>> map;
	
	public AssetMap() {
		// 加载jar包中的json文件，这里不能使用MC原版的资源加载方式，因为这个函数是在MC资源加载之前被调用的。
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStreamReader in = new InputStreamReader(classLoader.getResourceAsStream("assets/i18nmod/asset_map/asset_map.json"));
		this.map=loadJson(in);
	}
	
	private Map<String, ArrayList<String>> loadJson(File f) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return loadJson(in);
	}
	
	private Map<String, ArrayList<String>> loadJson(Reader in) {
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, Object>>() {}.getType();  
		Map<String, ArrayList<String>> map = gson.fromJson(in, type);
		return map;
	}
	
	public Set<String> getAssetDomains(Set<String> modidSet) {
		Set<String> assetDomains=new HashSet<String>();
		for(String modid : modidSet) {
			assetDomains.addAll(this.get(modid));
		}
		assetDomains.addAll(this.get("<DEFAULT>"));
		// 避免遗漏，加入所有未知映射关系的asset
		assetDomains.addAll(this.get("<UNKNOWN>"));
		return assetDomains;
	}

	public Collection<? extends String> get(String modid) {
		Collection<? extends String> ret = this.map.get(modid);
		if(ret==null)
			return new ArrayList<String>();
		else return ret;
	}

}
