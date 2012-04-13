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

package org.jcvi.assembly.ace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.ace.ReAbacusAceContigWorker.AbacusFixerBuilder;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultReadAceTag;
import org.jcvi.common.core.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.common.core.assembly.ace.HiLowAceContigPhdDatastore;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.ReadAceTag;
import org.jcvi.common.core.assembly.ace.WholeAssemblyAceTag;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationParser;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationVisitor;
import org.jcvi.common.core.assembly.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.assembly.ace.consed.ReadNavigationElement;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class MultiThreadedReAbacusAce {
    private static final int DEFAULT_FLANK_LENGTH = 20;

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("nav", "consed navigation file input that says where the problems are to be fixed (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("num_threads", "number of threads to run simultaneously.  Each contig is run in a different thread (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to partial ace output file (required)")
        .isRequired(true)
        .longName("out")
        .build());
        options.addOption(new CommandLineOptionBuilder("muscle_max_mem", "number of MBs max that muscle is allowed " +
                "to allocate to perform abacus re-alignments default: "+ReAbacusAceContigWorker.MUSCLE_MAX_MEM_DEFAULT)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("flank", "number of bases on each side of the problem regions to include in the reabacus.  " +
                "We add flanking bases in order to improve the alignments.  " +
                "Default flank if not specified is "+ DEFAULT_FLANK_LENGTH)
        .build());
        options.addOption(CommandLineUtils.createHelpOption());     
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        OutputStream out=null;
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            File inputAceFile = new File(commandLine.getOptionValue("a"));
            File navigationFile = new File(commandLine.getOptionValue("nav"));
            Map<String, List<Range>> abacusErrorMap = parseAbacusErrorsFrom(navigationFile);
            File outputAceFile = new File(commandLine.getOptionValue("o"));
            
            out = new FileOutputStream(outputAceFile);
            int numberOfFlankingBases = commandLine.hasOption("flank")? 
                    Integer.parseInt(commandLine.getOptionValue("flank"))
                    : DEFAULT_FLANK_LENGTH;
                    
            int maxMuscleMem = commandLine.hasOption("muscle_max_mem")? 
                    Integer.parseInt(commandLine.getOptionValue("muscle_max_mem"))
                    : ReAbacusAceContigWorker.MUSCLE_MAX_MEM_DEFAULT;  
            int numberOfThreads = Integer.parseInt(commandLine.getOptionValue("num_threads"));
            
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            IndexedFileRange contigOffsets = new DefaultIndexedFileRange();
            TagWriter tagWriter = new TagWriter(out);
            //populate offsets
            AceContigDataStoreBuilder builder =IndexedAceFileDataStore.createBuilder(inputAceFile, contigOffsets);
            
            AceFileParser.parseAceFile(inputAceFile, 
                    MultipleWrapper.createMultipleWrapper(AceFileVisitor.class, builder,tagWriter));
            
            AceContigDataStore datastore = builder.build();
            CloseableIterator<String> ids = datastore.idIterator();
            List<Future<Void>> futures = new ArrayList<Future<Void>>();
            try{
	            while(ids.hasNext()){
	                String contigId = ids.next();
	                File tempOutputFile = new File(outputAceFile.getParentFile(), outputAceFile.getName()+".contig"+contigId);
	                if(abacusErrorMap.containsKey(contigId)){
	                    Callable<Void> callable = new SingleContigReAbacusWorker(inputAceFile, abacusErrorMap, contigId, tempOutputFile, numberOfFlankingBases,maxMuscleMem);
	                    futures.add(executor.submit(callable));
	                }else{
	                    Callable<Void> callable = new StreamContigWorker(inputAceFile, contigOffsets.getRangeFor(contigId), tempOutputFile);
	                    futures.add(executor.submit(callable));
	                }
	            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(ids);
            }
            boolean success=true;
            for(Future<Void> future : futures){
                try{
                future.get();
                }catch(ExecutionException e){
                    success=false;
                    e.printStackTrace();
                    executor.shutdownNow();
                }catch(Exception e){
                    success=false;
                    e.printStackTrace();
                    executor.shutdownNow();
                }
            }
            if(!success){
                System.err.println("failed to complete reabacus process check error logs for details");                
            }else{
                CloseableIterator<String> contigIdIter = datastore.idIterator();
                while(contigIdIter.hasNext()){
                    String contigId = contigIdIter.next();
                    File tempFile = new File(outputAceFile.getParentFile(), outputAceFile.getName()+".contig"+contigId);
                    InputStream in = new FileInputStream(tempFile);
                    IOUtil.copy(in, out);
                    IOUtil.closeAndIgnoreErrors(in);
                    tempFile.delete();
                }
                InputStream in = new ByteArrayInputStream(tagWriter.getTagOutputStream().toByteArray());
                IOUtil.copy(in, out);
                IOUtil.closeAndIgnoreErrors(in);
            }
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
        }

    }
   

/**
     * @param options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "multiThreadedReAbacusAce -a <ace file> -nav <input nav file> -o <ace out file> -num_threads <num>", 
                
                "Parse an ace file and input nav file with abacus problem regions and write out a new ace file " +
                "with those regions realigned and consensus recalled to try to correct the problem regions.",
                options,
               "Created by Danny Katzel"
                  );
        
    }
    private static class StreamContigWorker implements Callable<Void>{
        private final File inputAceFile;
        private final Range range;
        private final File outFile;
        
        
        public StreamContigWorker(File inputAceFile, Range range,
                File outFile) {
            this.inputAceFile = inputAceFile;
            this.range = range;
            this.outFile = outFile;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public Void call() throws Exception {
            OutputStream out = new FileOutputStream(outFile);
            InputStream inputStream = IOUtil.createInputStreamFromFile(inputAceFile,range);
            IOUtil.copy(inputStream, out);
            IOUtil.closeAndIgnoreErrors(inputStream,out);
            return null;
        }
        
        
    }

   private static class SingleContigReAbacusWorker implements Callable<Void>{
       private final File inputAceFile;
       private final Map<String, List<Range>> abacusErrorMap;
       private final String contigId;
       private final File outFile;
       private final int numberOfFlankingBases;
       private final int maxMuscleMem;
        SingleContigReAbacusWorker(File inputAceFile,
            Map<String, List<Range>> abacusErrorMap, String contigId,
            File outFile, int numberOfFlankingBases, int maxMuscleMem){
            this.inputAceFile = inputAceFile;
            this.abacusErrorMap = abacusErrorMap;
            this.contigId = contigId;
            this.outFile = outFile;
            this.numberOfFlankingBases = numberOfFlankingBases;
            this.maxMuscleMem = maxMuscleMem;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Void call() throws IOException{
            PhdDataStore hilowPhdDataStore = HiLowAceContigPhdDatastore.create(inputAceFile, contigId);   
            OutputStream out = new FileOutputStream(outFile);
            AbacusFixerBuilder contigFixer = new AbacusFixerBuilder(abacusErrorMap,contigId, numberOfFlankingBases, out, hilowPhdDataStore,maxMuscleMem);
            
            AceFileParser.parseAceFile(inputAceFile, contigFixer);
            return null;
        }
        
    }

   private static class TagWriter extends AbstractAceFileVisitor{
       private final ByteArrayOutputStream tagOutputStream = new ByteArrayOutputStream();
       private DefaultConsensusAceTag.Builder consensusTagBuilder;
       private final OutputStream aceOut;
       
       
       
       public TagWriter(OutputStream aceOut) {
        this.aceOut = aceOut;
    }

    /**
        * @return the tagOut
        */
       public ByteArrayOutputStream getTagOutputStream() {
           return tagOutputStream;
       }

       /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs,
                int totalNumberOfReads) {
           //header will be the same because we aren't changing the
           //number of reads or contigs
            try {
               AceFileWriter.writeAceFileHeader(numberOfContigs, totalNumberOfReads, aceOut);
           } catch (IOException e) {
               throw new IllegalStateException("error writing out new ace header",e);
           }
            
        }
        @Override
        public void visitReadTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            super.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
            ReadAceTag tag =new DefaultReadAceTag(id, type, creator, creationDate, 
                    Range.create(gappedStart,gappedEnd), isTransient);
            
            try {
               AceFileWriter.writeReadTag(tag, tagOutputStream);
           } catch (IOException e) {
               throw new IllegalStateException("error writing out new ace read tag",e);
           }

        }
        
        @Override
        public void visitWholeAssemblyTag(String type, String creator,
                Date creationDate, String data) {
            super.visitWholeAssemblyTag(type, creator, creationDate, data);
            WholeAssemblyAceTag tag = new DefaultWholeAssemblyAceTag(type, creator, creationDate, data);
            try {
                AceFileWriter.writeWholeAssemblyTag(tag, tagOutputStream);
            } catch (IOException e) {
                throw new IllegalStateException("error writing out new ace whole assembly tag",e);
            }
        }
        
        @Override
        public synchronized void visitBeginConsensusTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            super.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
            consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                    type, creator, creationDate, Range.create(gappedStart, gappedEnd), isTransient);

        }
        @Override
        public void visitConsensusTagComment(String comment) {
            super.visitConsensusTagComment(comment);
            consensusTagBuilder.addComment(comment);

        }

        @Override
        public void visitConsensusTagData(String data) {
            super.visitConsensusTagData(data);
            consensusTagBuilder.appendData(data);

        }

       

        @Override
        public void visitEndConsensusTag() {
            super.visitEndConsensusTag();
            ConsensusAceTag tag = consensusTagBuilder.build();
            try {
                AceFileWriter.writeConsensusTag(tag, tagOutputStream);
            } catch (IOException e) {
                throw new IllegalStateException("error writing out new ace consensus tag",e);
            }

        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitNewContig(String contigId, NucleotideSequence consensus,
                int numberOfBases, int numberOfReads, boolean isComplimented) {
            //no-op
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitAceRead(String readId, NucleotideSequence validBasecalls,
                int offset, Direction dir, Range validRange, PhdInfo phdInfo,
                int ungappedFullLength) {
            //no-op
            
        }
   }
/**
 * @param navigationFile
 * @return
 * @throws IOException 
 */
private static Map<String, List<Range>> parseAbacusErrorsFrom(
        File navigationFile) throws IOException {
    final Map<String, List<Range>> map = new HashMap<String, List<Range>>();
    ConsedNavigationVisitor visitor = new ConsedNavigationVisitor(){

        @Override
        public void visitLine(String line) {}

        @Override
        public void visitFile() {}

        @Override
        public void visitEndOfFile() {}

        @Override
        public void visitTitle(String title) {}

        @Override
        public void visitElement(ReadNavigationElement readElement) {}

        @Override
        public void visitElement(ConsensusNavigationElement consensusElement) {
           if("CA abacus error".equals(consensusElement.getComment())){
               String contigId =consensusElement.getTargetId();
               Range range = consensusElement.getUngappedPositionRange();
               if(!map.containsKey(contigId)){
                   map.put(contigId, new ArrayList<Range>());
               }
               map.get(contigId).add(range);
           }
            
        }
        
    };
    ConsedNavigationParser.parse(navigationFile, visitor);
    return map;
}
}
