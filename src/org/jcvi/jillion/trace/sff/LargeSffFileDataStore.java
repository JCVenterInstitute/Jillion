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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 28, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.AbstractDataStore;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
/**
 * {@code LargeSffFileDataStore} is a {@link SffFileDataStore}
 * implementation that doesn't store any read information in memory.
 *  No data contained in this
 * sff file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the sff file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 */
final class LargeSffFileDataStore extends AbstractDataStore<SffFlowgram> implements SffFileDataStore{

    private final File sffFile;
    private Long size=null;
    private final DataStoreFilter filter;
    private final NucleotideSequence keySequence,flowSequence;
    /**
     * Create a new instance of {@link LargeSffFileDataStore}.
     * @param sffFile the sff file to parse.
     * @return a new SffDataStore; never null.
     * @throws NullPointerException if sffFile is null.
     * @throws IOException if sffFile does not exist or is not a valid sff file.
     */
    public static SffFileDataStore create(File sffFile) throws IOException{
    	return create(sffFile, DataStoreFilters.alwaysAccept());
    }
    
    /**
     * Create a new instance of {@link LargeSffFileDataStore}.
     * @param sffFile the sff file to parse.
     * @return a new SffDataStore; never null.
     * @throws NullPointerException if sffFile is null.
     * @throws IOException if sffFile does not exist or is not a valid sff file.
     */
    public static SffFileDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
    	verifyFileExists(sffFile);
    	verifyIsValidSff(sffFile);
    	
    	return new LargeSffFileDataStore(sffFile,filter);
    }
	private static void verifyFileExists(File sffFile)
			throws FileNotFoundException {
		if(sffFile ==null){
    		throw new NullPointerException("file can not be null");
    	}
    	if(!sffFile.exists()){
    		throw new FileNotFoundException("sff file does not exist");
    	}
	}
    private static void verifyIsValidSff(File f) throws IOException {
    	DataInputStream in=null;
    	try{
    		in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
    		//don't care about return value
    		//this will throw IOException if the file isn't valid
    		DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(in);
    	}finally{
    		IOUtil.closeAndIgnoreErrors(in);
    	}
		
	}
	/**
     * @param sffFile
	 * @throws IOException 
	 * @throws FileNotFoundException 
     */
    private LargeSffFileDataStore(File sffFile, DataStoreFilter filter) throws FileNotFoundException, IOException {
        this.sffFile = sffFile;
        this.filter = filter;
        HeaderVisitor visitor = new HeaderVisitor();
        SffFileParser.create(sffFile).accept(visitor);
        SffCommonHeader header = visitor.getHeader();
        if(header ==null){
        	throw new IOException("could not parse sff header");
        }
        this.keySequence = header.getKeySequence();
        this.flowSequence = header.getFlowSequence();
        
    }

    @Override
	public NucleotideSequence getKeySequence() {
		return keySequence;
	}

	@Override
	public NucleotideSequence getFlowSequence() {
		return flowSequence;
	}

	@Override
	protected boolean containsImpl(String id) throws DataStoreException {
		return get(id)!=null;
	}
	@Override
	protected SffFlowgram getImpl(String id) throws DataStoreException {
		if(!filter.accept(id)){
			return null;
		}
		try{
        	SingleFlowgramVisitor singleVisitor = new SingleFlowgramVisitor(id);
        	SffFileParser.create(sffFile).accept(singleVisitor);
        	return singleVisitor.getFlowgram();
        } catch (IOException e) {
            throw new DataStoreException("could not read sffFile ",e);
        }
	}
	@Override
	protected synchronized long getNumberOfRecordsImpl() throws DataStoreException {
		if(this.size ==null){
			//since filter could mean we won't
			//accept everything we can't just
			//look at the value in the common header
			//must iterate through everything
			StreamingIterator<SffFlowgram> iter=null;
			
        	
        	try{
        		iter = iterator();
        		long count=0;
        		while(iter.hasNext()){
        			iter.next();
        			count++;
        		}
        		size = count;
        	}catch(Exception e){
        		 throw new DataStoreException("could not parse sffFile ",e);
        	}finally{
        		IOUtil.closeAndIgnoreErrors(iter);
        	}
        }
        return size;
	}
	@Override
	protected StreamingIterator<String> idIteratorImpl()
			throws DataStoreException {
		SffFileIdIterator iter = SffFileIdIterator.createNewIteratorFor(sffFile,filter);
		return DataStoreStreamingIterator.create(this, iter);
	}
	@Override
	protected StreamingIterator<SffFlowgram> iteratorImpl()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this,
				SffFileIterator.createNewIteratorFor(sffFile,filter));
	}
	
   

   

    @Override
	protected void handleClose() throws IOException {
		//no-op
		
	}
	
    private static final class SffFileIdIterator extends AbstractBlockingStreamingIterator<String>{

    	private final File sffFile;
    	private final DataStoreFilter filter;

        public static SffFileIdIterator createNewIteratorFor(File sffFile, DataStoreFilter filter){
        	SffFileIdIterator iter = new SffFileIdIterator(sffFile,filter);
			iter.start();
    		
        	
        	return iter;
        }
    	
    	private SffFileIdIterator(File sffFile, DataStoreFilter filter){
    		this.sffFile = sffFile;
    		 this.filter =filter;
    	}

    	@Override
    	protected void backgroundThreadRunMethod() {
    		 try {
             	SffFileVisitor visitor = new SffFileVisitor() {
             		

             		@Override
					public void visitHeader(SffFileParserCallback callback,
							SffCommonHeader header) {
						//no-op						
					}

					@Override
					public SffFileReadVisitor visitRead(
							SffFileParserCallback callback,
							SffReadHeader readHeader) {
						String readId = readHeader.getId();
						if(filter.accept(readId)){
							SffFileIdIterator.this.blockingPut(readId);
						}
						//always skip underlying read data
						return null;
					}

					@Override
					public void end() {
						//no-op
					}

					
             	};
                 SffFileParser.create(sffFile).accept(visitor);
             } catch (IOException e) {
                 //should never happen
                 throw new RuntimeException(e);
             }
    		
    	}
    	
    	

    }
    
    
    private static final class SingleFlowgramVisitor implements SffFileVisitor{
        private final String idToFind;
        private SffFlowgram flowgram=null;
        private SingleFlowgramVisitor(String idToFind) {
			this.idToFind = idToFind;
		}

		public SffFlowgram getFlowgram() {
			return flowgram;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			//no-op			
		}

		@Override
		public SffFileReadVisitor visitRead(final SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			if(readHeader.getId().equals(idToFind)){
				return new SffFileReadVisitor() {
					
					@Override
					public void visitReadData(SffReadData readData) {
						flowgram = SffFlowgramImpl.create(readHeader, readData);
						
					}
					
					@Override
					public void visitEnd() {
						callback.stopParsing();
						
					}
				};
			}
			return null;
		}

		@Override
		public void end() {
			//no-op			
		}
    }
    
   private static final class HeaderVisitor implements SffFileVisitor {
		private SffCommonHeader header;
		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				SffReadHeader readHeader) {
			//skip
			return null;
		}
		
		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			this.header = header;
			callback.stopParsing();
			
		}
		
		@Override
		public void end() {
			//no-op
			
		}

		public final SffCommonHeader getHeader() {
			return header;
		}
		
		
	}
    
}
