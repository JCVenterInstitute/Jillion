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

package org.jcvi.jillion.core.internal.seq.fasta;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.internal.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;

/**
 * @author dkatzel
 *
 *
 */
public final class LargeFastaIdIterator extends AbstractBlockingStreamingIterator<String>{

    private final File fastaFile;
    private final DataStoreFilter filter;
    public static LargeFastaIdIterator createNewIteratorFor(File fastaFile, DataStoreFilter filter){
    	if(fastaFile ==null){
    		throw new NullPointerException("fasta file can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(fastaFile,filter);
		iter.start();
    	
    	return iter;
    }
    public static LargeFastaIdIterator createNewIteratorFor(File fastaFile){
    	return createNewIteratorFor(fastaFile, DataStoreFilters.alwaysAccept());
    }
	
    /**
     * @param fastaFile
     */
    private LargeFastaIdIterator(File fastaFile, DataStoreFilter filter) {
    	
        this.fastaFile = fastaFile;
        this.filter = filter;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
    	FastaFileVisitor visitor = new FastaFileVisitor() {
    		@Override
			public DeflineReturnCode visitDefline(String id, String comment) {
    			if(filter.accept(id)){
    				LargeFastaIdIterator.this.blockingPut(id);
    			}
				return DeflineReturnCode.SKIP_CURRENT_RECORD;
			}

			@Override
			public void visitLine(String line) {
				//no-op
			}

			@Override
			public void visitFile() {
				//no-op
			}

			@Override
			public void visitEndOfFile() {
				//no-op
			}

			
			@Override
			public void visitBodyLine(String bodyLine) {
				//no-op
			}

			@Override
			public EndOfBodyReturnCode visitEndOfBody() {
				return EndOfBodyReturnCode.KEEP_PARSING;
			}
            
            
        };
        try {
            FastaFileParser.parse(fastaFile, visitor);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
