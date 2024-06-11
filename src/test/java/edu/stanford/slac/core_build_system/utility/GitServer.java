package edu.stanford.slac.core_build_system.utility;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        Path sourcePath = Paths.get(new ClassPathResource("test-project").getURI());
        // copy test project and add to git
        copy(git, sourcePath, tempDir.toPath());
        git.add().addFilepattern(".").call();
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


    static private void copy(Git git, Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                try {
                    Files.copy(dir, targetDir);
                } catch (FileAlreadyExistsException e) {
                    if (!Files.isDirectory(targetDir))
                        throw e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetPath = target.resolve(source.relativize(file));
                Files.copy(file, targetPath);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
