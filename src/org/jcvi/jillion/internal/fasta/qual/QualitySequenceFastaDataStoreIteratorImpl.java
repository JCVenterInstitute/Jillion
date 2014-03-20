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
package org.jcvi.jillion.internal.fasta.qual;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.jcvi.jillion.fasta.qual.QualityFastaRecordBuilder;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractResuseableFastaRecordVisitor;

public class QualitySequenceFastaDataStoreIteratorImpl extends AbstractBlockingStreamingIterator<QualityFastaRecord>{
	
	private final FastaParser parser;
	private final DataStoreFilter filter;
	
	
	public static StreamingIterator<QualityFastaRecord> createIteratorFor(FastaParser parser, DataStoreFilter filter){
		
		QualitySequenceFastaDataStoreIteratorImpl iter = new QualitySequenceFastaDataStoreIteratorImpl(parser, filter);
		iter.start();
		return iter;
	}
	
	public QualitySequenceFastaDataStoreIteratorImpl( FastaParser parser, DataStoreFilter filter) {
		if(parser ==null){
			throw new NullPointerException("parser can not be null");
		}
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		this.parser = parser;
		this.filter =filter;
	}
	/**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
    	
    	final AbstractResuseableFastaRecordVisitor recordVisitor = new AbstractResuseableFastaRecordVisitor(){

			@Override
			public void visitRecord(String id, String optionalComment,
					String fullBody) {
				QualityFastaRecord record = new QualityFastaRecordBuilder(id,fullBody)
														.comment(optionalComment)
														.build();
				blockingPut(record);
				
			}

		
    		
    	};
        FastaVisitor visitor = new FastaVisitor() {
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			@Override
			public void halted() {
				//no-op					
			}
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(!filter.accept(id)){
					return null;
				}
				recordVisitor.prepareNewRecord(id, optionalComment);
				return recordVisitor;
			}
		};
        try {
            parser.parse(visitor);
        } catch (IOException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }

    }
    
   
    
}
