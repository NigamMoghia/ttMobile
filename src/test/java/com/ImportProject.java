package com;




import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;

/**
 * Created by RGupta on 12/28/2017.
 */
public class ImportProject {

    @Test
    public void pullLatestFromBitBucket() throws Exception{

        // Creation of a temp folder that will contain the Git repository
        File workingDirectory = new File("C:\\Temp\\NewTest");//File.createTempFile("C:\\Temp\\NewTest1","");
        //workingDirectory.delete();
       // deleteDirectory(workingDirectory);
        FileUtils.deleteDirectory(workingDirectory);
        FileUtils.forceMkdir(workingDirectory);

        //Create a Repository object
       // Repository repo = FileRepositoryBuilder.create(new File(workingDirectory, ".git"));

        Git git = Git.cloneRepository().setURI("http://RGupta@bitbucket.utd.com:7990/scm/cd/automation.git").setDirectory(workingDirectory).call();
        System.out.println("Look At Git Status Call " + git.status().call());
        git.pull().call();

    }

    public boolean deleteDirectory(File dir){
        if(dir.isDirectory()){
            File[] children = dir.listFiles();
            for(int i = 0 ; i<children.length ; i++){
                boolean success = deleteDirectory(children[i]);
                if(!success)
                    return false;
            }
        }
        // Now either dir is a file or an emoty directory --- so we can directly delete it.
        System.out.println("Removing File or directory "+ dir.getName());
        return dir.delete();
    }

 /*   @Sub_Topics_do_not_link_to_missing_file
    public static  void getProjectFromMaster() throws  Exception{
        File file = new File("C:\\Temp\\NewTest");
        FileUtils.deleteDirectory(file);
        FileUtils.forceMkdir(file);
        URL url = new URL("http://bitbucket.utd.com:7990/projects/CD/repos/automation/browse/export-Test");
        FileUtils.copyURLToFile(url,file);

    }*/
}
