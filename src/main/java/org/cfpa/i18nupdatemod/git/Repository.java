package org.cfpa.i18nupdatemod.git;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

public class Repository {
	static List<String> remoteURLs;
	private File localPath;
	public Git gitRepo=null;
	private String branch;
	public List<RemoteConfig> remoteList;
	
	public Repository(String localPath) {
		//TODO config
		remoteURLs = new ArrayList<String>();
		remoteURLs.add("https://github.com/CFPAOrg/Minecraft-Mod-Language-Package.git");
		
		this.localPath=new File(localPath);
		this.branch="1.12.2-release";
		initRepo();
	}
	
	private void initRepo() {
		if(localPath.exists()) {
			try {
				gitRepo=Git.open(localPath);
				this.remoteList=gitRepo.remoteList().call();
				// TODO 检查/重设remote list
			} catch (Exception e) {}
		}
		if(gitRepo==null) {
			try {
				gitRepo=Git.init().setDirectory(localPath).call();
				this.remoteList=new ArrayList<RemoteConfig> ();
				for(int i=0; i<remoteURLs.size(); i++) {
					configRemote("origin"+String.valueOf(i), remoteURLs.get(i), this.branch);
				}
				this.remoteList=gitRepo.remoteList().call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void configRemote(String name, String url, String branchName){
		try {
			StoredConfig config = gitRepo.getRepository().getConfig();
            config.setString("remote", name, "url", url);
            config.setString("remote", name, "fetch", "+refs/heads/"+branchName+":refs/remotes/origin/"+branchName);
            config.save();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void close() {
		gitRepo.getRepository().close();
	}

	public void pull() {
		for(RemoteConfig remoteConfig : remoteList) {
			// TODO 检查连接情况
			if(true) {
				try {
					gitRepo.pull().setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).setRemote(remoteConfig.getName()).setRemoteBranchName(branch).call();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				return;
			}
		}
		logger.error("仓库更新失败");
	}
	
	public static String getSubPathOfAsset(String domain) {
		return "assets/"+domain;
	}

	public static Collection<String> getSubPathsOfAssets(Set<String> assetDomains) {
		return assetDomains.stream().map(Repository::getSubPathOfAsset).collect(Collectors.toSet());
		
	}
	
	public File getLocalPath() {
		return localPath;
	}
}
