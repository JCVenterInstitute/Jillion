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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.Command;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.util.ExceptionIntolerantFixedSizedThreadPoolExecutor;
import org.joda.time.Period;


/**
 * @author dkatzel
 *
 *
 */
public class DefaultMultithreadedCasAssemblyBuilder extends AbstractExecutorCasAssemblyBuilder<Void>{
    private static final String DEFAULT_PREFIX = "cas2consed";
    private static final int DEFAULT_CACHE_SIZE = 1000;
    
    /**
     * @param casFile
     * @param numberOfContigsToConvertAtSameTime
     */
    public DefaultMultithreadedCasAssemblyBuilder(File casFile,
            int numberOfContigsToConvertAtSameTime) {
        super(casFile, numberOfContigsToConvertAtSameTime);
    }

    @Override
    protected ExecutorService createExecutorService(int numberOfContigsToConvertAtSameTime) {
        return new ExceptionIntolerantFixedSizedThreadPoolExecutor(numberOfContigsToConvertAtSameTime);
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
        options.addOption(new CommandLineOptionBuilder("num_cores", "number of cores to use when converting")
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
       Integer numCores = Integer.parseInt(commandLine.getOptionValue("num_cores"));
        AbstractMultiThreadedCasAssemblyBuilder builder = new DefaultMultithreadedCasAssemblyBuilder(casFile,numCores);
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
        formatter.printHelp( "multithreadedCas2Consed -cas <cas file> -o <output dir> [-prefix <prefix> -s <cache_size>]", 
                
                "convert a clc .cas assembly file into a consed package which runs on multiple cores on the same machine",
                options,
                "Created by Danny Katzel");
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected Callable createSingleAssemblyCasConversionCallable(final Command command) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SingleContigCasAssemblyBuilder.main(command.getArguments().toArray(new String[0]));
                //must return null to satisfy Void return
                return null;
            }
        };
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void jobFinished(Void returnedValue) {
        // no-op
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void cleanup() {
        // no-op
        
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
