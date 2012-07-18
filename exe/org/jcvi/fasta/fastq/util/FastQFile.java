/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.fasta.fastq.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.ExcludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.fastq.FastqUtil;
import org.jcvi.common.core.util.JoinedStringBuilder;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.FirstWordStringIdParser;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
/**
 * {@code FastQFile} is meant to be a FastQ version of 454's fnafile
 * which takes in a file of reads and filter options
 * and writes out a new file filtering the reads as specified.
 * @author dkatzel
 *
 *
 */
public class FastQFile {
    
    private static final String DEFAULT_FILE_OUTPUT = "Reads.fastq";
   
    /**
     * @param args
     * @throws IdReaderException 
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws IdReaderException, IOException {
        File fastQFile = new File(args[args.length-1]);
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("o", 
                String.format("output file to write (default :%s)",DEFAULT_FILE_OUTPUT ))
                        .longName("output")
                        .build());
        OptionGroup group = new OptionGroup();
        group.setRequired(true);
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOption(new CommandLineOptionBuilder("n", "number of ids in file (optional optimization)")
        
        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOptionGroup(group);
        OutputStream out=null;
        if(args.length ==1 && args[0].endsWith("-h")){
            printHelp(options);
            System.exit(0);
        }
        try {
            String[] argumentsOnly = Arrays.copyOf(args, args.length-1);
			CommandLine commandLine = CommandLineUtils.parseCommandLine(options, 
                    argumentsOnly);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            if(commandLine.hasOption("o")){
                out = new FileOutputStream(commandLine.getOptionValue("o"));
            }else{
                out = new FileOutputStream(DEFAULT_FILE_OUTPUT);
            }
            final File idFile;
            final FastXFilter filter;
            Integer numberOfIds =commandLine.hasOption("n")?Integer.parseInt(commandLine.getOptionValue("n")):null;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile,numberOfIds);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(
                            new File(commandLine.getOptionValue("e")),numberOfIds);
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else{
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile,numberOfIds));
            }
            //don't use fastq parser
            //manually read the file for speed reasons
            Scanner scanner = new Scanner(fastQFile);
            while(scanner.hasNext()){
                String defLine= scanner.nextLine();
                Matcher matcher = FastqUtil.SEQ_DEFLINE_PATTERN.matcher(defLine);
                if(!matcher.find()){
                    throw new IllegalStateException("invalid fastq file, could not parse id from "+defLine);
                }
                String id = matcher.group(1);
                String basecalls = scanner.nextLine();
                String qualDefLine = scanner.nextLine();
                String qualities = scanner.nextLine();
                if(filter.accept(id)){
                    String record= new JoinedStringBuilder(defLine,basecalls,qualDefLine,qualities)
                                        .glue("\n")
                                        .suffix("\n")
                                        .build();
                    out.write(record.getBytes("UTF-8"));
                }
            }
            scanner.close();
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(out);
        }
    }



    private static Set<String> parseIdsFrom(final File idFile, Integer numberOfIds)
            throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new FirstWordStringIdParser());
        final Set<String> ids; 
        if(numberOfIds ==null){
            ids= new HashSet<String>();
        }else{
            ids= new HashSet<String>(numberOfIds.intValue(),1F);
        }
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fastQFile  [-i | -e] idFile -o <output fastQ file> [OPTIONS] <fastq file>", 
                
                "filter  a fastQ file using the given ids to include/exclude and write the result to a new fastQ file",
                options,
                String.format("Example invocation%nfastQfile.pl -i ids.lst -o filtered.fastq original.fastq%nCreated by Danny Katzel"
                  ));
    }
    
   

}
