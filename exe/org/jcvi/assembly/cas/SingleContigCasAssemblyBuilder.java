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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigAdapter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.ace.consed.closure.NextGenClosureAceContigTrimmer;
import org.jcvi.assembly.cas.AbstractMultiThreadedCasAssemblyBuilder.AceWriterCallable;
import org.jcvi.assembly.cas.AbstractMultiThreadedCasAssemblyBuilder.PhdWriterCallable;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasFileQualityDataStore;
import org.jcvi.assembly.cas.read.DefaultCasFileReadIndexToContigLookup;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.cas.read.ReadCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.SffTrimDataStore;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.trim.TrimmerException;
import org.jcvi.assembly.util.DefaultTrimFileDataStore;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadOnlyDirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.TraceQualityDataStoreAdapter;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.trace.sanger.FileSangerTrace;
import org.jcvi.trace.sanger.SingleSangerTraceDirectoryFileDataStore;
import org.jcvi.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdSangerTraceDataStoreAdapter;
import org.jcvi.util.ExceptionIntolerantFixedSizedThreadPoolExecutor;
import org.jcvi.util.MultipleWrapper;
import org.joda.time.DateTime;

/**
 * @author dkatzel
 *
 *
 */
public class SingleContigCasAssemblyBuilder {
    private static final String DEFAULT_PREFIX = "temp";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     * @throws TrimmerException 
     * @throws SFFDecoderException 
     */
    public static void main(String[] args) throws IOException, DataStoreException, TrimmerException, SFFDecoderException {
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
        
        options.addOption(new CommandLineOptionBuilder("useIllumina", "any FASTQ files in this assembly are encoded in Illumina 1.3+ format (default is Sanger)")                                
                            .isFlag(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("useClosureTrimming", "apply additional contig trimming based on JCVI Closure rules")                                
                                                .isFlag(true)
                                                .build());
        options.addOption(new CommandLineOptionBuilder("casId", "the cas id to convert into ace contigs")                                
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("tempDir", "the path to the temp area to write temporary datastore files, these could get quite large")                                
                                    .isRequired(true)
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
                trimDatastore = MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimDataStore.class,
                        dataStores);
            }else{
                trimDatastore = TrimDataStoreUtil.EMPTY_DATASTORE;
            }
            CasTrimMap trimToUntrimmedMap;
            if(commandLine.hasOption("trimMap")){
                trimToUntrimmedMap = new DefaultTrimFileCasTrimMap(new File(commandLine.getOptionValue("trimMap")));
            }else{
                trimToUntrimmedMap = new UnTrimmedExtensionTrimMap();
            }
          
            
            boolean useClosureTrimming = commandLine.hasOption("useClosureTrimming");
            TraceDataStore<FileSangerTrace> sangerTraceDataStore=null;
            ReadOnlyDirectoryFileServer sourceChromatogramFileServer = null;
            
            if(commandLine.hasOption("chromat_dir")){
                sourceChromatogramFileServer = DirectoryFileServer.createReadOnlyDirectoryFileServer(new File(commandLine.getOptionValue("chromat_dir")));
                sangerTraceDataStore = new SingleSangerTraceDirectoryFileDataStore(
                        sourceChromatogramFileServer, ".scf");               
            }
            
            boolean useIllumina = commandLine.hasOption("useIllumina");
            final long referenceId = Long.parseLong(commandLine.getOptionValue("casId"));
            
            
            Date phdDate = new Date(System.currentTimeMillis());
            NextGenClosureAceContigTrimmer closureContigTrimmer=null;
            if(useClosureTrimming){
                closureContigTrimmer= new NextGenClosureAceContigTrimmer(2,5, 10);
            }
            
            DefaultCasFileReadIndexToContigLookup read2contigMap = new DefaultCasFileReadIndexToContigLookup();
            AbstractDefaultCasFileLookup readIdLookup = new DefaultReadCasFileLookup(casWorkingDirectory);
            AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup(casWorkingDirectory);
            
            CasDataStoreFactory referenceDataStoreFactory= new FastaCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,cacheSize);       
            
            AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(
                    referenceDataStoreFactory);
                CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(CasFileVisitor.class,
                        referenceIdLookup,readIdLookup,read2contigMap,referenceNucleotideDataStore));
                
               
                SffTrimDataStore sffTrimDatastore = new SffTrimDataStore();
                for(File readFile : readIdLookup.getFiles()){
                    String extension =FilenameUtils.getExtension(readFile.getName());
                    if("sff".equals(extension)){
                        SffParser.parseSFF(readFile, sffTrimDatastore);
                    }
                }
                TrimDataStore multiTrimDataStore =MultipleDataStoreWrapper.createMultipleDataStoreWrapper(
                        TrimDataStore.class, trimDatastore, sffTrimDatastore);
            
            File tempDir = new File(commandLine.getOptionValue("tempDir"));
            if(!tempDir.exists()){
                IOUtil.mkdirs(tempDir);
            }
            /////
            String referenceName = referenceIdLookup.getLookupIdFor(referenceId);
            List<Long> readIndexes = read2contigMap.getReadIdsForContig(referenceId);
            List<String> readIds = new ArrayList<String>(readIndexes.size());
            for(Long readIndex : readIndexes){
                readIds.add(readIdLookup.getLookupIdFor(readIndex));
            }
            DataStoreFilter readFilter =new DefaultIncludeDataStoreFilter(readIds);
            FastQQualityCodec qualityCodec=  useIllumina?  
                    new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE)
                 : new SangerFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
            
            MultiCasDataStoreFactory filteredCasReadDataStoreFactory = new MultiCasDataStoreFactory(
                     new H2SffCasDataStoreFactory(casWorkingDirectory,
                             DirectoryFileServer.createTemporaryDirectoryFileServer(tempDir),
                             readFilter),               
                     new H2FastQCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,qualityCodec,readFilter,tempDir),
                     new FastaCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,readFilter,cacheSize)        
             );
            
            AbstractCasFileNucleotideDataStore nucleotideDataStore = new ReadCasFileNucleotideDataStore(filteredCasReadDataStoreFactory);
            
            DefaultCasFileQualityDataStore qualityDataStore = new DefaultCasFileQualityDataStore(filteredCasReadDataStoreFactory);
            DefaultCasGappedReferenceMap gappedReferenceMap = new SingleCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup,referenceId);
            
            CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(CasFileVisitor.class,
                    nucleotideDataStore,qualityDataStore,gappedReferenceMap
                    ));
            final DefaultCasContig.Builder contigBuilder = new DefaultCasContig.Builder(referenceName);
            AbstractCasFileContigVisitor singleContigVisitor = new AbstractCasFileContigVisitor(
                    referenceIdLookup, readIdLookup, gappedReferenceMap, nucleotideDataStore,
                    multiTrimDataStore){

                        @Override
                        public synchronized void visitMatch(CasMatch match) {
                            boolean calledSuper=false;
                            if(match.matchReported()){
                                //only visit reads that align to our contig
                                if(match.getChosenAlignment().contigSequenceId() == referenceId){
                                    super.visitMatch(match);
                                    calledSuper=true;
                                }
                            }
                            if(!calledSuper){
                                incrementReadCounter();
                            }
                           
                        }

                        @Override
                        protected void visitPlacedRead(long referenceId,
                                CasPlacedRead casPlacedRead) {
                            contigBuilder.addCasPlacedRead(casPlacedRead);
                            
                        }
                
            };
            
            CasParser.parseCas(casFile, singleContigVisitor);
            //here we are done parsing
            CasContig casContig =contigBuilder.build();
          //  casContigMap.put(contig.getId(), contig);
            final AceContigAdapter adpatedCasContig = new AceContigAdapter(casContig, phdDate,readIdLookup);
            DataStore<EncodedGlyphs<PhredQuality>> aceQualityDatastore;
            
            if(commandLine.hasOption("chromat_dir")){                
                aceQualityDatastore = MultipleDataStoreWrapper.createMultipleDataStoreWrapper(QualityDataStore.class, 
                        TraceQualityDataStoreAdapter.adapt(sangerTraceDataStore),qualityDataStore);
            }else{
                aceQualityDatastore = qualityDataStore;
            }
            final DateTime phdDateTime = new DateTime(phdDate);
            final PhdDataStore casPhdDataStore = CachedDataStore.createCachedDataStore(PhdDataStore.class,
                    new ArtificalPhdDataStore(nucleotideDataStore, aceQualityDatastore, phdDateTime),
                    1000);
            final PhdDataStore phdDataStore = !commandLine.hasOption("chromat_dir")? casPhdDataStore :
                MultipleDataStoreWrapper.createMultipleDataStoreWrapper(PhdDataStore.class,
                        new PhdSangerTraceDataStoreAdapter<FileSangerTrace>(sangerTraceDataStore,phdDateTime),
                        casPhdDataStore);
            CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(adpatedCasContig.getPlacedReads());
            int numReads =0;
            int numContigs =0;
            OutputStream aceOutputStream = new FileOutputStream(outputDir.createNewFile(prefix+".ace"));
            OutputStream phdOutputStream = new FileOutputStream(outputDir.createNewFile(prefix+".phd"));
            OutputStream consensusOutputStream = new FileOutputStream(outputDir.createNewFile(prefix+".consensus.fasta"));
            PrintWriter countWriter= new PrintWriter(outputDir.createNewFile(prefix+".counts"));
            final ExecutorService executor = new ExceptionIntolerantFixedSizedThreadPoolExecutor(2);
            try {
                for(AceContig aceContig : ConsedUtil.split0xContig(adpatedCasContig, coverageMap)){
                    if(useClosureTrimming){
                        AceContig trimmedAceContig =closureContigTrimmer.trimContig(aceContig);
                        if(trimmedAceContig ==null){
                            System.out.printf("%s was completely trimmed... skipping%n", aceContig.getId());
                            continue;
                        }
                        aceContig =trimmedAceContig;
                    }
                   
                    List<Callable<Void>> writers = new ArrayList<Callable<Void>>();
                    writers.add(new PhdWriterCallable(aceContig, phdDataStore, phdOutputStream));
                    writers.add(new AceWriterCallable(aceContig, phdDataStore, aceOutputStream,consensusOutputStream));
                    for(Future<Void> futures :executor.invokeAll(writers)){
                        futures.get();              
                    }
                    numReads +=aceContig.getNumberOfReads();
                    numContigs++;
                   
                }
                //this way we only write the data if we finished successfully
                //hopefully this way we will crash later on 
                //when we try to sum up all the count files
                //and get an empty one.
                countWriter.printf("%d\t%d%n", numContigs, numReads);
                
            } catch (Exception e) {
                e.printStackTrace();
               System.exit(1);
            }finally{
                executor.shutdownNow();                 
                IOUtil.closeAndIgnoreErrors(phdOutputStream);
                IOUtil.closeAndIgnoreErrors(aceOutputStream,consensusOutputStream);
               
                IOUtil.closeAndIgnoreErrors(countWriter);
                IOUtil.closeAndIgnoreErrors(phdDataStore);
                IOUtil.closeAndIgnoreErrors(nucleotideDataStore);
                IOUtil.closeAndIgnoreErrors(aceQualityDatastore);
                IOUtil.closeAndIgnoreErrors(readIdLookup);
                IOUtil.closeAndIgnoreErrors(referenceIdLookup);
            }

    }catch (ParseException e) {
        System.exit(1);
    }

    }

}


