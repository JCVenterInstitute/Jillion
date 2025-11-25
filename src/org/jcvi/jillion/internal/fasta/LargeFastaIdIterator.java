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
package org.jcvi.jillion.internal.fasta;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * 
 * @author dkatzel
 *
 *
 */
public final class LargeFastaIdIterator extends AbstractBlockingStreamingIterator<String>{

    private final FastaParser parser;
    private final Predicate<String> filter;
    private final Long maxNumberOfIds;
    private final BiFunction<String,String, Defline> idConverter;
   
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Long maxNumberofIds, BiFunction<String,String, Defline> idConverter){
    	if(parser ==null){
    		throw new NullPointerException("fasta file can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(parser,filter, maxNumberofIds, idConverter);
		iter.start();
    	
    	return iter;
    }

    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser){
        return createNewIteratorFor(parser, null);
    }
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser,  BiFunction<String,String, Defline> idConverter){
    	return createNewIteratorFor(parser, DataStoreFilters.alwaysAccept(), null, null);
    }
	
    /**
     * @param parser
     */
    private LargeFastaIdIterator(FastaParser parser, Predicate<String> filter, Long maxNumberOfIds, BiFunction<String,String, Defline> idConverter ) {
    	super(10_000);// these are just ids so we can buffer a lot of them
        this.parser = parser;
        this.filter = filter;
        this.maxNumberOfIds = maxNumberOfIds;
        this.idConverter = idConverter==null? Defline::of: idConverter;
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
                            Defline convertedId = idConverter.apply(id, optionalComment);
                            if (convertedId !=null && filter.test(convertedId.getId())) {
                                LargeFastaIdIterator.this.blockingPut(convertedId.getId());
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
        if(maxNumberOfIds !=null) {
        	visitor = new MaxNumberOfRecordsFastaVisitor(maxNumberOfIds, visitor);
        }
        try {
        	parser.parse(visitor);
        } catch (IOException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        
    }

}
