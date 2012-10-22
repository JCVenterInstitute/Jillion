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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqFileDataStoreFactory;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriter;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;

/**
 * @author dkatzel
 *
 *
 */
public class SplitFastq {

    /**
     * @param args
     * @throws ParseException 
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("i", 
                                    "input fastq file")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("o", 
        "output directory to write split files to")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("n", 
                     "number of files to split into")
                    .isRequired(true)
                    .build());
        
        options.addOption(new CommandLineOptionBuilder("sanger", 
                        "input fastq file is encoded as a SANGER fastq file (default is ILLUMINA 1.3+)")
                    .isFlag(true)
                       .build());
        
        options.addOption(CommandLineUtils.createHelpOption());

        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        CommandLine commandLine;
        try {
            commandLine = CommandLineUtils.parseCommandLine(options, args);
            File fastqFile = new File(commandLine.getOptionValue("i"));
            final FastqQualityCodec fastqQualityCodec;
            if(commandLine.hasOption("sanger")){
                fastqQualityCodec = FastqQualityCodec.SANGER;
            }else{
                fastqQualityCodec = FastqQualityCodec.ILLUMINA;
            }
            int n = Integer.parseInt(commandLine.getOptionValue("n"));
            ReadWriteDirectoryFileServer outputDir = DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
            List<FastqRecordWriter> writers = createWriters(outputDir, fastqFile, n,fastqQualityCodec);
            
            StreamingIterator<FastqRecord> iterator= null;
            		
            int counter=0;
            try{
            	iterator=FastqFileDataStoreFactory.create(fastqFile, DataStoreProviderHint.OPTIMIZE_ITERATION, fastqQualityCodec)
                		.iterator();
                while(iterator.hasNext()){
                    int mod = counter %n;
                    FastqRecord record = iterator.next();
                    writers.get(mod).write(record); 
                    counter++;
                }
            }finally{
                for(FastqRecordWriter writer : writers){
                    IOUtil.closeAndIgnoreErrors(writer);
                }
                IOUtil.closeAndIgnoreErrors(iterator);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
       
    }

    /**
     * @param outputDir
     * @param fastqFile
     * @param n
     * @return
     * @throws IOException 
     */
    private static List<FastqRecordWriter> createWriters(
            ReadWriteDirectoryFileServer outputDir, File fastqFile, int n, FastqQualityCodec fastqQualityCodec) throws IOException {
        List<FastqRecordWriter> writers = new ArrayList<FastqRecordWriter>();
        String basename = fastqFile.getName();
        try{
            for(int i=0; i< n; i++){
                File newFile =outputDir.createNewFile(String.format("%s.part_%d.fastq", basename, i));
                writers.add(new FastqRecordWriterBuilder(newFile)
                			.qualityCodec(fastqQualityCodec)
                			.build());
            }
            return writers;
        }catch(IOException e){
            for(FastqRecordWriter writer : writers){
                IOUtil.closeAndIgnoreErrors(writer);
            }
            throw e;
        }
    }

    /**
     * @param options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "splitFastq [OPTIONS] -i <fastq file> -n <# files>", 
                
                "Parse a fastQ file and re-write the data into several files.  Each file will contain 1/nth of the total number of reads." +
                "the files will be named <original fastq file>.part_[0-(n-1)].fastq",
                options,
                "Created by Danny Katzel"
                  );
        
    }

}
