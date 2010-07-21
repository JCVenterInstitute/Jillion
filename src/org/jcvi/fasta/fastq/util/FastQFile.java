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
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.fasta.ExcludeFastXIdFilter;
import org.jcvi.fasta.FastXFilter;
import org.jcvi.fasta.IncludeFastXIdFilter;
import org.jcvi.fasta.fastq.FastQFileParser;
import org.jcvi.fasta.fastq.FastQFileVisitor;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.FirstWordStringIdParser;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.IdReaderException;
/**
 * {@code FastQFile} is meant to be a FastQ version of 454's fnafile
 * which takes in a file of reads and filter options
 * and writes out a new file filtering the reads as specified.
 * @author dkatzel
 *
 *
 */
public class FastQFile implements FastQFileVisitor{
    private static final String DEFAULT_FILE_OUTPUT = "Reads.fastq";
    private final FastXFilter filter;
    private final OutputStream out;
    private boolean shouldWrite=false;
    private String currentLine;
    
    /**
     * @param filter
     * @param out
     */
    public FastQFile(FastXFilter filter, OutputStream out) {
        this.filter = filter;
        this.out = out;
    }

  

    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        shouldWrite= filter.accept(id, optionalComment);
        if(shouldWrite){
            writeToOutputStream(currentLine);
        }
        return shouldWrite;
    }

    @Override
    public void visitEncodedQualities(String encodedQualities) {
        shouldWrite=false;
    }

    @Override
    public void visitEndBlock() {
        try {
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("could not flush output file", e);
        }
    }

    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
    }

    @Override
    public void visitLine(String line) {
        currentLine = line;
        if(shouldWrite){
            writeToOutputStream(line);
        }
        
    }



    private void writeToOutputStream(String line) {
        try {
            out.write((line+"\n").getBytes());
        } catch (IOException e) {
            throw new RuntimeException("could not write to output file", e);
        }
    }

    @Override
    public void visitEndOfFile() {
        
        
    }

    @Override
    public void visitFile() {
        
    }
    /**
     * @param args
     * @throws IdReaderException 
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws IdReaderException, FileNotFoundException {
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
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOptionGroup(group);
        OutputStream out=null;
        if(args.length ==1 && args[0].endsWith("-h")){
            printHelp(options);
            System.exit(0);
        }
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, 
                    Arrays.copyOf(args, args.length-1));
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
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else{
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile));
            }
            FastQFile fastQFileAdapter = new FastQFile(filter, out);
            FastQFileParser.parse(fastQFile, fastQFileAdapter);
            
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(out);
        }
    }



    private static Set<String> parseIdsFrom(final File idFile)
            throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new FirstWordStringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fastQFile [-i | -e] idFile -o <output fastQ file> [OPTIONS] <fastq file>", 
                
                "filter  a fastQ file using the given ids to include/exclude and write the result to a new fastQ file",
                options,
                String.format("Example invocation%nfastQfile.pl -i ids.lst -o filtered.fastq original.fastq%nCreated by Danny Katzel"
                  ));
    }
}
