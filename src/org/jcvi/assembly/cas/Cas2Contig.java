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
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.cas.read.FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.cas.read.DefaultSffCasDataStoreFactory;
import org.jcvi.assembly.contig.ContigFileWriter;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStore;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.DefaultQualityFastaRecord;
import org.jcvi.fasta.fastq.SolexaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.joda.time.Period;

public class Cas2Contig {
    private static final String DEFAULT_PREFIX = "cas2contig";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    /**
     * @param args
     * @throws Throwable 
     */
    public static void main(String[] args) throws Throwable {
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
        
        options.addOption(new CommandLineOptionBuilder("s", "cache size ( default "+DEFAULT_CACHE_SIZE +" )")  
                                .longName("cache_size")
                                        .build());
        
        CommandLine commandLine;
        try {
            commandLine = CommandLineUtils.parseCommandLine(options, args);
        
            int cacheSize = commandLine.hasOption("s")? Integer.parseInt(commandLine.getOptionValue("s")) : DEFAULT_CACHE_SIZE;
            
            File casFile = new File(commandLine.getOptionValue("cas"));
            ReadWriteDirectoryFileServer outputDir = 
                    DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
            
            String prefix = commandLine.hasOption("prefix")? commandLine.getOptionValue("prefix"): DEFAULT_PREFIX;
            
            PrintWriter logOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".log")),true);
            PrintWriter consensusOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".consensus.fasta")),true);
            PrintWriter traceFilesOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".traceFiles.txt")),true);
            PrintWriter referenceFilesOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".referenceFiles.txt")),true);
            
            
            long startTime = System.currentTimeMillis();
           logOut.println(System.getProperty("user.dir"));
    
           try{
            final SolexaFastQQualityCodec solexaQualityCodec = new SolexaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
            MultiCasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                    new DefaultSffCasDataStoreFactory(),               
                    new FastQCasDataStoreFactory(solexaQualityCodec,cacheSize),
                    new FastaCasDataStoreFactory(cacheSize)        
            );
       
            CasAssembly casAssembly = new DefaultCasAssembly.Builder(casFile, casDataStoreFactory)
                                                            
                                                            .build();
            for(File traceFile : casAssembly.getNuceotideFiles()){
                traceFilesOut.println(traceFile.getAbsolutePath());
            }
            for(File traceFile : casAssembly.getReferenceFiles()){
                referenceFilesOut.println(traceFile.getAbsolutePath());
            }
            DataStore<CasContig> contigDatastore = casAssembly.getContigDataStore();
            logOut.println("finished creating contig datastore");
            logOut.printf("# of contigs = %d%n", contigDatastore.size());
    
            OutputStream contigOut = new FileOutputStream(outputDir.createNewFile(prefix+".contig"));
            PrintWriter seqOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".seq")),true);
            PrintWriter qualOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".qual")),true);
            ContigFileWriter contigWriter = new ContigFileWriter(contigOut);
            
            for(CasContig contig : contigDatastore){
                contigWriter.write(contig);
                consensusOut.print(new DefaultEncodedNucleotideFastaRecord(contig.getId(),
                        NucleotideGlyph.convertToString(NucleotideGlyph.convertToUngapped(contig.getConsensus().decode()))));
                
                for(PlacedRead read :contig.getPlacedReads()){
                    String id = read.getId();
                    seqOut.print(
                            new DefaultEncodedNucleotideFastaRecord(id,casAssembly.getNucleotideDataStore().get(id)));
                    qualOut.print(
                            new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(id,casAssembly.getQualityDataStore().get(id)));                
                }
            }
            contigWriter.close();
            consensusOut.close();
            seqOut.close();
            qualOut.close();
            outputDir.close();
            traceFilesOut.close();
            referenceFilesOut.close();
           }catch(Throwable t){
               t.printStackTrace(logOut);
               throw t;
           }finally{
            long endTime = System.currentTimeMillis();
            logOut.printf("took %s%n", new Period(endTime - startTime));
            logOut.flush();
            logOut.close();
           }
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "cas2Contig -cas <cas file> -o <output dir> [-prefix <prefix> -s <cache_size>]", 
                
                "convert a clc .cas assembly file into a contig package",
                options,
                "Created by Danny Katzel");
    }
}
