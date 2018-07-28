package org.cfpa.i18nupdatemod.git;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

public class ZipCompressorByAnt {

    private File zipFile;


    public ZipCompressorByAnt(String finalFile) {
        zipFile = new File(finalFile);
    }

    public void compress(String srcPathName) {
        File srcdir = new File(srcPathName);
        if (!srcdir.exists()){
            throw new RuntimeException(srcPathName + "不存在！");
        }

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        //fileSet.setIncludes("**/*.java"); //包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
        //fileSet.setExcludes(...); //排除哪些文件或文件夹
        zip.addFileset(fileSet);
        zip.execute();
    }
}