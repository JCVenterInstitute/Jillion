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
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.DecodingOptions;
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
	private final BiFunction<String,String, Defline> idConverter;

	private final DecodingOptions decodingOptions;

	public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser){
		return createNewIteratorFor(parser, DataStoreFilters.alwaysAccept(),null);
	}
	public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter){
		return createNewIteratorFor(parser, filter, recordFilter, null);
	}
	public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter, BiFunction<String,String, Defline> idConverter){
		return createNewIteratorFor(parser, filter, recordFilter, idConverter, null);
	}

	public static LargeProteinFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter, BiFunction<String,String, Defline> idConverter, DecodingOptions decodingOptions){
		LargeProteinFastaIterator iter = new LargeProteinFastaIterator(parser, filter, recordFilter,idConverter, decodingOptions);
		iter.start();

		return iter;
	}
	 
	 private LargeProteinFastaIterator(FastaParser parser,Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter, BiFunction<String,String, Defline> idConverter, DecodingOptions decodingOptions){
		 this.parser = parser;
		 this.filter = filter;
		 this.recordFilter = recordFilter;
		 this.idConverter = idConverter==null? Defline::of: idConverter;
		 this.decodingOptions = decodingOptions==null? DecodingOptions.DEFAULT: decodingOptions;
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
					Defline defline = idConverter.apply(id, optionalComment);
					if(!filter.test(defline.getId())){
						return null;
					}
					
					return new AbstractProteinFastaRecordVisitor(defline.getId(), defline.getComment(), true, decodingOptions) {
						
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
