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
package org.jcvi.jillion.internal.fasta.aa;

import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractProteinFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class LargeProteinFastaIterator extends AbstractBlockingStreamingIterator<ProteinFastaRecord>{

	private final FastaParser parser;
	private final Predicate<String> filter;
	private final  Predicate<ProteinFastaRecord> recordFilter;
	
	public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser){
		return createNewIteratorFor(parser, DataStoreFilters.alwaysAccept(),null);
	}
	 public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter){
		 LargeProteinFastaIterator iter = new LargeProteinFastaIterator(parser, filter, recordFilter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeProteinFastaIterator(FastaParser parser,Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter){
		 this.parser = parser;
		 this.filter = filter;
		 this.recordFilter = recordFilter;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	    	FastaVisitor visitor = new FastaVisitor(){

				@Override
				public FastaRecordVisitor visitDefline(
						final FastaVisitorCallback callback, String id,
						String optionalComment) {
					if(!filter.test(id)){
						return null;
					}
					
					return new AbstractProteinFastaRecordVisitor(id, optionalComment) {
						
						@Override
						protected void visitRecord(ProteinFastaRecord fastaRecord) {
						    if(recordFilter ==null || recordFilter.test(fastaRecord)){
							blockingPut(fastaRecord);
							if(LargeProteinFastaIterator.this.isClosed()){
								callback.haltParsing();
							}
						    }
						}
					};
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
				throw new RuntimeException("can not parse fasta file",e);
			}
	    }
}
