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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.ExcludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastqFileDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

/**
 * {@code Fastq2Fasta} is a program that parses a Fastq file and
 * write the data out as seq and/or qual fastas.
 * @author dkatzel
 *
 *
 */
public class Fastq2Fasta {
   
    
   
    /**
     * @param args
     * @throws FileNotFoundException 
     * @throws IdReaderException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, IdReaderException, DataStoreException {
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("s", 
                                    "output file to write sequence Data to")
                        .longName("sequence")
                        .build());
        options.addOption(new CommandLineOptionBuilder("q", 
                     "output file to write quality Data to")
                    .longName("quality")
                    .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                        "input fastq file is encoded as a SANGER fastq file (default is ILLUMINA 1.3+)")
                    .isFlag(true)
                       .build());
        options.addOption(CommandLineUtils.createHelpOption());
        
        options.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        options.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());

        OutputStream seqOut =null;
        OutputStream qualOut =null;
        if(args.length ==1 && args[0].endsWith("-h")){
            printHelp(options);
            System.exit(0);
        }
        if(args.length <1){
            printHelp(options);
            System.exit(1);
        }
        File fastQFile = new File(args[args.length-1]);
        
        try {
            if(CommandLineUtils.helpRequested(args)){
                printHelp(options);
                System.exit(0);
            }
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, 
                    Arrays.copyOf(args, args.length-1));
            
            if(commandLine.hasOption("s")){
                seqOut = new FileOutputStream(commandLine.getOptionValue("s"));
            }
            if(commandLine.hasOption("q")){
                qualOut = new FileOutputStream(commandLine.getOptionValue("q"));
            }
            if(seqOut ==null && qualOut ==null){
                throw new ParseException("must specify at least either -s or -q");
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
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile));
            }else{
                filter = AcceptingFastXFilter.INSTANCE;
            }
            final FastqQualityCodec fastqQualityCodec;
            if(commandLine.hasOption("sanger")){
                fastqQualityCodec = FastqQualityCodec.SANGER;
            }else{
                fastqQualityCodec = FastqQualityCodec.ILLUMINA;
            }
            
            CloseableIterator<FastqRecord> iter=null;
            try{
            	iter = LargeFastqFileDataStore.create(fastQFile, filter, fastqQualityCodec)
            								.iterator();
            	while(iter.hasNext()){
            		FastqRecord fastQ = iter.next();
            		 String id = fastQ.getId();
                     if(qualOut!=null){
                         try {
                             qualOut.write(new DefaultQualityFastaRecord(id, 
                                     fastQ.getQualitySequence()).toString().getBytes());
                         } catch (IOException e) {
                             throw new IOException("could not write to quality data for "+ id, e);
                         }
                     }
                     if(seqOut!=null){
                         try {
                             seqOut.write(new NucleotideSequenceFastaRecord(
                                     id,fastQ.getComment(),fastQ.getNucleotideSequence()) 
                                     .toString().getBytes());
                         } catch (IOException e) {
                             throw new IOException("could not write to sequence data for "+ id, e);
                         }
                     }
            	}
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter,seqOut,qualOut);
            }
            
            
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(seqOut);
            IOUtil.closeAndIgnoreErrors(qualOut);
        }
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
        
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fastq2Fasta [OPTIONS] <fastq file>", 
                
                "Parse a fastQ file and write the results out as seq and/or qual fasta files",
                options,
                String.format("Example invocation%nfastq2Fasta.pl -i ids.lst -s filtered.seq.fasta original.fastq%nCreated by Danny Katzel"
                  ));
    }
  
   

}
