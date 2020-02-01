package edu.lu.uni.serval;

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import edu.lu.uni.serval.BugCommit.Distribution;
import edu.lu.uni.serval.BugCommit.parser.MultipleThreadsPatchesParser1;
import edu.lu.uni.serval.utils.FileHelper;

// modified by apr
public class Main2 {

	public static void main(String[] args) throws IOException, ParseException  {
		// parse parameters
		setParameters(args);
		Configuration.commitNoMap.clear();
		Configuration.commitExecutedNoMap.clear();
		
		// delete first
		Configuration.CCIPath = Configuration.PARSE_RESULTS_PATH + "CCI/" + Configuration.PROJ_BUG + "/" + Configuration.ID + "/";
		Configuration.CCIPurePath = Configuration.PARSE_RESULTS_PATH + "CCI-pure/" + Configuration.PROJ_BUG + "/" + Configuration.ID + "/";
		FileHelper.deleteDirectory(Configuration.CCIPath);
		FileHelper.deleteDirectory(Configuration.CCIPurePath);
		
//		System.out.println("\n\n\n======================================================================================");
//		System.out.println("Statistics of diff hunk sizes of code changes.");
//		System.out.println("======================================================================================");
		new Distribution().statistics(Configuration.PATCH_COMMITS_PATH, Configuration.DIFFENTRY_SIZE_PATH);
		
//		System.out.println("\n\n\n======================================================================================");
//		System.out.println("Parse code changes of patches.");
//		System.out.println("======================================================================================");
		new MultipleThreadsPatchesParser1().parse(Configuration.PATCH_COMMITS_PATH, Configuration.PARSE_RESULTS_PATH);
//		new MultipleThreadsPatchesParser2().parse(Configuration.PATCH_COMMITS_PATH, Configuration.PARSE_RESULTS_PATH);
	}

	private static void setParameters(String[] args) {
//		public static String D4J_REPO = "/home/dale/ALL_APR_TOOLS/d4j-repo/";
//		public static String PROJ_BUG = "Chart";
//		public static String PROJECT = "jfreechart";
//		public static String ID = "2";
//		public static boolean DELETE_COMMITS = false;
        Option opt1 = new Option("d4j","D4J_REPO",true,"e.g., /home/dale/ALL_APR_TOOLS/d4j-repo/");
        opt1.setRequired(false);
        Option opt2 = new Option("bugProj","PROJ_BUG",true,"e.g., Chart");
        opt2.setRequired(false);   
        Option opt3 = new Option("oriProj","PROJECT",true,"e.g., jfreechart");
        opt3.setRequired(false);
        Option opt4 = new Option("id","ID",true,"e.g., 2");
        opt4.setRequired(false);

        Options options = new Options();
        options.addOption(opt1);
        options.addOption(opt2);
        options.addOption(opt3);
        options.addOption(opt4);

        CommandLine cli = null;
        CommandLineParser cliParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        try {
            cli = cliParser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            helpFormatter.printHelp(">>>>>> test cli options", options);
            e.printStackTrace();
        } 

        if (cli.hasOption("d4j")){
        	Configuration.D4J_REPO = cli.getOptionValue("d4j");
        }
        if(cli.hasOption("bugProj")){
        	Configuration.PROJ_BUG = cli.getOptionValue("bugProj");
        }
        if(cli.hasOption("oriProj")){
        	Configuration.PROJECT = cli.getOptionValue("oriProj");
        }
        if(cli.hasOption("id")){
        	Configuration.ID = cli.getOptionValue("id");
        }
        
        System.out.format("Proj: %s, ID: %s", Configuration.PROJ_BUG, Configuration.ID);
        
        // set project dir
    }
}
