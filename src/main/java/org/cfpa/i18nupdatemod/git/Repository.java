package org.cfpa.i18nupdatemod.git;

import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cfpa.i18nupdatemod.I18nConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;

public class Repository {
    static String[] remoteURLs;
    private File localPath;
    public Git gitRepo = null;
    private String branch;
    public List<RemoteConfig> remoteList;
    private Set<String> assetDomains;

    public Repository(String localPath, Set<String> assetDomains) {
        remoteURLs = I18nConfig.download.remoteRepoURL;
        this.localPath = new File(localPath);
        this.assetDomains = assetDomains;
        this.branch = "1.12.2-release";
        initRepo();
    }

    private void initRepo() {
        if (localPath.exists()) {
            try {
                gitRepo = Git.open(localPath);
                this.remoteList = gitRepo.remoteList().call();
                // TODO 检查/重设remote list
            } catch (Exception e) {
            }
        }
        if (gitRepo == null) {
            try {
                gitRepo = Git.init().setDirectory(localPath).call();
                this.remoteList = new ArrayList<RemoteConfig>();
                for (int i = 0; i < remoteURLs.length; i++) {
                    configRemote("origin" + String.valueOf(i), remoteURLs[i], this.branch);
                }
                this.remoteList = gitRepo.remoteList().call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void configRemote(String name, String url, String branchName) {
        try {
            StoredConfig config = gitRepo.getRepository().getConfig();
            config.setString("remote", name, "url", url);
            config.setString("remote", name, "fetch",
                    "+refs/heads/" + branchName + ":refs/remotes/origin/" + branchName);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        gitRepo.getRepository().close();
    }

    public void fetch(ProgressMonitor monitor) {
        boolean success = false;
        for (RemoteConfig remoteConfig : remoteList) {
            // TODO 检查连接情况
            if (true) {
                try {
                    // fetch
                    gitRepo.fetch()
                    .setProgressMonitor(monitor)
                    .setRemote(remoteConfig.getName())
                    .call();
                    success = true;
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if(!success) {
            logger.error("仓库更新失败");
            return;
        }
    }
    
    public void sparseCheckout(Collection<String> subPathSet, ProgressMonitor monitor) {
        try {
            // create branch and set upstream
            gitRepo.branchCreate()
            .setName(branch)
            .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
            .setStartPoint("origin/"+branch)
            .setForce(true)
            .call();
            
            // reset to remote head
            gitRepo.reset()
            .setProgressMonitor(monitor)
            .setMode(ResetType.SOFT)
            .setRef("refs/remotes/origin/"+branch)
            .call();
            
            // sparse checkout
            CheckoutCommand checkoutCommand = gitRepo.checkout();
            checkoutCommand.setProgressMonitor(monitor)
            .setName(branch)
            .setStartPoint(branch);
            for(String subpath: subPathSet) {
                checkoutCommand.addPath(subpath);
            }
            checkoutCommand.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }

    public static String getSubPathOfAsset(String domain) {
        return "assets/" + domain;
    }

    public Collection<String> getSubPaths() {
        return assetDomains.stream().map(Repository::getSubPathOfAsset).collect(Collectors.toSet());

    }

    public File getLocalPath() {
        return localPath;
    }
}
