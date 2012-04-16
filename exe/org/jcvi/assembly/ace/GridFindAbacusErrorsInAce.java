/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.command.grid.GridJobBuilder;
import org.jcvi.common.command.grid.GridJobBuilders;
import org.jcvi.common.command.grid.GridJobExecutorService;
import org.jcvi.common.command.grid.PostExecutionHook;
import org.jcvi.common.command.grid.SimpleGridJob;
import org.jcvi.common.command.grid.GridJob.MemoryUnit;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationWriter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultExcludeDataStoreFilter;
import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

/**
 * @author dkatzel
 *
 *
 */
public class GridFindAbacusErrorsInAce {
    private static final int DEFAULT_MAX_JOBS = 300;
    private static final File ABACUS_WORKER_EXE;
    
    static{
        try {
            ABACUS_WORKER_EXE= getPathToAbacusWorker();
        } catch (IOException e) {
           throw new RuntimeException("error reading config file",e);
        }
    }
    /**
     * @param args
     * @throws DrmaaException 
     * @throws IOException 
     * @throws DataStoreException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws DrmaaException, IOException, DataStoreException, InterruptedException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        
        options.addOption(new CommandLineOptionBuilder("nav", "path to consed navigation file to see abacus errors easier in consed (required)")
        .isRequired(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("cwd", "path to what the working directory for this program should be.  This is where " +
        		"all the grid STDERR and STDOUT files will go.  If not set" +
        		" defaults to current working directory")
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        
        options.addOption(new CommandLineOptionBuilder("P", "grid project code (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("max_submitted_jobs", "max number of jobs that are SUBMITTED to the grid " +
        		"at any time.  If there are more jobs than this number to submit, then this program will queue them internally " +
        		"and wait for the currently submitted jobs to finish.  NOTE: it is still up to the grid engine to schedule the jobs that have " +
        		"been submitted.  If this option isn't set, then the default value is used : " + DEFAULT_MAX_JOBS)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("percent", "percentage expressed as a decimal 0 - 1 of the percentage of gaps vs non-gap " +
                "characters per read in the region to be considered an abacus error default = "+ DetectAbacusErrorsContigWorker.DEFAULT_GAP_PERCENTAGE)
        .build());
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        SessionFactory factory = SessionFactory.getFactory();
        final Session session = factory.getSession();
        session.init("");
        GridJobExecutorService executor=null;
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            String projectCode = commandLine.getOptionValue("P");
            boolean wantsNav = commandLine.hasOption("nav");
            File navFile=null;
            File workDir = commandLine.hasOption("cwd")?
                    new File(commandLine.getOptionValue("cwd")) : new File(".");
           
            if(wantsNav){
                navFile = new File(commandLine.getOptionValue("nav"));
                IOUtil.deleteIgnoreError(navFile);
                if(!navFile.createNewFile()){
                    throw new IOException("error creating file; already exists and cannot delete"+ navFile.getAbsolutePath());
                }
               // workDir = navFile.getCanonicalFile().getParentFile();
            }
            final String userDefinedPercentage = commandLine.hasOption("percent")?commandLine.getOptionValue("percent") : null;
            int maxJobs = commandLine.hasOption("max_submitted_jobs")?
                    Integer.parseInt(commandLine.getOptionValue("max_submitted_jobs"))
                    : DEFAULT_MAX_JOBS;
            executor = new GridJobExecutorService(session,"abacusErrorDetector", maxJobs);
            final GridJobExecutorService builtExecutor = executor;
            List<SimpleGridJob> jobs = new ArrayList<SimpleGridJob>();
            File aceFile = new File(commandLine.getOptionValue("a"));
            final DataStoreFilter filter = getDataStoreFilter(commandLine);
            AceContigDataStore datastore = IndexedAceFileDataStore.create(aceFile);
            CloseableIterator<String> idIter = datastore.idIterator();
            Set<File> files = new HashSet<File>();
            try{
            while(idIter.hasNext()){
                final String contigId = idIter.next();
                if(filter.accept(contigId)){
                    Command findAbacusErrorWorker = new Command(ABACUS_WORKER_EXE);
                    findAbacusErrorWorker.setOption("-ace", aceFile.getAbsolutePath());
                    findAbacusErrorWorker.setOption("-c", contigId);
                    if(wantsNav){
                        File temp = new File(navFile.getParentFile(),navFile.getName()+".ctg."+contigId);
                        
                        findAbacusErrorWorker.setOption("-nav", temp.getAbsolutePath());
                        files.add(temp);
                    }
                    if(userDefinedPercentage !=null){
                        findAbacusErrorWorker.setOption("-percent", userDefinedPercentage);
                    }
                    findAbacusErrorWorker.setWorkingDir(workDir);
                    GridJobBuilder<SimpleGridJob> job = GridJobBuilders.createSimpleGridJobBuilder(
                                                        session,
                                                        findAbacusErrorWorker, 
                                                        projectCode);
                    job.postExecutionHook(new PostExecutionHook() {
                        
                        @Override
                        public int execute(Map<String, JobInfo> jobInfoMap) throws Exception {
                            for(Entry<String, JobInfo> entry : jobInfoMap.entrySet()){
                                JobInfo info = entry.getValue();
                                if(info.hasSignaled()){
                                    System.err.printf("grid job %s for contig id %d was cancelled%n",
                                            entry.getKey(),contigId);
                                    return 1;
                                }
                                if (info.hasExited() && info.getExitStatus() != 0){
                                    System.err.printf("grid job %s for contig id %d FAILED%n",
                                    entry.getKey(),contigId);
                                    builtExecutor.shutdownNow();
                                    return 1;
                                }
                                    System.out.printf("grid job %s for contig id  %s finished%n", 
                                            entry.getKey(), contigId);
                                
                            }
                            return 0;
                        }
                    });
                    job.setWorkingDirectory(workDir);
                    job.setMemory(16, MemoryUnit.GB);
                    job.setWorkingDirectory(navFile.getParentFile());
                    jobs.add(job.build());
                }             
            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(idIter);
            }
           for(Future<?> future : executor.invokeAll(jobs)){
               try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
           }
            executor.shutdown();
            OutputStream out = new FileOutputStream(navFile);
            PrintWriter navWriter= new PrintWriter(out);
            ConsedNavigationWriter.create("Abacus errors for " + aceFile.getName(), out);
            for(File partialNav : files){
                System.out.println("reading "+ partialNav.getName());
                Scanner scanner = new Scanner(partialNav);
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    System.out.println(line);
                    navWriter.println(line);
                }
                scanner.close();                    
            }
            navWriter.close();
            for(File partialNav : files){
                IOUtil.deleteIgnoreError(partialNav);
            }
            
           
        } catch (ParseException e) {
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }finally{
            session.exit();
        }
    }
    private static DataStoreFilter getDataStoreFilter(CommandLine commandLine)
                                                        throws IdReaderException {
        final DataStoreFilter filter;
        File idFile;
        if(commandLine.hasOption("i")){
            idFile =new File(commandLine.getOptionValue("i"));
            Set<String> includeList=parseIdsFrom(idFile);
            if(commandLine.hasOption("e")){
                Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                includeList.removeAll(excludeList);
            }
            filter = new DefaultIncludeDataStoreFilter(includeList);
            
        }else if(commandLine.hasOption("e")){
            idFile =new File(commandLine.getOptionValue("e"));
            filter = new DefaultExcludeDataStoreFilter(parseIdsFrom(idFile));
        }else{
            filter = AcceptingDataStoreFilter.INSTANCE;
        }
        return filter;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "gridFindAbacusErrorsInAceFile -a <ace file>", 
                
                "Parse an ace file and write out ungapped consensus coordinates of abacus assembly errors",
                options,
               "Created by Danny Katzel"
                  );
    }
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
    
    private static final File getPathToAbacusWorker() throws IOException{
        InputStream in = GridReAbacusAce.class.getResourceAsStream("/javacommon.config");
        Properties props =new Properties();
        try{
            props.load(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        if(!props.containsKey("detect_abacus_worker")){
            throw new IllegalStateException("could not read property 'detect_abacus_worker'");
        }
        return new File(props.get("detect_abacus_worker").toString());
     }
}
