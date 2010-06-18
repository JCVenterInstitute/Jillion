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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.DefaultQualityFastaRecord;
import org.jcvi.fasta.fastq.AbstractFastQFileVisitor;
import org.jcvi.fasta.fastq.FastQFileParser;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.IdReaderException;
import org.jcvi.io.idReader.StringIdParser;

/**
 * {@code Fastq2Fasta} is a program that parses a Fastq file and
 * write the data out as seq and/or qual fastas.
 * @author dkatzel
 *
 *
 */
public class Fastq2Fasta extends AbstractFastQFileVisitor<FastQRecord> {
    private final FastQFilter filter;
    private final OutputStream seqOut;
    private final OutputStream qualOut;
    private final FastQQualityCodec qualityCodec;
    private boolean shouldWrite=false;
    
    private String currentId;
    
    /**
     * @param filter
     * @param out
     */
    public Fastq2Fasta(FastQFilter filter, FastQQualityCodec qualityCodec,OutputStream seqOut,OutputStream qualOut) {
        this.filter = filter;
        this.seqOut = seqOut;
        this.qualOut = qualOut;
        if(seqOut==null && qualOut==null){
            throw new IllegalArgumentException("must write at least seq or qual data");
        }
        this.qualityCodec = qualityCodec;
    }
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        shouldWrite= filter.accept(id, optionalComment);
        if(shouldWrite){
            currentId = id;
        }
        return shouldWrite;
    }

    
    @Override
    public boolean visitEncodedQualities(String encodedQualities) {
        if(qualOut!=null && shouldWrite){
            try {
                qualOut.write(new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(currentId, 
                        qualityCodec.decode(encodedQualities)).toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("could not write to quality data for "+ currentId, e);
            }
        }
        return true;
    }
    @Override
    public boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        if(seqOut!=null && shouldWrite){
            try {
                seqOut.write(new DefaultEncodedNucleotideFastaRecord( currentId,nucleotides) 
                        .toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException("could not write to sequence data for "+ currentId, e);
            }
        }
        return true;
    }
    /**
     * @param args
     * @throws FileNotFoundException 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws FileNotFoundException, IdReaderException {
        
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
                        "input fastq file is encoded as a SANGER fastq file (default is SOLEXA)")
                    .isFlag(true)
                       .build());
        OptionGroup group = new OptionGroup();
        group.setRequired(true);
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        
        options.addOptionGroup(group);
        OutputStream seqOut =null;
        OutputStream qualOut =null;
        if(args.length <1){
            printHelp(options);
            System.exit(1);
        }
        File fastQFile = new File(args[args.length-1]);
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, 
                    Arrays.copyOf(args, args.length-1));
            
            if(commandLine.hasOption("s")){
                seqOut = new FileOutputStream(commandLine.getOptionValue("s"));
            }
            if(commandLine.hasOption("q")){
                qualOut = new FileOutputStream(commandLine.getOptionValue("q"));
            }
            final File idFile;
            final FastQFilter filter;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastQIdFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastQIdFilter(parseIdsFrom(idFile));
            }else{
                filter = NullFastQFilter.INSTANCE;
            }
            final FastQQualityCodec fastqQualityCodec;
            if(commandLine.hasOption("sanger")){
                fastqQualityCodec = new SangerFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
            }else{
                fastqQualityCodec = new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
            }
            Fastq2Fasta fastq2Fasta = new Fastq2Fasta(filter, fastqQualityCodec, seqOut, qualOut);
            
            FastQFileParser.parse(fastQFile, fastq2Fasta);
            
            
        } catch (ParseException e) {
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
