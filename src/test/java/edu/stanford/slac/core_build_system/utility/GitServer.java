package edu.stanford.slac.core_build_system.utility;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitServer {

    private static File tempDir;

    public static String setupServer(List<String> branchNames) throws Exception {
        // Create a temporary directory for the repository
        tempDir = File.createTempFile("testRepo", "");
        if (!tempDir.delete()) {
            throw new IOException("Could not delete temporary file " + tempDir);
        }
        if (!tempDir.mkdir()) {
            throw new IOException("Could not create directory " + tempDir);
        }

        // Initialize a new Git repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Git git = Git.init().setDirectory(tempDir).setInitialBranch("main").call();

        // Add a file to the repository
        File myFile = new File(tempDir, "testfile.txt");
        if (!myFile.createNewFile()) {
            throw new IOException("Could not create file " + myFile);
        }
        git.add().addFilepattern("testfile.txt").call();
        git.commit().setMessage("Initial commit").call();
        createBranch(git, "branch1");
        createBranch(git, "branch2");
        return tempDir.getAbsolutePath();
    }

    private static void createBranch(Git git, String branchName) throws GitAPIException, IOException {
        // Checkout a new branch
        git.checkout().setCreateBranch(true).setName(branchName).call();

        // Add a file to the new branch
        File branchFile = new File(tempDir, branchName + ".txt");
        if (!branchFile.createNewFile()) {
            throw new IOException("Could not create file " + branchFile);
        }
        git.add().addFilepattern(branchName + ".txt").call();
        git.commit().setMessage("Initial commit on " + branchName).call();

        // Checkout back to main
        git.checkout().setName("main").call();
    }

    public static void cleanup() {
        if (tempDir != null && tempDir.exists()) {
            deleteDirectory(tempDir);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

}
