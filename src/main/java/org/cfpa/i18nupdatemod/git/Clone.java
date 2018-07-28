package org.cfpa.i18nupdatemod.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import java.io.File;

public class Clone {

    public static String cloneRepository(String url,String localPath)
    {
        try{
            System.out.println("Cloning......");

            CloneCommand cc = Git.cloneRepository().setURI(url);
            cc.setDirectory(new File(localPath)).call();

            System.out.println("Success......");

            return "success";
        }catch(Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }
    }

