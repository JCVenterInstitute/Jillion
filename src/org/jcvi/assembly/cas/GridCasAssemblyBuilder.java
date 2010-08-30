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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.jcvi.command.Command;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.command.grid.BatchGridJobImpl;
import org.jcvi.command.grid.GridJob;
import org.jcvi.command.grid.GridJobExecutorService;
import org.jcvi.command.grid.GridUtils;
import org.jcvi.command.grid.PostExecutionHook;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.joda.time.Period;

/**
 * @author dkatzel
 *
 *
 */
public class GridCasAssemblyBuilder extends AbstractExecutorCasAssemblyBuilder<Integer>{
    private static final int DEFAULT_CACHE_SIZE = 1000;
    
    private final String projectCode;
    private final File singleContigCasAssemblyExecutable;
    private final Session gridSession;
    /**
     * @param casFile
     * @param numberOfContigsToConvertAtSameTime
     */
    public GridCasAssemblyBuilder(File casFile,
            int numberOfContigsToConvertAtSameTime, File singleContigCasAssemblyExecutable,String projectCode) {
        
       this(GridUtils.buildNewSession(), casFile, numberOfContigsToConvertAtSameTime, singleContigCasAssemblyExecutable,projectCode);
    }
    public GridCasAssemblyBuilder(Session session,File casFile,
            int numberOfContigsToConvertAtSameTime, File singleContigCasAssemblyExecutable,String projectCode) {
        
        super(casFile, numberOfContigsToConvertAtSameTime);
        this.gridSession = session;
        this.projectCode = projectCode;
        this.singleContigCasAssemblyExecutable = singleContigCasAssemblyExecutable;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    protected ExecutorService createExecutorService(
            int numberOfContigsToConvertAtSameTime) {
        return new GridJobExecutorService(gridSession,"gridCas2Consed",numberOfContigsToConvertAtSameTime);
    }

    @Override
    protected GridJob createSingleAssemblyCasConversionCallable(
            Command aCommand) {
        Command command = new Command(singleContigCasAssemblyExecutable);
        for(Entry<String, String> entry :aCommand.getOpt().entrySet()){
            command.setOption(entry.getKey(), entry.getValue());
        }
        for(String flag :aCommand.getFlags()){
            command.addFlag(flag);
        }
        for(String target :aCommand.getTargets()){
            command.addTarget(target);
        }
        GridJob job = new BatchGridJobImpl.Builder(GridUtils.getGlobalSession(), command, projectCode)
                        .postExecutionHook(new PostExecutionHook() {
                            
                            @Override
                            public int execute(Map<String, JobInfo> jobInfoMap) throws Exception {
                                for(JobInfo jobInfo : jobInfoMap.values()){
                                    if(jobInfo.hasExited() && jobInfo.getExitStatus() !=0){
                                        //errored out? 
                                        //cancel everything?
                                        System.out.println("job " + jobInfo.getJobId() +" has errored out" + jobInfo.getExitStatus());
                                        GridCasAssemblyBuilder.this.getExecutor().shutdownNow();
                                    }
                                }
                                return 0;
                            }
                        })
                        .build();
        
        return job;
    }

    @Override
    protected void jobFinished(Integer returnedValue) {
        // TODO Auto-generated method stub
        
    }
 public static void main(String[] args) throws IOException{
        
        
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("cas", "cas file")
                            .isRequired(true)
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("o", "output directory")
                            .longName("outputDir")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("prefix", "file prefix for all generated files ( default "+DEFAULT_PREFIX +" )")                                
                                .build());
        options.addOption(new CommandLineOptionBuilder("tempDir", "temp directory")
                                .build());
        options.addOption(new CommandLineOptionBuilder("num_jobs", "number of jobs  to launch at a time when converting")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("project_code", "grid project code")
                                .isRequired(true)
                                .build());
        options.addOption(new CommandLineOptionBuilder("trim", "trim file in sfffile's tab delimmed trim format")                                
                                                        .build());
        options.addOption(new CommandLineOptionBuilder("trimMap", "trim map file containing tab delimited trimmed fastX file to untrimmed counterpart")                                
                                    .build());
        options.addOption(new CommandLineOptionBuilder("chromat_dir", "directory of chromatograms to be converted into phd "+
                "(it is assumed the read data for these chromatograms are in a fasta file which the .cas file knows about")                                
                        .build());
        options.addOption(new CommandLineOptionBuilder("s", "cache size ( default "+DEFAULT_CACHE_SIZE +" )")  
                                .longName("cache_size")
                                        .build());
        options.addOption(new CommandLineOptionBuilder("coverage_trim", "perform additional contig ends trimming based on coverage.  The value of coverage_trim is the min level coverage required at ends.")                                
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("useIllumina", "any FASTQ files in this assembly are encoded in Illumina 1.3+ format (default is Sanger)")                                
                            .isFlag(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("useClosureTrimming", "apply additional contig trimming based on JCVI Closure rules")                                
                                                .isFlag(true)
                                                .build());
        CommandLine commandLine;
        try {
            commandLine = CommandLineUtils.parseCommandLine(options, args);
            
       File casFile = new File(commandLine.getOptionValue("cas"));
       Integer numCores = Integer.parseInt(commandLine.getOptionValue("num_jobs"));
        AbstractMultiThreadedCasAssemblyBuilder builder = new GridCasAssemblyBuilder(casFile,numCores, 
                new File("/usr/local/devel/DAS/software/JavaCommon2/singleContigCasAssemblyConverter.pl"),
                commandLine.getOptionValue("project_code"));
        builder.commandLine(commandLine);
        
        if(!commandLine.hasOption("tempDir")){
            //default to scratch
            builder.tempDir(Cas2Consed.DEFAULT_TEMP_DIR);
        }else{
            File t =new File(commandLine.getOptionValue("tempDir"));
            t.mkdirs();
            ReadWriteDirectoryFileServer tempDir =DirectoryFileServer.createTemporaryDirectoryFileServer(t);
          System.out.println(tempDir.getRootDir());
            //  t.mkdirs();
            builder.tempDir(tempDir.getRootDir());
        }
      
        long start =System.currentTimeMillis();
        builder.build();
        long end =System.currentTimeMillis();
        
        System.out.println(new Period(end-start));
    
    }catch(ParseException e){
        printHelp(options);
        System.exit(1);
    }
    
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "gridCas2Consed -cas <cas file> -o <output dir> [-prefix <prefix> -s <cache_size>]", 
                
                "convert a clc .cas assembly file into a consed package which will launch grid jobs," +
                "one per contig to convert.  The number of jobs running at the same time" +
                "can be controlled with the num_jobs option",
                options,
                "Created by Danny Katzel");
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void cleanup() {
        try {
            gridSession.exit();
        } catch (DrmaaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    /**
     * {@inheritDoc}
     */
     @Override
     protected void handleException(Exception e) {
         e.printStackTrace();
         getExecutor().shutdownNow();
         
     }
   

}
