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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.jcvi.assembly.ace.consed.closure.NextGenClosureAceContigTrimmer;
import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.LargeNoQualitySliceMapFactory;
import org.jcvi.assembly.slice.SliceMapFactory;
import org.jcvi.assembly.util.DefaultTrimFileDataStore;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadOnlyDirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.TraceQualityDataStoreAdapter;
import org.jcvi.trace.sanger.FileSangerTrace;
import org.jcvi.trace.sanger.SingleSangerTraceDirectoryFileDataStore;
import org.jcvi.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdSangerTraceDataStoreAdapter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;

public class Cas2Consed {
    private static final String DEFAULT_PREFIX = "cas2consed";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    
    private static final File DEFAULT_TEMP_DIR = new File("/usr/local/scratch");
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
        options.addOption(new CommandLineOptionBuilder("tempDir", "temp directory")
                                                    .build());
        options.addOption(new CommandLineOptionBuilder("prefix", "file prefix for all generated files ( default "+DEFAULT_PREFIX +" )")                                
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
        
            int cacheSize = commandLine.hasOption("s")? Integer.parseInt(commandLine.getOptionValue("s")) : DEFAULT_CACHE_SIZE;
            
            File casFile = new File(commandLine.getOptionValue("cas"));
            File casWorkingDirectory = casFile.getParentFile();
            ReadWriteDirectoryFileServer outputDir = 
                    DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
            
            String prefix = commandLine.hasOption("prefix")? commandLine.getOptionValue("prefix"): DEFAULT_PREFIX;
            TrimDataStore trimDatastore;
            if(commandLine.hasOption("trim")){
                List<TrimDataStore> dataStores = new ArrayList<TrimDataStore>();
                final String trimFiles = commandLine.getOptionValue("trim");
                for(String trimFile : trimFiles.split(",")){
                    System.out.println("adding trim file "+ trimFile);
                    dataStores.add( new DefaultTrimFileDataStore(new File(trimFile)));
                }
                trimDatastore = MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimDataStore.class, dataStores);
            }else{
                trimDatastore = TrimDataStoreUtil.EMPTY_DATASTORE;
            }
            CasTrimMap trimToUntrimmedMap;
            if(commandLine.hasOption("trimMap")){
                trimToUntrimmedMap = new DefaultTrimFileCasTrimMap(new File(commandLine.getOptionValue("trimMap")));
            }else{
                trimToUntrimmedMap = new UnTrimmedExtensionTrimMap();
            }
            Integer minCoverageAtEnds=null;
            if(commandLine.hasOption("coverage_trim")){
                minCoverageAtEnds = Integer.parseInt(commandLine.getOptionValue("coverage_trim"));
            }
            
            boolean useClosureTrimming = commandLine.hasOption("useClosureTrimming");
            TraceDataStore<FileSangerTrace> sangerTraceDataStore=null;
            Map<String, File> sangerFileMap = null;
            ReadOnlyDirectoryFileServer sourceChromatogramFileServer = null;
            
            if(commandLine.hasOption("chromat_dir")){
                sourceChromatogramFileServer = DirectoryFileServer.createReadOnlyDirectoryFileServer(new File(commandLine.getOptionValue("chromat_dir")));
                sangerTraceDataStore = new SingleSangerTraceDirectoryFileDataStore(
                        sourceChromatogramFileServer, ".scf");
                sangerFileMap = new HashMap<String, File>();
                Iterator<String> iter = sangerTraceDataStore.getIds();
                while(iter.hasNext()){
                    String id = iter.next();
                    sangerFileMap.put(id, sangerTraceDataStore.get(id).getFile());
                }
            }
            PrintWriter logOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".log")),true);
            PrintWriter consensusOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".consensus.fasta")),true);
            PrintWriter traceFilesOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".traceFiles.txt")),true);
            PrintWriter referenceFilesOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".referenceFiles.txt")),true);
            
            
            long startTime = System.currentTimeMillis();
           logOut.println(System.getProperty("user.dir"));
           
         final ReadWriteDirectoryFileServer tempDir;
         if(!commandLine.hasOption("tempDir")){
             //default to scratch
             tempDir=DirectoryFileServer.createTemporaryDirectoryFileServer(DEFAULT_TEMP_DIR);
         }else{
             File t =new File(commandLine.getOptionValue("tempDir"));
             t.mkdirs();
             tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer(t);
         }
           try{
               if(!outputDir.contains("chromat_dir")){
                   outputDir.createNewDir("chromat_dir");
               }
               //copy scfs
               if(sourceChromatogramFileServer !=null){
                   for(File f :sourceChromatogramFileServer){
                       String name =f.getName();
                       
                       OutputStream out = new FileOutputStream(outputDir.createNewFile("chromat_dir/"+name));
                       final FileInputStream fileInputStream = new FileInputStream(f);
                       
                       try{
                           IOUtils.copy(fileInputStream, out);
                       }finally{
                           IOUtils.closeQuietly(out);
                           IOUtils.closeQuietly(fileInputStream);
                       }
                   }
               }
               FastQQualityCodec qualityCodec=  commandLine.hasOption("useIllumina")?  
                       new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE)
                    : new SangerFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
               
               CasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                        new H2SffCasDataStoreFactory(casWorkingDirectory,tempDir,EmptyDataStoreFilter.INSTANCE),               
                        new H2FastQCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,qualityCodec,tempDir.getRootDir()),
                        new FastaCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,cacheSize)        
                );
                
                final SliceMapFactory sliceMapFactory = new LargeNoQualitySliceMapFactory();
                
                CasAssembly casAssembly = new DefaultCasAssembly.Builder(casFile, casDataStoreFactory, trimDatastore, trimToUntrimmedMap,casWorkingDirectory)
                .build();
                System.out.println("finished making casAssemblies");
                for(File traceFile : casAssembly.getNuceotideFiles()){
                    traceFilesOut.println(traceFile.getAbsolutePath());
                    final String name = traceFile.getName();
                    String extension = FilenameUtils.getExtension(name);
                    if(name.contains("fastq")){
                        if(!outputDir.contains("solexa_dir")){
                            outputDir.createNewDir("solexa_dir");
                        }
                        if(outputDir.contains("solexa_dir/"+name)){
                            outputDir.getFile("solexa_dir/"+name).delete();
                        }
                        outputDir.createNewSymLink(traceFile.getAbsolutePath(), "solexa_dir/"+name);
                    }else if ("sff".equals(extension)){
                        if(!outputDir.contains("sff_dir")){
                            outputDir.createNewDir("sff_dir");
                        }
                        
                        if(outputDir.contains("sff_dir/"+name)){
                            outputDir.getFile("sff_dir/"+name).delete();
                        }
                        outputDir.createNewSymLink(traceFile.getAbsolutePath(), "sff_dir/"+name);
                    }
                }
                for(File traceFile : casAssembly.getReferenceFiles()){
                    referenceFilesOut.println(traceFile.getAbsolutePath());
                }
                DataStore<CasContig> contigDatastore = casAssembly.getContigDataStore();
                Map<String, AceContig> aceContigs = new HashMap<String, AceContig>();
                CasIdLookup readIdLookup = sangerFileMap ==null? casAssembly.getReadIdLookup() :
                    new DifferentFileCasIdLookupAdapter(casAssembly.getReadIdLookup(), sangerFileMap);
                Date phdDate = new Date(startTime);
                NextGenClosureAceContigTrimmer closureContigTrimmer=null;
                if(useClosureTrimming){
                    closureContigTrimmer= new NextGenClosureAceContigTrimmer(5, 5, 10);
                }
                for(CasContig casContig : contigDatastore){
                    final AceContigAdapter adpatedCasContig = new AceContigAdapter(casContig, phdDate,readIdLookup);
                    CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(adpatedCasContig.getPlacedReads());
                    for(AceContig aceContig : ConsedUtil.split0xContig(adpatedCasContig, coverageMap)){
                        if(useClosureTrimming){
                            AceContig trimmedAceContig =closureContigTrimmer.trimContig(aceContig);
                            if(trimmedAceContig ==null){
                                System.out.printf("%s was completely trimmed... skipping%n", aceContig.getId());
                                continue;
                            }
                            aceContig =trimmedAceContig;
                        }
                       aceContigs.put(aceContig.getId(), aceContig);
                        consensusOut.print(new DefaultEncodedNucleotideFastaRecord(aceContig.getId(),
                                NucleotideGlyph.convertToString(NucleotideGlyph.convertToUngapped(aceContig.getConsensus().decode()))));
                    }
                    
                }
                System.out.printf("finished adapting %d casAssemblies into %d ace contigs%n", contigDatastore.size(),aceContigs.size());
                QualityDataStore qualityDataStore = sangerTraceDataStore == null?
                            casAssembly.getQualityDataStore() :
                                MultipleDataStoreWrapper.createMultipleDataStoreWrapper(QualityDataStore.class, 
                                        TraceQualityDataStoreAdapter.adapt(sangerTraceDataStore),casAssembly.getQualityDataStore() );
                final DateTime phdDateTime = new DateTime(phdDate);
                final PhdDataStore casPhdDataStore = CachedDataStore.createCachedDataStore(PhdDataStore.class,
                        new ArtificalPhdDataStore(casAssembly.getNucleotideDataStore(), qualityDataStore, phdDateTime),
                        cacheSize);
                final PhdDataStore phdDataStore = sangerTraceDataStore ==null? casPhdDataStore :
                    MultipleDataStoreWrapper.createMultipleDataStoreWrapper(PhdDataStore.class,
                            new PhdSangerTraceDataStoreAdapter<FileSangerTrace>(sangerTraceDataStore,phdDateTime),
                            casPhdDataStore);
                
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
            trimDatastore.close();
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
