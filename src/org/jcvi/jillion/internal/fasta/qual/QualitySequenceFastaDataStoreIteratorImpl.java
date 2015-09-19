/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta.qual;

import java.io.IOException;
import java.util.function.Predicate;

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
	private final Predicate<String> filter;
	private final Predicate<QualityFastaRecord> recordFilter;
	
	public static StreamingIterator<QualityFastaRecord> createIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter){
		
		QualitySequenceFastaDataStoreIteratorImpl iter = new QualitySequenceFastaDataStoreIteratorImpl(parser, filter, recordFilter);
		iter.start();
		return iter;
	}
	
	public QualitySequenceFastaDataStoreIteratorImpl( FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) {
		if(parser ==null){
			throw new NullPointerException("parser can not be null");
		}
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		this.parser = parser;
		this.filter =filter;
		this.recordFilter = recordFilter;
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
				if(recordFilter ==null || recordFilter.test(record)){
				    blockingPut(record);
				}
				
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
				if(!filter.test(id)){
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
