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
import java.util.function.Predicate;

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
    
   
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Long maxNumberofIds){
    	if(parser ==null){
    		throw new NullPointerException("fasta file can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	LargeFastaIdIterator iter= new LargeFastaIdIterator(parser,filter, maxNumberofIds);
		iter.start();
    	
    	return iter;
    }
    
    public static LargeFastaIdIterator createNewIteratorFor(FastaParser parser){
    	return createNewIteratorFor(parser, DataStoreFilters.alwaysAccept(), null);
    }
	
    /**
     * @param fastaFile
     */
    private LargeFastaIdIterator(FastaParser parser, Predicate<String> filter, Long maxNumberOfIds) {
    	super(10_000);// these are just ids so we can buffer a lot of them
        this.parser = parser;
        this.filter = filter;
        this.maxNumberOfIds = maxNumberOfIds;
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
                            if (filter.test(id)) {
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
