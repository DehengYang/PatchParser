package edu.lu.uni.serval.BugCommit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

import edu.lu.uni.serval.Configuration;
import edu.lu.uni.serval.BugCommit.parser.Pair;
import edu.lu.uni.serval.git.exception.GitRepositoryNotFoundException;
import edu.lu.uni.serval.git.exception.NotValidGitRepositoryException;
import edu.lu.uni.serval.git.travel.CommitDiffEntry;
import edu.lu.uni.serval.git.travel.GitRepository;
import edu.lu.uni.serval.utils.FileHelper;

public class PatchRelatedCommits {
	
	public void collectCommits(String projectsPath, String outputPath, String urlPath) {
		File[] projects = new File(projectsPath).listFiles();
		
		// dale
		if(Configuration.DELETE_COMMITS){
			FileHelper.deleteDirectory(outputPath);
		}

		for (File project : projects) {
			if (!project.isDirectory()) continue;
			String repoName = project.getName();
			String revisedFilesPath = "";
			String previousFilesPath = "";
			String gitRepoPath = project.getPath() + "/.git";
			GitRepository gitRepo = new GitRepository(gitRepoPath, revisedFilesPath, previousFilesPath);
			try {
				System.out.println("\nProject: " + repoName);
				gitRepo.open();
				
				List<RevCommit> commits = gitRepo.getAllCommits(false);
				System.out.println("All Commits: " + commits.size());
				
				//dale: get all chart bugs diff info 
				Configuration.PROJECT = project.getName();
				BugDiff bugDiff = new BugDiff();
				Map<Integer, List<String>>  diffMap = bugDiff.getChart();
				Map<String,  List<Pair<String, String>>>  commitMap = new HashMap<>();
				List<CommitDiffEntry> commitsDiffentries = gitRepo.getCommitDiffEntries(commits);
				// TODO: only false in debugging mode.
				boolean flagWriteAllCommitsDiff = false;
				gitRepo.createFilesForGumTree(outputPath + "Keywords/" + repoName + "_allCommits/", commitsDiffentries, commitMap, flagWriteAllCommitsDiff);
//				gitRepo.outputCommitMessages(outputPath + "CommitMessage/" + repoName + "_allCommits.txt", commits);
				
				matchCommitId(diffMap, commitMap);
				
				List<RevCommit> keywordPatchCommits = gitRepo.filterCommits(commits); // searched by keywords.
				System.out.println("Keywords-matching Commits: " + keywordPatchCommits.size());
				
				List<CommitDiffEntry> unlinkedPatchCommitDiffentries = gitRepo.getCommitDiffEntries(keywordPatchCommits);
				gitRepo.createFilesForGumTree(outputPath + "Keywords/" + repoName + "/", unlinkedPatchCommitDiffentries, true);
				gitRepo.outputCommitMessages(outputPath + "CommitMessage/" + repoName + "_Keywords.txt", keywordPatchCommits);
//				gitRepo.outputCommitMessages(outputPath + "CommitMessage/" + repoName + "_Linked.txt", linkedPatchCommits);
				
				outputCommitIds(outputPath + "CommitIds/" + repoName + "_Keywords.txt", keywordPatchCommits);
//				outputCommitIds(outputPath + "CommitIds/" + repoName + "_Linked.txt", linkedPatchCommits);
			} catch (GitRepositoryNotFoundException e) {
				e.printStackTrace();
			} catch (NotValidGitRepositoryException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} finally {
				gitRepo.close();
			}
		}
	}

	private void matchCommitId(Map<Integer, List<String>> diffMap, Map<String, List<Pair<String, String>>> commitMap) throws IOException {
		for(Map.Entry<String, List<Pair<String,String>>> entry : commitMap.entrySet()){
			String commitId = entry.getKey();
			List<Pair<String,String>> diffEntryList =  entry.getValue();
			
			// read all diffs of a commit
			String diffEntryContent = "";
			for(Pair<String,String> diffEntry : diffEntryList){
				File diffEntryFile = new File(diffEntry.getSecond()); 
				if(diffEntryFile.exists()){// check if exist
					diffEntryContent += FileHelper.readFile(diffEntryFile).replace("\n", "");
				}
			}
			
			// each bug diff
			for(Map.Entry<Integer, List<String>> entryBug : diffMap.entrySet()){
				int id = entryBug.getKey();
				List<String> diffHunks = entryBug.getValue();
				int isThisCommitFlag = 0;
				
				for (String diffHunk : diffHunks){
					// when the diff hunk is "- }", may need to skip. i.e., do not add flag with 1
					if (diffEntryContent.indexOf(diffHunk) == -1){
						break; // not contain 
					}else{
						isThisCommitFlag++;
					}
				}
				
				// is this commit
				// may change to isThisCommitFlag == diffHunks.size()... Not sure.
				if (isThisCommitFlag == diffHunks.size()){ // > 0){
//					System.out.format("This is a buggy commit: %s\n%s\n%s\n\n", fileName, diffEntry, commitId);
					String targetPath = Configuration.BUGS + Configuration.PROJ_BUG + "/" + id + "/CommitId-" + commitId;
					String[] cmd3 = {"/bin/sh","-c", "cd " + Configuration.SUBJECTS_PATH + Configuration.PROJECT 
							+ " && " + " git show -s --format=%ci " 
							+ commitId
							};
					String commitTime = BugDiff.shellRun2(cmd3);
					
					FileHelper.outputToFile(targetPath, commitTime, true);
				}
			}
		}
	}

	private void outputCommitIds(String fileName, List<RevCommit> commits) {
		StringBuilder builder = new StringBuilder("Previous Commit Id ------ Patch Commit Id.");
		for (RevCommit commit : commits) {
			builder.append(commit.getParents()[0].getId().name()).append(" ------ ").append(commit.getId().name());
		}
		FileHelper.outputToFile(fileName, builder, false);
	}

}
