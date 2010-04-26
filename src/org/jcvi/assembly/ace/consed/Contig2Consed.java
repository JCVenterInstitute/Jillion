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
/*
 * Created on Dec 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.ArtificalNucleotideDataStoreFromContig;
import org.jcvi.assembly.ArtificalQualityDataStoreFromContig;
import org.jcvi.assembly.ace.AceAssembly;
import org.jcvi.assembly.ace.DefaultAceAdapterContigFileDataStore;
import org.jcvi.assembly.ace.DefaultAceAssembly;
import org.jcvi.assembly.contig.DefaultContigFileParser;
import org.jcvi.assembly.contig.qual.ZeroGapQualityValueStrategy;
import org.jcvi.assembly.slice.LargeSliceMapFactory;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeNucleotideFastaFileDataStore;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;
import org.jcvi.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;

public class Contig2Consed {

    private static final int DEFAULT_CACHE_SIZE = 1000;
    /**
     * @param args
     * @throws DataStoreException 
     * @throws IOException 
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("contig", "path to contig file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("seq", "path to seq file")
                        .build());
        options.addOption(new CommandLineOptionBuilder("qual", "path to qual file")
                    .build());
        options.addOption(new CommandLineOptionBuilder("out", "path to output directory")
                        .build());
        options.addOption(new CommandLineOptionBuilder("cache_size", "number of reads to store in cache for fast retrieval (should be set to at least max coverage): default :"+ DEFAULT_CACHE_SIZE)
                        .build());
        options.addOption(new CommandLineOptionBuilder("h", "prints this message")
                        .longName("help")
                            .build());
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            
            File outputDir;
            if(commandLine.hasOption("out")){
                outputDir= new File(commandLine.getOptionValue("out"));
            }else{
                outputDir = new File(".");
            }
            final int cacheSize;
            if(commandLine.hasOption("cache_size")){
                cacheSize = Integer.parseInt(commandLine.getOptionValue("cache_size"));
            }else{
                cacheSize = DEFAULT_CACHE_SIZE;
            }
            File contigFile = new File(commandLine.getOptionValue("contig"));
            DateTime date = new DateTime(DateTimeUtils.currentTimeMillis());
            DefaultAceAdapterContigFileDataStore aceDataStore = new DefaultAceAdapterContigFileDataStore(date.toDate());
            
            DefaultContigFileParser.parse(new FileInputStream(contigFile), aceDataStore);
            NucleotideDataStore seqDataStore;
            if(commandLine.hasOption("seq")){
                File seqFile = new File(commandLine.getOptionValue("seq"));
                seqDataStore = CachedDataStore.createCachedDataStore(NucleotideDataStore.class, 
                        new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeNucleotideFastaFileDataStore(seqFile))),
                        cacheSize);
               
            }
            else{
                seqDataStore = CachedDataStore.createCachedDataStore(NucleotideDataStore.class,
                        new ArtificalNucleotideDataStoreFromContig(aceDataStore), cacheSize);
            }
            QualityDataStore qualDataStore;
            if(commandLine.hasOption("qual")){
                File qualFile = new File(commandLine.getOptionValue("qual"));
                qualDataStore = CachedDataStore.createCachedDataStore(QualityDataStore.class, 
                        new QualityDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(qualFile))),
                        cacheSize);
                
                
            }else{
                qualDataStore = CachedDataStore.createCachedDataStore(QualityDataStore.class,
                        new ArtificalQualityDataStoreFromContig(aceDataStore, PhredQuality.valueOf(20)),
                        cacheSize);
            }
            
            
            long start= System.currentTimeMillis();
            
            
           
            
            PhdDataStore phdDataStore = new ArtificalPhdDataStore(seqDataStore,qualDataStore,date);
            AceAssembly aceAssembly = new DefaultAceAssembly(aceDataStore, phdDataStore);
            
            ConsedWriter.writeConsedPackage(aceAssembly, 
                    new LargeSliceMapFactory(new ZeroGapQualityValueStrategy(),cacheSize),                    
                    outputDir,"contig", false);
            long end= System.currentTimeMillis();
            System.out.printf("done! took %s%n", new Period(end-start));
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
    

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "contig2Consed -contig <contig file> [-seq <seq file>] [-qual <qual file>] -out <output dir>", 
                
                "create a consed package with an .ace file and a phd ball from a contig file "+
                "and optionally .seq and .qual.  If no .seq or .qual are given, "+
                "then phdball generates fake data.",
                options,
                "Created by Danny Katzel");
    }

}
