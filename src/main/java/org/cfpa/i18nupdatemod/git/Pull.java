package org.cfpa.i18nupdatemod.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.File;
import java.io.IOException;

;

public class Pull {
    public static String localPath = System.getProperty("java.io.tmpdir")+ File.separator+"Minecraft-Mod-Language-Package-1.12.2" ;
    public static void pull() throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(localPath+ File.separator+".git"));
        git.pull().setRemoteBranchName("1.12.2").call();
    }
}
