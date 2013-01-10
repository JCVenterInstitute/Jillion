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

package org.jcvi.common.core.seq.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.internal.util.iter.AbstractBlockingStreamingIterator;

/**
 * {@code SffFileIterator} is a {@link StreamingIterator}
 * that can iterate over {@link Flowgram}s contained
 * in a sff file.
 * @author dkatzel
 *
 */
public final class SffFileIterator extends AbstractBlockingStreamingIterator<Flowgram>{

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
         		private SffReadHeader currentReadHeader;
         		@Override
         		public void visitFile() {
         			//no-op
         		}

         		
         		@Override
         		public void visitEndOfFile() {
         			//no-op
         		}

         		@Override
         		public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
         			return CommonHeaderReturnCode.PARSE_READS;
         		}

         		@Override
         		public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
         			if(filter.accept(readHeader.getId())){
	         			this.currentReadHeader = readHeader;
	         			return ReadHeaderReturnCode.PARSE_READ_DATA;
         			}
         			return ReadHeaderReturnCode.SKIP_CURRENT_READ;
         		}

         		@Override
         		public ReadDataReturnCode visitReadData(SffReadData readData) {
         			SffFileIterator.this.blockingPut(SffFlowgram.create(currentReadHeader, readData));
         			return ReadDataReturnCode.PARSE_NEXT_READ;
         		}
         	};
             SffFileParser.parse(sffFile, visitor);
         } catch (IOException e) {
             //should never happen
             throw new RuntimeException(e);
         }
		
	}
	
	

}
