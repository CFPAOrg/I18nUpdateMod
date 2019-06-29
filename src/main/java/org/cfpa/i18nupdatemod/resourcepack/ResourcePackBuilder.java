package org.cfpa.i18nupdatemod.resourcepack;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Set;

import org.cfpa.i18nupdatemod.I18nConfig;
import org.cfpa.i18nupdatemod.I18nUtils;
import org.cfpa.i18nupdatemod.git.Repository;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import net.minecraft.client.Minecraft;

public class ResourcePackBuilder {
	private Set<String> modidSet;
	private File rootPath;
	private File assetFolder;
	private AssetMap assetMap;
	private Set<String> assetDomains;
	private Set<String> path;
	
	public ResourcePackBuilder() {
		modidSet = net.minecraftforge.fml.common.Loader.instance().getIndexedModList().keySet();
		rootPath = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), I18nConfig.download.langPackName);
		assetFolder = new File(rootPath,"assets");
		assetMap = new AssetMap();
	}
	
	public void setAssetMap(AssetMap assetMap) {
		this.assetMap=assetMap;
	}
	
	public Set<String> getAssetDomains() {
		return assetDomains;
	}
	
	public boolean initAndCheckUpdate() {
		assetDomains = assetMap.getAssetDomains(modidSet);
		// 不存在资源包文件夹
		// TODO 检查资源包是否合法
		if(!(rootPath.exists() && assetFolder.exists())) {
			this.initResourcePack();
        	return true;
        }
		// 超过更新检查时间间隔
		if(longTimeNoUpdate()) {
			return true;
		}
		// 部分asset文件缺失，可能增加了mod
		for(String domain : assetDomains) {
			File assetFolder = getAssetFolder(domain);
			if(!assetFolder.exists())
				return true;
		}
		return false;
	}
	
	private File getAssetFolder(String domain) {
		return new File(assetFolder, domain);
	}

	private boolean longTimeNoUpdate() {
		File f = new File(rootPath, "pack.mcmeta");
        try {
            return (System.currentTimeMillis() - f.lastModified()) > (I18nConfig.download.maxDay * 24 * 3600 * 1000);
        } catch (Throwable e) {
            logger.error("检查文件日期失败", e);
            return true;
        }
	}

	private void initResourcePack() {
		assetFolder.mkdirs();
		// PNG 图标
		File icon = new File(rootPath,"pack.png");
		if(!icon.exists()) {
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream in = classLoader.getResourceAsStream("assets/i18nmod/icon/pack.png");
			try {
				Files.copy(in, icon.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// pack.mcmeta
		writePackMeta();
	}
	
	private void writePackMeta() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = df.format(new Date());
		// 这里给pack.mcmeta加了行注释，测试没问题，但理论上如果是json文件不能加注释
		dateTime="# 修改时间："+dateTime;
		File info=new File(rootPath, "pack.mcmeta");
		String meta="{\n" + 
				"  \"pack\": {\n" + 
				"    \"pack_format\": 3,\n" + 
				"    \"description\": \"I18n Update Mod 汉化包\"\n" + 
				"  }\n" + 
				"}\n";
		try {
			BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (info),"UTF-8"));
			writer.write(dateTime+"\n"+meta);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void build() {
		// 写pack.mcmeta文件，作为更新时间标记
		writePackMeta();
	}

	public void updateAllNeededFilesFromRepo(Repository repo) {
		//TODO 只复制需要更新的文件，可以考虑给copyDir方法加filter
		for(String domain:this.getAssetDomains()) {
    		copyAssetsFromRepo(domain, repo);
    	}
	}
	
	private void copyAssetsFromRepo(String domain, Repository repo) {
		I18nUtils.copyDir(new File(repo.getLocalPath(),Repository.getSubPathOfAsset(domain)).toPath(), new File(this.assetFolder, domain).toPath());
	}

}
