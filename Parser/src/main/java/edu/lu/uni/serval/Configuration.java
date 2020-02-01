package edu.lu.uni.serval;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
	// dale : parameters.
	public static String D4J_REPO = "/home/dale/ALL_APR_TOOLS/d4j-repo/";
	public static String commitDB = "/home/dale/env/defects4j/framework/projects/";
	public static String PROJ_BUG = "Chart";//"Chart";
	public static String PROJECT = "jfreechart";//"jfreechart";
	public static String ID = "19";
	public static boolean DELETE_PatchCommitsDir = true;
	public static boolean PRINT_ALLCOMMIT = false;
	public static String CIIPath;
	public static String CIIPurePath;
	public static String USER_NAME = "deheng";
	public static Map<String, Integer> numOfBugs = new HashMap<>();
	static{
		numOfBugs.put("Chart", 26);
		numOfBugs.put("Closure", 133); //176  //TODO
		numOfBugs.put("Lang", 65);
		numOfBugs.put("Math", 106);
		numOfBugs.put("Time", 27);
		numOfBugs.put("Mockito", 38);
	}
	public static int commitIdLength = 8;
	public static Map<String, Integer> commitNoMap = new HashMap<>();  // commitId number
	public static Map<String, Integer> commitExecutedNoMap = new HashMap<>();  // commitId number
	public static Date commitTime = new Date(0); // bug fix.   add 0 as a parameter
		
		
	public static final String BUG_REPORT_URL = "https://issues.apache.org/jira/browse/";
	public static String SUBJECTS_PATH = "../subjects/";  //dale
	private static final String OUTPUT_PATH = "../data/";
	public static final String BUG_REPORTS_PATH = OUTPUT_PATH + "BugReports/";
	public static final String PATCH_COMMITS_PATH = OUTPUT_PATH + "PatchCommits/";
	public static final String DIFFENTRY_SIZE_PATH = OUTPUT_PATH + "DiffentrySizes/";
	public static final String PARSE_RESULTS_PATH = OUTPUT_PATH + "ParseResults/";
	
	//dale
	public static final String AllCCI = OUTPUT_PATH + "AllCCI/";
	
	public static final long TIMEOUT_THRESHOLD = 108000L; //1800L; dale debug
	
	// dale: get current dir path
	// /home/dale/ALL_APR_TOOLS/Pre-PatchParse/PatchParser-D4J/Parser
	public static final String HOME = System.getProperty("user.dir") + "/";
	public static final String BUGS = OUTPUT_PATH + "bugs/";
	
	public static Map<String, Integer> numOfWorkers = new HashMap<>();
	public static Map<String, Integer> sizeThreshold = new HashMap<>();
	
	static {
		numOfWorkers.put("commons-io", 1);
		numOfWorkers.put("commons-lang", 1);
		numOfWorkers.put("commons-math", 10);
		numOfWorkers.put("derby", 20);
		numOfWorkers.put("lucene-solr", 50);
		numOfWorkers.put("mahout", 10);
		
		sizeThreshold.put("buggy hunk", 8);
		sizeThreshold.put("fixed hunk", 10);
	}
}
