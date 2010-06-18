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
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.AbstractFastaVisitor;
import org.jcvi.fasta.ExcludeFastXIdFilter;
import org.jcvi.fasta.FastXFilter;
import org.jcvi.fasta.FastaParser;
import org.jcvi.fasta.FastaVisitor;
import org.jcvi.fasta.IncludeFastXIdFilter;
import org.jcvi.fasta.NullFastXFilter;
import org.jcvi.fasta.QualityFastaH2DataStore;
import org.jcvi.fasta.fastq.DefaultFastQRecord;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.fasta.fastq.FastQUtil;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.TemporaryDirectoryFileServer;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.IdReaderException;
import org.jcvi.io.idReader.StringIdParser;

/**
 * @author dkatzel
 *
 *
 */
public class Fasta2Fastq {

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws IOException, DataStoreException, IdReaderException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("s", 
                                    "input sequence FASTA file")
                        .longName("sequence")
                        .build());
        options.addOption(new CommandLineOptionBuilder("q", 
                     "input quality FASTA file")
                    .longName("quality")
                    .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                        "should encode output fastq file in SANGER fastq file (default is ILLUMINA 1.3+)")
                        .isFlag(true)
                       .build());
        
        options.addOption(new CommandLineOptionBuilder("o", 
                        "output fastq file")
                        .isRequired(true)
                        .build());
        
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            boolean useSanger = commandLine.hasOption("sanger");
            
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
                filter = NullFastXFilter.INSTANCE;
            }
            
            RunLengthEncodedGlyphCodec qualityCodec = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
            final FastQQualityCodec fastqQualityCodec = useSanger? new SangerFastQQualityCodec(qualityCodec): new IlluminaFastQQualityCodec(qualityCodec);
        
            //parse nucleotide data to temp file
            TemporaryDirectoryFileServer tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
     
            H2QualityDataStore h2DataStore = new H2QualityDataStore(tempDir.createNewFile("h2Qualities"));
            File qualFile = new File(commandLine.getOptionValue("q"));
            final QualityFastaH2DataStore qualityDataStore = new QualityFastaH2DataStore(qualFile, h2DataStore,filter);
            
            File seqFile = new File(commandLine.getOptionValue("s"));
            final PrintWriter writer = new PrintWriter(commandLine.getOptionValue("o"));
            
            FastaVisitor visitor = new AbstractFastaVisitor() {
                
                @Override
                public void visitRecord(String id, String comment, String entireBody) {
                    try {
                        if(filter.accept(id, comment)){
                            EncodedGlyphs<PhredQuality> qualities =qualityDataStore.get(id);
                            if(qualities ==null){
                                throw new IllegalStateException("no quality values for "+ id);
                            }
                            FastQRecord fastq = new DefaultFastQRecord(id, 
                                    new DefaultNucleotideEncodedGlyphs(entireBody.replaceAll("\\s+", "")), qualities);
    
                            writer.print(FastQUtil.encode(fastq, fastqQualityCodec));
                        }
                    } catch (DataStoreException e) {
                        throw new IllegalStateException("error getting quality data for "+ id);
                    }
                    
                }
            };
            FastaParser.parseFasta(seqFile, visitor);
            writer.close();
            IOUtil.closeAndIgnoreErrors(qualityDataStore);
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fasta2Fastq [OPTIONS] -s <seq file> -q <qual file> -o <fastq file>", 
                
                "Parse a  seq and qual file and write the results out a fastq file",
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
}
