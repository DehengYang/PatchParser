package edu.lu.uni.serval.BugCommit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.lu.uni.serval.utils.FileHelper;

/*
 * This is to get all D4J bugs diff info
 * 
 */
public class BugDiff {
	public String getSrc(String proj, int id){
		String allPath = "/home/dale/ALL_APR_TOOLS/Pre-PatchParse/PatchParser-D4J/d4j-info/src_path/" + proj.toLowerCase() + "/" + id + ".txt" ;
		return FileHelper.readFile(allPath).split("\n")[0]; // srcPath
	}
	
	public String getShellDiff(String proj, int id) throws IOException{
		String repoBuggy = "/home/dale/ALL_APR_TOOLS/d4j-repo/";
		String repoFixed = "/home/dale/ALL_APR_TOOLS/d4j-repo/fixed_bugs_dir/";
		String[] cmd = {"/bin/sh","-c", "cd " + repoBuggy 
				+ " && " + "/bin/bash single-download.sh "
				+ proj + " " + id + " 1"};
//		shellRun2(cmd);
		
		String[] cmd2 = {"/bin/sh","-c", "cd " + repoFixed 
				+ " && " + "/bin/bash  fixed_single_download.sh "
				+ proj + " " + id + " 1"};
//		shellRun2(cmd2);
		
		String srcPath = getSrc(proj, id);
		String buggySrcPath = repoBuggy + proj + "/" + proj + "_" + id + srcPath;
		String fixedrcPath = repoFixed + proj + "/" + proj + "_" + id + srcPath;
		
		String[] cmd3 = {"/bin/sh","-c", "cd " + repoBuggy 
				+ " && " + "/usr/bin/diff -Naur " 
				+ buggySrcPath + " " + fixedrcPath
				};
		String shellDiff = shellRun2(cmd3);
		return shellDiff;
	}
	
	public Map<Integer, List<String>> getChart() throws IOException{
		String proj = "Chart";
		Map<Integer, List<String>> diffMap = new HashMap<>();
		for(int id = 1; id <= 1; id++){
//			String diffPath = "/home/dale/env/defects4j/framework/projects/Chart/patches/" + id + ".src.patch";
//			String diffInfo = FileHelper.readFile(diffPath);
			String shellDiff = getShellDiff(proj, id);
			
			// save shell diff
			String targetPath = "/home/dale/ALL_APR_TOOLS/Pre-PatchParse/PatchParser-D4J/data/PatchCommits/Keywords/jfreechart/"
					 + proj + "/" + id + "/diffInfo.txt";
			FileHelper.outputToFile(targetPath, shellDiff, false);
			
			String[] diffLines = shellDiff.split("\n");
			List<String> diffHunkList = new ArrayList<>();
			boolean curFlag = false;
			for (String line : diffLines){
				// save buggy and fixed files
				if(line.length() > 4){
					if(line.substring(0,4).equals("--- ")){
						cpBugFix(line, "bug",proj,id);
					}
					if(line.substring(0,4).equals("+++ ")){
						cpBugFix(line, "fix",proj,id);
					}
				}
				
				if(line.length() > 1){
					// is a diff info
//					System.out.format("1:%s\n2:%s\n3:%s\n",line.substring(0,1), line.substring(1,2), line );
//					System.out.print(line.substring(0,1).equals("-"));
//					System.out.print(line.substring(0,1).equals("+"));
					if( (line.substring(0,1).equals("-") ||    //debug: change '-' to "-"
							line.substring(0,1).equals("+")) //debug: change '+' to "+"
							&& line.substring(1,2).equals(" ")){
						if(curFlag == true){
							String diff = diffHunkList.get(diffHunkList.size() - 1);
							diff = diff + line;
							diffHunkList.set(diffHunkList.size() - 1, diff.trim());
						}else{
							diffHunkList.add(line);
						}
						
						curFlag = true; // is a diff
					}else{
						curFlag = false;
					}
				}else{
					curFlag = false;
				}
			}
			
			diffMap.put(id, diffHunkList);
			System.out.print("");
		}
		return diffMap;
	}
	
	
	private void cpBugFix(String line, String flag, String proj, int id) throws IOException {
		String buggyPath = line.split(" ")[1].split("\t")[0];
		File buggyFile = new File(buggyPath);
		String fileName;
		if (flag.equals("bug")){
			fileName = "buggy-" + buggyFile.getName();
		}else{
			fileName = "fixed-" + buggyFile.getName();
		}
		
		String targetPath = "/home/dale/ALL_APR_TOOLS/Pre-PatchParse/PatchParser-D4J/data/PatchCommits/Keywords/jfreechart/"
				 + proj + "/" + id + "/" + fileName;
		///home/dale/ALL_APR_TOOLS/Pre-PatchParse/PatchParser-D4J/data/PatchCommits/Keywords/jfreechart/Chart/1/
		FileHelper.outputToFile(targetPath, "", false);
		String result = shellRun2("cp " + buggyPath + " " + targetPath);
		
	}

	// dale: simple run
	public static String shellRun2(String cmd) throws IOException {
        Process process= Runtime.getRuntime().exec(cmd);
        String results = getShellOut(process);
        return results;
	}
	// dale: simple run
	public static String shellRun2(String[] cmd) throws IOException {
		Process process= Runtime.getRuntime().exec(cmd);
		String results = getShellOut(process);
		return results;
	}
	
	private static String getShellOut(Process process) {
		ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(new ReadShellProcess(process));
        String returnString = "";
        try {
            returnString = future.get(10800L, TimeUnit.SECONDS);
        } catch (InterruptedException e){
            future.cancel(true);
            e.printStackTrace();
            shutdownProcess(service, process);
            return "";
        } catch (TimeoutException e){
            future.cancel(true);
            e.printStackTrace();
            shutdownProcess(service, process);
            return "";
        } catch (ExecutionException e){
            future.cancel(true);
            e.printStackTrace();
            shutdownProcess(service, process);
            return "";
        } finally {
            shutdownProcess(service, process);
        }
        return returnString;
	}
	
	private static void shutdownProcess(ExecutorService service, Process process) {
		service.shutdownNow();
        try {
			process.getErrorStream().close();
			process.getInputStream().close();
	        process.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        process.destroy();
	}
}

class ReadShellProcess implements Callable<String> {
    public Process process;

    public ReadShellProcess(Process p) {
        this.process = p;
    }

    public synchronized String call() {
        StringBuilder sb = new StringBuilder();
        BufferedInputStream in = null;
        BufferedReader br = null;
        try {
            String s;
            in = new BufferedInputStream(process.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            while ((s = br.readLine()) != null && s.length()!=0) {
                if (sb.length() < 1000000){
                    if (Thread.interrupted()){
                        return sb.toString();
                    }
                    sb.append(System.getProperty("line.separator"));
                    sb.append(s);
                }
            }
            in = new BufferedInputStream(process.getErrorStream());
            br = new BufferedReader(new InputStreamReader(in));
            while ((s = br.readLine()) != null && s.length()!=0) {
                if (Thread.interrupted()){
                    return sb.toString();
                }
                if (sb.length() < 1000000){
                    sb.append(System.getProperty("line.separator"));
                    sb.append(s);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e){
                }
            }
            if (in != null){
                try {
                    in.close();
                } catch (IOException e){
                }
            }
            process.destroy();
        }
        return sb.toString();
    }
}