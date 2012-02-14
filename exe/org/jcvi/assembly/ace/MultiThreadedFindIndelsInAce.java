package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationWriter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class MultiThreadedFindIndelsInAce {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws DataStoreException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DataStoreException, InterruptedException {
		Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output file (if not used output printed to STDOUT)")
        .longName("out")
        .build());
        
        options.addOption(new CommandLineOptionBuilder("nav", "path to optional consed navigation file to see abacus errors easier in consed")
        .build());
        options.addOption(new CommandLineOptionBuilder("num_threads", "number of threads to run simultaneously.  Each contig is run in a different thread (required)")
        .isRequired(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("min_var_length", "length of the minimum variant GAPPED read length to consider.  This has no bearing " +
        		"on the final consensus length since an assembly could have gaps at those consensus positions.  (default = "+DetectIndelsInAceContigWorker.MIN_INDEL_LENGTH + ")")
        .build());
        options.addOption(new CommandLineOptionBuilder("min_var_percent", 
        		String.format("minimum percent coverage of variants vs consensus called in the slice to be considered." +
        				" (default = %.2f)",DetectIndelsInAceContigWorker.MIN_VARIANT_PERCENT))
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());     
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            int numberOfThreads = Integer.parseInt(commandLine.getOptionValue("num_threads"));
            final File inputAceFile = new File(commandLine.getOptionValue("a"));
            File finalNavigationFile = new File(commandLine.getOptionValue("nav"));
            IOUtil.mkdirs(finalNavigationFile.getParentFile());
            boolean writeToOutFile = commandLine.hasOption("o");
            final File finalOutFile;
            if(writeToOutFile){
            	finalOutFile = new File(commandLine.getOptionValue("o"));
            	IOUtil.mkdirs(finalOutFile.getParentFile());
            }else{
            	finalOutFile =null;
            }
            Float minVarPercent = commandLine.hasOption("min_var_percent")?
            		 	Float.parseFloat(commandLine.getOptionValue("min_var_percent")) : null;
		 	
		 	Float minVarLength = commandLine.hasOption("min_var_length")?
		 			Float.parseFloat(commandLine.getOptionValue("min_var_length")) : null;	 	
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            AceContigDataStore datastore = IndexedAceFileDataStore.create(inputAceFile);
            
            Iterator<String> ids = datastore.getIds();
            List<Future<Void>> futures = new ArrayList<Future<Void>>();
            
            
            while(ids.hasNext()){
            	final String id = ids.next();
            	final List<String> workerArgs = new ArrayList<String>();
            	
            	final File tmpNavFile = generateTempNavFileNameFor(finalNavigationFile, id);
            	
            	workerArgs.add("-a");
        		workerArgs.add(inputAceFile.getAbsolutePath());
        		
        		workerArgs.add("-c");
        		workerArgs.add(id);
        		
        		workerArgs.add("-nav");
        		workerArgs.add(tmpNavFile.getAbsolutePath());
            	if(writeToOutFile){
            		final File tmpOutFile = generateTempOutFileNameFor(finalOutFile, id);
            		workerArgs.add("-o");
            		workerArgs.add(tmpOutFile.getAbsolutePath());
            	}
            	if(minVarPercent!=null){
            		workerArgs.add("-min_var_percent");
            		workerArgs.add(String.format("%.2f",minVarPercent));
            	}
            	if(minVarLength!=null){
            		workerArgs.add("-min_var_length");
            		workerArgs.add(String.format("%.2f",minVarLength));
            	}
            	futures.add(executor.submit(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						DetectIndelsInAceContigWorker.main(workerArgs.toArray(new String[0]));
						return null;
					}
	
            		
            	}));
            }            
            executor.shutdown();
            boolean success=true;
            for(Future<Void> future : futures){
                try{
                	future.get();
                }catch(ExecutionException e){
                    success=false;
                    e.printStackTrace();
                    executor.shutdownNow();
                }catch(Exception e){
                    success=false;
                    e.printStackTrace();
                }
            }
            executor.awaitTermination(5, TimeUnit.SECONDS);
            if(!success){
                System.err.println("failed to complete reabacus process check error logs for details");                
            }else{
                CloseableIterator<String> contigIdIter = datastore.getIds();
                OutputStream navOut = new FileOutputStream(finalNavigationFile);
                ConsedNavigationWriter.create("indel errors for "+inputAceFile.getName(), navOut);
                final OutputStream stdOut;
                if(writeToOutFile){
                	stdOut= new FileOutputStream(finalOutFile);
                }else{
                	stdOut=null;
                }
                try{
                while(contigIdIter.hasNext()){
                    String contigId = contigIdIter.next();
                    File tempNavFile = generateTempNavFileNameFor(finalNavigationFile, contigId);
                    copyThenDelete(tempNavFile, navOut);
                    if(writeToOutFile){
	                    File tempOutFile = generateTempOutFileNameFor(finalOutFile, contigId);
	                    copyThenDelete(tempOutFile, stdOut);
                    }
                    
                }

                }finally{
                	IOUtil.closeAndIgnoreErrors(navOut,stdOut);
                }
            }
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
	}

	private static void copyThenDelete(File from, OutputStream to)
			throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(from);
		IOUtils.copy(in, to);
		IOUtil.closeAndIgnoreErrors(in);
		IOUtil.deleteIgnoreError(from);
	}
	
	private static File generateTempOutFileNameFor(File finalOutFile, String contigId) {
		String newName = String.format("%s.contig.%s", finalOutFile.getName(), contigId);
		return new File(finalOutFile.getParentFile(), newName);
	}

	private static File generateTempNavFileNameFor(File finalNavFile, String contigId){
		String newName = String.format("%s.contig.%s", finalNavFile.getName(), contigId);
		return new File(finalNavFile.getParentFile(), newName);
	}
	/**
     * @param options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "multiThreadedFindIndelsInAce -a <ace file> -nav <out nav file> -num_threads <num>", 
                
                "Parse an ace file and find indel errors in the contigs and write the locations to a nav file.",
                options,
               "Created by Danny Katzel"
                  );
        
    }
}
