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
 * Created on Jan 8, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jcvi.assembly.ace.AceAssembly;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigAdapter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.ConsensusAceTag;
import org.jcvi.assembly.ace.DefaultAceAssembly;
import org.jcvi.assembly.ace.DefaultAceTagMap;
import org.jcvi.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.assembly.ace.ReadAceTag;
import org.jcvi.assembly.ace.WholeAssemblyAceTag;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.ace.consed.ConsedWriter;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.LargeNoQualitySliceMapFactory;
import org.jcvi.assembly.slice.SliceMapFactory;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.fastq.SolexaFastQQualityCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;

public class Cas2Consed {
    private static final String DEFAULT_PREFIX = "cas2consed";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    /**
     * @param args
     * @throws Throwable 
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
               if(!outputDir.contains("chromat_dir")){
                   outputDir.createNewDir("chromat_dir");
               }
                final SolexaFastQQualityCodec solexaQualityCodec = new SolexaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
                MultiCasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                        new H2SffCasDataStoreFactory(),               
                        new H2FastQCasDataStoreFactory(solexaQualityCodec),
                        new FastaCasDataStoreFactory(cacheSize)        
                );
                
                final SliceMapFactory sliceMapFactory = new LargeNoQualitySliceMapFactory();
                                                    CasAssembly casAssembly = new DefaultCasAssembly.Builder(casFile, casDataStoreFactory)
                                                    .build();
                System.out.println("finished making casAssemblies");
                for(File traceFile : casAssembly.getNuceotideFiles()){
                    traceFilesOut.println(traceFile.getAbsolutePath());
                    String extension = FilenameUtils.getExtension(traceFile.getName());
                    if("fastq".equals(extension)){
                        if(!outputDir.contains("solexa_dir")){
                            outputDir.createNewDir("solexa_dir");
                        }
                        outputDir.createNewSymLink(traceFile.getAbsolutePath(), "solexa_dir/"+traceFile.getName());
                    }else if ("sff".equals(extension)){
                        if(!outputDir.contains("sff_dir")){
                            outputDir.createNewDir("sff_dir");
                        }
                        outputDir.createNewSymLink(traceFile.getAbsolutePath(), "sff_dir/"+traceFile.getName());
                    }
                }
                for(File traceFile : casAssembly.getReferenceFiles()){
                    referenceFilesOut.println(traceFile.getAbsolutePath());
                }
                DataStore<CasContig> contigDatastore = casAssembly.getContigDataStore();
                Map<String, AceContig> aceContigs = new HashMap<String, AceContig>();
                Date phdDate = new Date(startTime);
                for(CasContig casContig : contigDatastore){
                    final AceContigAdapter adpatedCasContig = new AceContigAdapter(casContig, phdDate,casAssembly.getReadIdLookup());
                    CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(adpatedCasContig.getPlacedReads());
                    for(AceContig splitAceContig : ConsedUtil.split0xContig(adpatedCasContig, coverageMap)){
                        aceContigs.put(splitAceContig.getId(), splitAceContig);
                        consensusOut.print(new DefaultEncodedNucleotideFastaRecord(splitAceContig.getId(),
                                NucleotideGlyph.convertToString(NucleotideGlyph.convertToUngapped(splitAceContig.getConsensus().decode()))));
                    }
                    
                }
                System.out.println("finished adapting casAssemblies into aces");
                final DataStore<Phd> phdDataStore = CachedDataStore.createCachedDataStore(DataStore.class,
                        new ArtificalPhdDataStore(casAssembly.getNucleotideDataStore(), casAssembly.getQualityDataStore(), new DateTime(phdDate)),
                        cacheSize);
                /*
                 * WA{
    phdBall newbler 080416:144002
    ../phdball_dir/phd.ball.1
    }

                 */
                WholeAssemblyAceTag pathToPhd = new DefaultWholeAssemblyAceTag("phdball", "cas2consed", 
                        new Date(DateTimeUtils.currentTimeMillis()), "../phd_dir/"+prefix+".phd.ball");
               
                AceAssembly aceAssembly = new DefaultAceAssembly<AceContig>(new SimpleDataStore<AceContig>(aceContigs), 
                        phdDataStore, Collections.<File>emptyList(),new DefaultAceTagMap(Collections.<ConsensusAceTag>emptyList(), Collections.<ReadAceTag>emptyList(), 
                                Arrays.asList(pathToPhd)));
                System.out.println("writing consed package...");
                ConsedWriter.writeConsedPackage(aceAssembly, sliceMapFactory,outputDir.getRootDir(), prefix, false);
           
           }catch(Throwable t){
               t.printStackTrace(logOut);
               throw t;
           }finally{
            long endTime = System.currentTimeMillis();
            logOut.printf("took %s%n", new Period(endTime - startTime));
            logOut.flush();
            logOut.close();
            outputDir.close();
            consensusOut.close();
            traceFilesOut.close();
            referenceFilesOut.close();
           }
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
       }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "cas2Consed -cas <cas file> -o <output dir> [-prefix <prefix> -s <cache_size>]", 
                
                "convert a clc .cas assembly file into a consed package",
                options,
                "Created by Danny Katzel");
    }
}
