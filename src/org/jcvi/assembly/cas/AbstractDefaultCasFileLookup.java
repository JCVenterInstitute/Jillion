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
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;
import org.jcvi.fasta.AbstractFastaVisitor;
import org.jcvi.fasta.FastaParser;
import org.jcvi.fasta.fastq.AbstractFastQFileVisitor;
import org.jcvi.fasta.fastq.FastQFileParser;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.AbstractSffFileVisitor;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;

public abstract class AbstractDefaultCasFileLookup  implements CasIdLookup, CasFileVisitor{

    private final List<String> readNameOrder = new ArrayList<String>();
    private final Map<String, File> readNameToFile = new HashMap<String, File>();
    private final CasTrimMap trimToUntrimmedMap;
    
    private final List<File> files = new ArrayList<File>();
    private boolean initialized = false;
    private boolean closed =false;
    private final File workingDir;
    /**
     * Create a new instance of {@link AbstractDefaultCasFileLookup}.
     * Since Cas files use relative paths to specify the read and reference
     * files, an optional workingDir parameter can be set.
     * @param trimToUntrimmedMap the {@link CasTrimMap} to use to perform
     * additional trimming to the reads.
     * @param workingDir the working directory to read relative paths from 
     * (can be null if should use current working directory).
     */
    public AbstractDefaultCasFileLookup(CasTrimMap trimToUntrimmedMap, File workingDir) {
        this.trimToUntrimmedMap = trimToUntrimmedMap;
        this.workingDir = workingDir;
    }
    /**
     * Create a new instance of {@link AbstractDefaultCasFileLookup} Same as
     * {@link #AbstractDefaultCasFileLookup(CasTrimMap,File) new AbstractDefaultCasFileLookup(trimToUntrimmedMap,null)}.
     * @param trimToUntrimmedMap the {@link CasTrimMap} to use to perform
     * additional trimming to the reads.
     * @param workingDir the working directory to read relative paths from 
     * (can be null if should use current working directory).
     * @see #AbstractDefaultCasFileLookup(CasTrimMap, File)
     */
    public AbstractDefaultCasFileLookup(CasTrimMap trimToUntrimmedMap) {
       this(trimToUntrimmedMap,null);
    }
    public AbstractDefaultCasFileLookup(){
        this((File)null);
    }
    public AbstractDefaultCasFileLookup(File workingDir){
        this(EmptyCasTrimMap.getInstance(),workingDir);
    }
    protected synchronized void checkNotYetInitialized(){
        if(initialized){
            throw new IllegalStateException("has already been initialized");
        }
    }
    @Override
    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public int getNumberOfIds() {
        return readNameOrder.size();
    }
    protected void loadFiles(CasFileInfo casFileInfo){
        for(String filePath: casFileInfo.getFileNames()){
            try {
                
                final File file = new File(workingDir,filePath);
                final File filetoParse = trimToUntrimmedMap.getUntrimmedFileFor(file);
               
                files.add(filetoParse);
                parse(filetoParse);
            } catch (Exception e) {
               throw new IllegalStateException("could not load read file: "+ filePath,e);
            }
        }
    }
    private void parse(File file) throws SFFDecoderException, IOException {
        String fileName = file.getName();
        FileInputStream in=null;
        try{
            if(fileName.endsWith("sff")){
                
                    in = new FileInputStream(file);
                    SffParser.parseSFF(in, new SffReadOrder(file));
                
            }
            else if(fileName.endsWith("fastq") || fileName.matches("\\S*\\.fastq\\S*")){
                in = new FileInputStream(file);
                FastQFileParser.parse(in, new FastQReadOrder(file));
            }
            else{
              //try as fasta...
                System.out.println(file.getAbsolutePath());
                in = new FileInputStream(file);
                FastaParser.parseFasta(in, new FastaReadOrder(file));
               
            }
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
        
        
            
    }
    
    @Override
    public synchronized void visitAssemblyProgramInfo(String name, String version,
            String parameters) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitContigDescription(CasContigDescription description) {
        checkNotYetInitialized();
        
    }
    

    @Override
    public synchronized void visitContigPair(CasContigPair contigPair) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitMatch(CasMatch match) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitMetaData(long numberOfContigSequences, long numberOfReads) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitNumberOfContigFiles(long numberOfContigFiles) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitNumberOfReadFiles(long numberOfReadFiles) {
        checkNotYetInitialized();
        
    }
    

    @Override
    public synchronized void visitScoringScheme(CasScoringScheme scheme) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
        initialized =true;
        
    }

    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
        
    }

    private void checkIsInitialized(){
        if(!initialized){
            throw new IllegalStateException("has NOT been initialized");
        }
    }
    private void checkNotClosed(){
        if(closed){
            throw new IllegalStateException("Lookup is closed");
        }
    }
    @Override
    public synchronized String getLookupIdFor(long casReadId) {
        checkIsInitialized();
        checkNotClosed();
        return readNameOrder.get((int)casReadId);
    }

    @Override
    public synchronized long getCasIdFor(String lookupId) {
        checkIsInitialized();
        checkNotClosed();
        return readNameOrder.indexOf(lookupId);
    }
    @Override
    public File getFileFor(long casReadId) {
        return getFileFor(getLookupIdFor(casReadId));
    }
    @Override
    public File getFileFor(String lookupId) {
        return readNameToFile.get(lookupId);
    }
    @Override
    public synchronized void close() throws IOException {
        readNameOrder.clear();
        readNameToFile.clear();
        closed=true;
    }
    
    private final class SffReadOrder extends AbstractSffFileVisitor{
        private final File file;
        SffReadOrder(File file){
            this.file =file;
        }
        @Override
        public boolean visitReadHeader(SFFReadHeader readHeader) {
            final String name = readHeader.getName();
            readNameOrder.add(name);
            readNameToFile.put(name, file);
            return true;
        }
        
    }
    private final class FastaReadOrder extends AbstractFastaVisitor{
        private final File file;
        FastaReadOrder(File file){
            this.file =file;
        }
        @Override
        public void visitRecord(String id, String comment, String entireBody) {
            readNameOrder.add(id);
            readNameToFile.put(id, file);
            
        }
        
    }
    private final class FastQReadOrder extends AbstractFastQFileVisitor<FastQRecord>{
        private final File file;
        FastQReadOrder(File file){
            this.file =file;
        }
        @Override
        public boolean visitBeginBlock(String id, String optionalComment) {
            readNameOrder.add(id);
            readNameToFile.put(id, file);
            return true;
        }
        
    }

   
}
