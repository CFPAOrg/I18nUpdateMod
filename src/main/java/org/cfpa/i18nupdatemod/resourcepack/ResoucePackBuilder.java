package org.cfpa.i18nupdatemod.resourcepack;

import java.io.File;
import java.util.Set;

import org.cfpa.i18nupdatemod.I18nConfig;

import net.minecraft.client.Minecraft;

public class ResoucePackBuilder {
	private Set<String> modidSet;
	private File rootPath;
	private AssetMap assetMap;
	private Set<String> assetDomains;
	
	public ResoucePackBuilder() {
		modidSet = net.minecraftforge.fml.common.Loader.instance().getIndexedModList().keySet();
		rootPath = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toString(), I18nConfig.download.langPackName);
		assetMap = new AssetMap();
	}
	
	public void setAssetMap(AssetMap assetMap) {
		this.assetMap=assetMap;
	}
	
	public boolean initAndCheckUpdate() {
		assetDomains = assetMap.getAssetDomains(modidSet);
		// 不存在资源包文件夹
		if(!rootPath.exists()) {
			this.initResourcePack();
        	return true;
        }
		// 超过更新检查时间间隔
		if(intervalDaysCheck()) {
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
		// TODO Auto-generated method stub
		return null;
	}

	private boolean intervalDaysCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	private void initResourcePack() {
		rootPath.mkdirs();
		// TODO Auto-generated method stub
		
	}

	public void build() {
        
	}

}
