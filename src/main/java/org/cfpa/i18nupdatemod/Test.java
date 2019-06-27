package org.cfpa.i18nupdatemod;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import org.cfpa.i18nupdatemod.resourcepack.AssetMap;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.Minecraft;
public class Test {

	public static void main(String[] args) {
		
		
		String url="https://git.coding.net/baka943/Minecraft-Mod-Language-Package.git";
		File localPath=new File("/Users/liuxinyuan/Desktop/a/git");
		String subpath="project/assets/0x_trans_fix/lang";
		try {
			//Git.open(checkout);
			Git gitRepo=Git.cloneRepository().setURI(url).setDirectory(localPath).setNoCheckout(true).call();
			gitRepo.checkout().setName("origin/1.12.2").setStartPoint("origin/1.12.2").addPath(subpath).call();
			gitRepo.getRepository().close();
			System.out.print(111111);
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}

}
