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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * {@code SffFileIterator} is a {@link StreamingIterator}
 * that can iterate over {@link SffFlowgram}s contained
 * in a sff file.
 * @author dkatzel
 *
 */
public final class SffFileIterator extends AbstractBlockingStreamingIterator<SffFlowgram>{

	private final File sffFile;
	private final DataStoreFilter filter;
	public static SffFileIterator createNewIteratorFor(File sffFile){
		return createNewIteratorFor(sffFile, DataStoreFilters.alwaysAccept());
	}
    public static SffFileIterator createNewIteratorFor(File sffFile, DataStoreFilter filter){
    	SffFileIterator iter;
			iter = new SffFileIterator(sffFile,filter);
			iter.start();
		
    	
    	return iter;
    }
	
	private SffFileIterator(File sffFile, DataStoreFilter filter){
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
						SffFileParserCallback callback, final SffReadHeader readHeader) {
					if(filter.accept(readHeader.getId())){
						return new SffFileReadVisitor() {
							
							@Override
							public void visitReadData(SffReadData readData) {
								SffFileIterator.this.blockingPut(SffFlowgramImpl.create(readHeader, readData));
								
							}
							
							@Override
							public void visitEnd() {
								//no-op
								
							}
						};
					}
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
