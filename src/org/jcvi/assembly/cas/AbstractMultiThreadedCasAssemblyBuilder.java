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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jcvi.Builder;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigAdapter;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.ace.consed.closure.NextGenClosureAceContigTrimmer;
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
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadOnlyDirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jcvi.trace.TraceQualityDataStoreAdapter;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.trace.sanger.FileSangerTrace;
import org.jcvi.trace.sanger.SangerFileDataStore;
import org.jcvi.trace.sanger.SingleSangerTraceDirectoryFileDataStore;
import org.jcvi.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdSangerTraceDataStoreAdapter;
import org.jcvi.trace.sanger.phd.PhdWriter;
import org.jcvi.util.MultipleWrapper;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * @author dkatzel
 *
 *
 */
public class AbstractMultiThreadedCasAssemblyBuilder implements Builder<CasAssembly>{
    private static final String DEFAULT_PREFIX = "cas2consed";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    
    private final File casFile;
    private boolean useIllumina =false;
    private boolean useClosureTrimming = false;
    private CasTrimMap trimToUntrimmedMap;
    private TrimDataStore trimDataStore = TrimDataStoreUtil.EMPTY_DATASTORE;
    private File tempDir;
    private static final int DEFAULT_FASTA_CACHE_SIZE = 100;
    private File chromatogramDir;
    private CommandLine commandLine;
    /**
     * @param casFile
     */
    public AbstractMultiThreadedCasAssemblyBuilder(File casFile) {
        this.casFile = casFile;
    }

    public AbstractMultiThreadedCasAssemblyBuilder useClosureTrimming(boolean useClosureTrimming){
        this.useClosureTrimming = useClosureTrimming;
        return this;
    }
    public AbstractMultiThreadedCasAssemblyBuilder useIllumina(boolean useIllumina){
        this.useIllumina = useIllumina;
        return this;
    }
    
    public AbstractMultiThreadedCasAssemblyBuilder commandLine(CommandLine commandLine){
        this.commandLine = commandLine;
        return this;
    }
    
    
    public AbstractMultiThreadedCasAssemblyBuilder trimToUntrimmedMap(CasTrimMap trimToUntrimmedMap){
        this.trimToUntrimmedMap = trimToUntrimmedMap;
        return this;
    }
    
    public AbstractMultiThreadedCasAssemblyBuilder trimDataStore(TrimDataStore trimDataStore){
        this.trimDataStore = trimDataStore;
        return this;
    }
    public AbstractMultiThreadedCasAssemblyBuilder tempDir(File tempDir){
        this.tempDir = tempDir;
        return this;
    }
    public AbstractMultiThreadedCasAssemblyBuilder chromatDir(File chromatogramDir){
        this.chromatogramDir = chromatogramDir;
        return this;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CasAssembly build() {
        Date phdDate = new Date(System.currentTimeMillis());
        NextGenClosureAceContigTrimmer closureContigTrimmer=null;
        if(useClosureTrimming){
            closureContigTrimmer= new NextGenClosureAceContigTrimmer(5, 5, 10);
        }
        
        DefaultCasFileReadIndexToContigLookup read2contigMap = new DefaultCasFileReadIndexToContigLookup();
        try {
            CasParser.parseOnlyMetaData(casFile, read2contigMap);

            
            int numberOfCasContigs = read2contigMap.getNumberOfContigs();
            for(long i=0; i< numberOfCasContigs; i++){
                ReadWriteFileServer aceOut = DirectoryFileServer.createReadWriteDirectoryFileServer(new File(tempDir, ""+i));
                //build up command and call main method of single 
                //contig cas2consed reusing same arguments + reference id
                
                
            }
            //wait till all contigs are done...
            
            //here we have a fully written out all contigs map
            int numContigs=0;
            int numReads=0;
            ReadWriteDirectoryFileServer consedOut = DirectoryFileServer.createReadWriteDirectoryFileServer(tempDir);
            
            for(int i=0; i<numberOfCasContigs; i++){
                File countMap = consedOut.getFile(i+"/temp.counts");
                Scanner scanner = new Scanner(countMap);
                numContigs +=scanner.nextInt();
                numReads +=scanner.nextInt();
                scanner.close();
            }
            System.out.println("num contigs ="+ numContigs);
            System.out.println("num reads ="+ numReads);
            consedOut.createNewDir("edit_dir");
            consedOut.createNewDir("phdball_dir");
            OutputStream masterAceOut = new FileOutputStream (consedOut.createNewFile("edit_dir/cas2consed.ace.1"));
            OutputStream masterPhdOut = new FileOutputStream (consedOut.createNewFile("phdball_dir/cas2consed.phd.ball"));
            masterAceOut.write(String.format("AS %d %d%n", numContigs, numReads).getBytes());
            for(int i=0; i<numberOfCasContigs; i++){
                InputStream aceIn = consedOut.getFileAsStream(i+"/temp.ace");
                IOUtils.copy(aceIn, masterAceOut);
                
                InputStream phdIn = consedOut.getFileAsStream(i+"/temp.phd");
                IOUtils.copy(phdIn, masterPhdOut);
                
                IOUtil.closeAndIgnoreErrors(aceIn);
                IOUtil.closeAndIgnoreErrors(phdIn);
                
            }
            IOUtil.closeAndIgnoreErrors(masterAceOut);
            IOUtil.closeAndIgnoreErrors(masterPhdOut);
            consedOut.createNewSymLink("../phdball_dir/cas2consed.phd.ball", 
                                "edit_dir/phd.ball");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public static class AceWriterCallable implements Callable<Void>{
        private final AceContig aceContig;
        private final PhdDataStore phdDataStore;
        private final OutputStream aceOutputStream;
        
        
        /**
         * @param aceContig
         * @param phdDataStore
         * @param phdOutputStream
         */
        public AceWriterCallable(AceContig aceContig, PhdDataStore phdDataStore,
                OutputStream phdOutputStream) {
            this.aceContig = aceContig;
            this.phdDataStore = phdDataStore;
            this.aceOutputStream = phdOutputStream;
        }


        @Override
        public Void call() throws IOException, DataStoreException{
            AceFileWriter.writeAceFile(aceContig, phdDataStore, aceOutputStream);
            return null;
            
        }
    }
    public static class PhdWriterCallable implements Callable<Void>{
        private final AceContig aceContig;
        private final PhdDataStore phdDataStore;
        private final OutputStream phdOutputStream;
        
        
        /**
         * @param aceContig
         * @param phdDataStore
         * @param phdOutputStream
         */
        public PhdWriterCallable(AceContig aceContig, PhdDataStore phdDataStore,
                OutputStream phdOutputStream) {
            this.aceContig = aceContig;
            this.phdDataStore = phdDataStore;
            this.phdOutputStream = phdOutputStream;
        }


        @Override
        public Void call() throws IOException, DataStoreException{
          //only write phds that make it into the assembly
            for(AcePlacedRead read : aceContig.getPlacedReads()){
                String id = read.getId();
                PhdWriter.writePhd(id, phdDataStore.get(id), phdOutputStream);
            }
            
            return null;
            
        }
    }
    public static void main(String[] args) throws FileNotFoundException, ParseException{
        
        
        
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
            
       // File casFile = new File("/usr/local/projects/VHTNGS/sample_data_new/giv3/MCE/30209/mapping/giv3_MCE_30209_hybrid_edited_refs.cas");
        File casFile = new File(commandLine.getOptionValue("cas"));
        AbstractMultiThreadedCasAssemblyBuilder builder = new AbstractMultiThreadedCasAssemblyBuilder(casFile);
        builder.commandLine(commandLine);
        
        long start =System.currentTimeMillis();
        builder.build();
        long end =System.currentTimeMillis();
        
        System.out.println(new Period(end-start));
    
    }catch(ParseException e){
        throw e;
    }
    
    }
}
