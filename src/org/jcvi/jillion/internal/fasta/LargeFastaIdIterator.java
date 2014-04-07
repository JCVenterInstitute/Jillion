/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.internal.fasta;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class LargeFastaIdIterator extends AbstractBlockingStreamingIterator<String>{

    private final FastaParser parser;
    private final DataStoreFilter filter;
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser, DataStoreFilter filter){
    	if(parser ==null){
    		throw new NullPointerException("fasta file can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(parser,filter);
		iter.start();
    	
    	return iter;
    }
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser){
    	return createNewIteratorFor(parser, DataStoreFilters.alwaysAccept());
    }
	
    /**
     * @param fastaFile
     */
    private LargeFastaIdIterator(FastaParser parser, DataStoreFilter filter) {
    	
        this.parser = parser;
        this.filter = filter;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
    	FastaVisitor visitor = new FastaVisitor() {

			@Override
			public FastaRecordVisitor visitDefline(
					FastaVisitorCallback callback, String id,
					String optionalComment) {
				if(filter.accept(id)){
    				LargeFastaIdIterator.this.blockingPut(id);
    			}
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
        };
        try {
        	parser.parse(visitor);
        } catch (IOException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
