package edu.lu.uni.serval.BugCommit.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import edu.lu.uni.serval.Configuration;

/**
 * Parse all patches together.
 * 
 * @author kui.liu
 *
 */
public class MultipleThreadsPatchesParser1 {
	
	private static Logger log = LoggerFactory.getLogger(MultipleThreadsPatchesParser1.class);

	@SuppressWarnings("deprecation")
	public void parse(String patchPath, String outputPath) {
		int bugHunkSize = Configuration.sizeThreshold.get("buggy hunk");
		int fixHunkSize = Configuration.sizeThreshold.get("fixed hunk");
		// dale comment
//		List<MessageFile> msgFiles = readMessageFiles(patchPath, "Linked");
//		msgFiles.addAll(readMessageFiles(patchPath, "Keywords"));
		List<MessageFile> msgFiles = readMessageFiles2(patchPath, "Keywords");
		// dale add
		
		ActorSystem system = null;
		ActorRef parsingActor = null;
		// dale 1
		int numberOfWorkers = 1; //00;
		final WorkMessage msg = new WorkMessage(0, msgFiles);
		try {
//			log.info("Parsing begins...");
			system = ActorSystem.create("Parsing-Patches-System");
			parsingActor = system.actorOf(ParsePatchActor.props(numberOfWorkers, outputPath, bugHunkSize, fixHunkSize), "patch-parser-actor");
			parsingActor.tell(msg, ActorRef.noSender());
		} catch (Exception e) {
			system.shutdown();
			e.printStackTrace();
		}
	}

	// dale : read Chart buggy and fixed files
	// ../data/PatchCommits/Keywords/jfreechart   Chart/1/
	private List<MessageFile> readMessageFiles2(String path, String dataType) {
		List<MessageFile> msgFiles = new ArrayList<>();
		File[] projects = new File(path + dataType).listFiles();
		for (File project : projects) {
			if (project.isDirectory()) {
				String projId = "/Chart/1/";
				String projectPath = project.getPath();
				File revFilesPath = new File(projectPath + projId);
				File[] revFiles = revFilesPath.listFiles();   // project folders
				
				for (File revFile : revFiles) {
					if (revFile.getName().startsWith("fixed-")) {
						String fileName = revFile.getName();
						File prevFile = new File(projectPath + projId + fileName.replace("fixed-", "buggy-"));// previous file
						File diffentryFile = new File(projectPath + projId + "diffInfo.txt"); // DiffEntry file
						MessageFile msgFile = new MessageFile(revFile, prevFile, diffentryFile);
						msgFiles.add(msgFile);
					}
				}
			}
		}
		return msgFiles;
	}
	
	private List<MessageFile> readMessageFiles(String path, String dataType) {
		List<MessageFile> msgFiles = new ArrayList<>();
		File[] projects = new File(path + dataType).listFiles();
		for (File project : projects) {
			if (project.isDirectory()) {
				String projectPath = project.getPath();
				File revFilesPath = new File(projectPath + "/revFiles/");
				File[] revFiles = revFilesPath.listFiles();   // project folders
				
				for (File revFile : revFiles) {
					if (revFile.getName().endsWith(".java")) {
						String fileName = revFile.getName();
						File prevFile = new File(projectPath + "/prevFiles/prev_" + fileName);// previous file
						fileName = fileName.replace(".java", ".txt");
						File diffentryFile = new File(projectPath + "/DiffEntries/" + fileName); // DiffEntry file
						MessageFile msgFile = new MessageFile(revFile, prevFile, diffentryFile);
						msgFiles.add(msgFile);
					}
				}
			}
		}
		return msgFiles;
	}
}
