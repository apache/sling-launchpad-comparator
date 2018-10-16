package org.apache.sling.tooling.lc.git;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitChangeLogFinder {
    
    public static void main(String[] args) throws IOException, GitAPIException  {
        new GitChangeLogFinder("..").getChanges("org.apache.sling.adapter", "2.1.2", "2.1.6")
            .stream().forEach(System.out::println);
    }
    
    private final String slingRepoCheckoutDir;
    
    /**
     * @param slingRepoCheckoutDir the <tt>repo</tt> root for Apache Sling
     */
    public GitChangeLogFinder(String slingRepoCheckoutDir) {
        this.slingRepoCheckoutDir = slingRepoCheckoutDir;
    }

    public List<String> getChanges(String artifactId, String from, String to) throws IOException, GitAPIException {
        
        Path repoPath = Paths.get(slingRepoCheckoutDir, artifactId.replace('.', '-'), ".git");
        
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        
        Repository repository = repositoryBuilder.setGitDir(repoPath.toFile())
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();

        Ref fromTag = getTagChecked(repository, artifactId, from);
        Ref toTag = getTagChecked(repository, artifactId, to);
        
        Git git = Git.wrap(repository);
        
        fromTag = repository.getRefDatabase().peel(fromTag);
        toTag = repository.getRefDatabase().peel(toTag);
        
        List<String> commits = new ArrayList<>();
        git.log()
            .addRange(fromTag.getPeeledObjectId(), toTag.getPeeledObjectId())
            .call()
            .forEach( c -> commits.add(c.getShortMessage()));
        
        return commits;
    }

    private Ref getTagChecked(Repository repository, String artifactId, String version) throws IOException {
        
        final String tagName = artifactId + "-" + version;
        final Ref ref = repository.getRefDatabase().getRef(Constants.R_TAGS + tagName);
        if ( ref == null )
            throw new RuntimeException("No tag " + tagName + " found in git repo at " + repository.getDirectory());
        return ref;
    }
}
