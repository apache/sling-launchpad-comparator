/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    public static void main(String[] args) throws IOException, GitAPIException {
        new GitChangeLogFinder("..")
                .getChanges("org.apache.sling.adapter", "2.1.2", "2.1.6").stream()
                        .forEach(System.out::println);
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

        Repository repository = repositoryBuilder
                .setGitDir(repoPath.toFile())
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
                .forEach(c -> commits.add(c.getShortMessage()));

        return commits;
    }

    private Ref getTagChecked(Repository repository, String artifactId, String version) throws IOException {

        final String tagName = artifactId + "-" + version;
        final Ref ref = repository.getRefDatabase().getRef(Constants.R_TAGS + tagName);
        if (ref == null)
            throw new RuntimeException("No tag " + tagName + " found in git repo at " + repository.getDirectory());
        return ref;
    }
}
