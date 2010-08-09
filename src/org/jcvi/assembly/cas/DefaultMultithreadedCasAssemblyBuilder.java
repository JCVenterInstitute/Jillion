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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.util.ExceptionIntolerantFixedSizedThreadPoolExecutor;
import org.joda.time.Period;


/**
 * @author dkatzel
 *
 *
 */
public class DefaultMultithreadedCasAssemblyBuilder extends AbstractMultiThreadedCasAssemblyBuilder{
    private static final String DEFAULT_PREFIX = "cas2consed";
    private static final int DEFAULT_CACHE_SIZE = 1000;
    private final ExecutorService executor;
    private final List<Callable<Void>> submissions = new ArrayList<Callable<Void>>();
    
    public DefaultMultithreadedCasAssemblyBuilder(File casFile,int numberOfContigsToConvertAtSameTime){
        super(casFile);
        this.executor = new ExceptionIntolerantFixedSizedThreadPoolExecutor(numberOfContigsToConvertAtSameTime);
    }
    /**
     * {@inheritDoc}
     */
     @Override
     protected void submitSingleCasAssemblyConversion(final List<String> args)
             throws IOException {
         submissions.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SingleContigCasAssemblyBuilder.main(args.toArray(new String[0]));
                //must return null to satisfy Void return
                return null;
            }
        });
         
     }

     @Override
     protected void waitForAllAssembliesToFinish() throws Exception {
         List<Future<Void>> futures = executor.invokeAll(submissions);
         for(Future<Void> future: futures){
             future.get();
         }
         submissions.clear();
         executor.shutdown();
     }
    public static void main(String[] args) throws ParseException{
        
        
        
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
            builder.tempDir(t);
        }
      
        long start =System.currentTimeMillis();
        builder.build();
        long end =System.currentTimeMillis();
        
        System.out.println(new Period(end-start));
    
    }catch(ParseException e){
        throw e;
    }
    
    }

   
}
