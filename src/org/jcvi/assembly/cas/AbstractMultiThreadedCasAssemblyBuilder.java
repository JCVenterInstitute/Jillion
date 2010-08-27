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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.IOUtils;
import org.jcvi.Builder;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasFileReadIndexToContigLookup;
import org.jcvi.command.Command;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdWriter;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;


/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractMultiThreadedCasAssemblyBuilder implements Builder<CasAssembly>{
    public static final String DEFAULT_PREFIX = "cas2consed";
    private final File casFile;
    private File tempDir;
    private CommandLine commandLine;
    /**
     * @param casFile
     */
    public AbstractMultiThreadedCasAssemblyBuilder(File casFile) {
        this.casFile = casFile;
    }
    
    public AbstractMultiThreadedCasAssemblyBuilder commandLine(CommandLine commandLine){
        this.commandLine = commandLine;
        return this;
    }

    public AbstractMultiThreadedCasAssemblyBuilder tempDir(File tempDir){
        this.tempDir = tempDir;
        return this;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CasAssembly build() {
        
        DefaultCasFileReadIndexToContigLookup read2contigMap = new DefaultCasFileReadIndexToContigLookup();
        try {
            CasParser.parseOnlyMetaData(casFile, read2contigMap);

            ReadWriteDirectoryFileServer consedOut = DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
            long startTime = DateTimeUtils.currentTimeMillis();
            int numberOfCasContigs = read2contigMap.getNumberOfContigs();
            for(long i=0; i< numberOfCasContigs; i++){
                File outputDir =consedOut.createNewDir(""+i);
                Command aCommand = new Command(new File("fakeCommand"));
               //build up command and call main method of single 
                //contig cas2consed reusing same arguments + reference id
                aCommand.setOption("-casId", ""+i);
                aCommand.setOption("-cas", commandLine.getOptionValue("cas"));
                aCommand.setOption("-o", outputDir.getAbsolutePath());
                aCommand.setOption("-tempDir", tempDir.getAbsolutePath());
                aCommand.setOption("-prefix", "temp");
                
                if(commandLine.hasOption("useIllumina")){
                    aCommand.addFlag("-useIllumina");
                }
                if(commandLine.hasOption("useClosureTrimming")){
                    aCommand.addFlag("-useClosureTrimming");
                }
                if(commandLine.hasOption("trim")){
                    aCommand.setOption("-trim", commandLine.getOptionValue("trim"));                    
                }
                if(commandLine.hasOption("trimMap")){
                    aCommand.setOption("-trimMap", commandLine.getOptionValue("trimMap")); 
                }
                //chromat_dir
                if(commandLine.hasOption("chromat_dir")){
                    aCommand.setOption("-chromat_dir", commandLine.getOptionValue("chromat_dir")); 
                }
                submitSingleCasAssemblyConversion(aCommand);
                
            }
            //wait till all contigs are done...
            waitForAllAssembliesToFinish();
            //here we have a fully written out all contigs map
            int numContigs=0;
            int numReads=0;
           
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
            consedOut.createNewDir("phd_dir");
            String prefix = commandLine.hasOption("prefix")?commandLine.getOptionValue("prefix")
                            : DEFAULT_PREFIX;
            OutputStream masterAceOut = new FileOutputStream (consedOut.createNewFile("edit_dir/"+prefix+".ace.1"));
            OutputStream masterPhdOut = new FileOutputStream (consedOut.createNewFile("phd_dir/"+prefix+".phd.ball"));
            OutputStream masterConsensusOut = new FileOutputStream (consedOut.createNewFile(prefix+".consensus.fasta"));
            OutputStream logOut = new FileOutputStream (consedOut.createNewFile(prefix+".log"));
            
            try{
                masterAceOut.write(String.format("AS %d %d%n", numContigs, numReads).getBytes());
                for(int i=0; i<numberOfCasContigs; i++){
                    InputStream aceIn = consedOut.getFileAsStream(i+"/temp.ace");
                    IOUtils.copy(aceIn, masterAceOut);
                    
                    InputStream phdIn = consedOut.getFileAsStream(i+"/temp.phd");
                    IOUtils.copy(phdIn, masterPhdOut);
                    //".consensus.fasta"
                    InputStream consensusIn = consedOut.getFileAsStream(i+"/temp.consensus.fasta");
                    IOUtils.copy(consensusIn, masterConsensusOut);
                    
                    IOUtil.closeAndIgnoreErrors(aceIn,phdIn,consensusIn);
                    //delete temp dirs
                    File tempDir = consedOut.getFile(i+"");
                    IOUtil.recursiveDelete(tempDir);
                }
                
                consedOut.createNewSymLink("../phd_dir/"+prefix+".phd.ball", 
                                    "edit_dir/phd.ball");
                long endTime = DateTimeUtils.currentTimeMillis();
                logOut.write(String.format("took %s%n",new Period(endTime- startTime)).getBytes());
                
            }finally{
                IOUtil.closeAndIgnoreErrors(masterAceOut,masterPhdOut,masterConsensusOut,logOut);
            }
        } catch (Exception e) {
            handleException(e);
        }finally{
            cleanup();
        }
        return null;
    }
    protected abstract void submitSingleCasAssemblyConversion(Command command) throws IOException;
    
    
    protected abstract void waitForAllAssembliesToFinish() throws Exception;
    
    protected abstract void cleanup();
    
    protected abstract void handleException(Exception e);
    
    public static class AceWriterCallable implements Callable<Void>{
        private final AceContig aceContig;
        private final PhdDataStore phdDataStore;
        private final OutputStream aceOutputStream;
        private final OutputStream consensusOutputStream;
        
        
        /**
         * @param aceContig
         * @param phdDataStore
         * @param phdOutputStream
         */
        public AceWriterCallable(AceContig aceContig, PhdDataStore phdDataStore,
                OutputStream phdOutputStream,OutputStream consensusOutputStream) {
            this.aceContig = aceContig;
            this.phdDataStore = phdDataStore;
            this.aceOutputStream = phdOutputStream;
            this.consensusOutputStream = consensusOutputStream;
        }


        @Override
        public Void call() throws IOException, DataStoreException{
            consensusOutputStream.write(new DefaultEncodedNucleotideFastaRecord(aceContig.getId(),
                    NucleotideGlyph.convertToString(NucleotideGlyph.convertToUngapped(aceContig.getConsensus().decode()))).toString().getBytes());
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
                final Phd phd = phdDataStore.get(id);
                if(phd ==null){
                    throw new NullPointerException("phd is null for "+id);
                }
                PhdWriter.writePhd(id, phd, phdOutputStream);
            }
            
            return null;
            
        }
    }

}
