/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqFileParser;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
import org.jcvi.jillion.trace.sff.SffCommonHeader;
import org.jcvi.jillion.trace.sff.SffFileParser;
import org.jcvi.jillion.trace.sff.SffFileParserCallback;
import org.jcvi.jillion.trace.sff.SffFileReadVisitor;
import org.jcvi.jillion.trace.sff.SffFileVisitor;
import org.jcvi.jillion.trace.sff.SffReadHeader;

public abstract class AbstractDefaultCasFileLookup  implements CasIdLookup, CasFileVisitor{

    private final Map<Long,String> readNameOrder = new HashMap<Long, String>();
    private final Map<String,Long> name2IdMap = new HashMap<String, Long>();
    private final Map<String, File> readNameToFile = new HashMap<String, File>();
    private final CasTrimMap trimToUntrimmedMap;
    private long readCounter=0;
    
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
                final File file;
                if(filePath.startsWith("/")){
                    file = new File(filePath);
                }else{
                    file= new File(workingDir,filePath);
                }
                final File filetoParse = trimToUntrimmedMap.getUntrimmedFileFor(file);
               
                files.add(filetoParse);
                parse(filetoParse);
            } catch (Exception e) {
               throw new IllegalStateException("could not load read file: "+ filePath,e);
            }
        }
    }
    private void parse(File file) throws IOException {
        String fileName = file.getName();
        if(fileName.endsWith("sff")){
          SffFileParser.create(file).accept(new SffReadOrder(file));                
        }
        else if(fileName.endsWith("fastq") || fileName.matches("\\S*\\.fastq\\S*")){
            FastqFileParser.create(file).accept(new FastqReadOrder(file));
        }
        else{
          //try as fasta...
        	FastaFileParser.create(file).accept(new FastaReadOrder(file));
           
        }
    }
    
    @Override
    public synchronized void visitAssemblyProgramInfo(String name, String version,
            String parameters) {
        checkNotYetInitialized();
        
    }

    @Override
    public synchronized void visitReferenceDescription(CasReferenceDescription description) {
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
    public synchronized void visitNumberOfReferenceFiles(long numberOfContigFiles) {
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
        return readNameOrder.get(casReadId);
    }

    @Override
    public synchronized long getCasIdFor(String lookupId) {
        checkIsInitialized();
        checkNotClosed();
        return name2IdMap.get(lookupId);
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
    
    private void addRead(String name, File source){
        name2IdMap.put(name, readCounter);
        readNameOrder.put(readCounter, name);
        readNameToFile.put(name, source);
        readCounter++;
    }
    
    private final class SffReadOrder implements SffFileVisitor{
        private final File file;
        SffReadOrder(File file){
            this.file =file;
        }
      
        
        @Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			//no-op
			
		}


		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				SffReadHeader readHeader) {
			final String name = readHeader.getId();
            addRead(name,file);
            //always skip underlying read data
			return null;
		}


		@Override
		public void end() {
			//no-op
			
		}
        
    }
    private final class FastaReadOrder implements FastaVisitor{
        private final File file;
        FastaReadOrder(File file){
            this.file =file;
        }
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			addRead(id,file);
			return null;
		}
		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted() {
			//no-op			
		}
    }
    private final class FastqReadOrder implements FastqVisitor{
        private final File file;
        FastqReadOrder(File file){
            this.file =file;
        }
        
		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			addRead(id,file);
			return null;
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted() {
			//no-op			
		}
    }

   
}
