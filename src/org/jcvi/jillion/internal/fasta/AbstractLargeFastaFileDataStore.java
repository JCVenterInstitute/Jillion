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
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.Sneak;
public abstract class AbstractLargeFastaFileDataStore<T,S extends Sequence<T>, F extends FastaRecord<T, S>, D extends DataStore<S>> implements FastaDataStore<T,S,F,D>{

    
    private final FastaParser parser;
    private final Predicate<String> filter;
    private final Predicate<F> recordFilter;
    private Long size;
    private volatile boolean closed=false;
    private final Long maxNumberOfRecords;
    
    /**
     * Construct a {@link AbstractLargeFastaFileDataStore} using
     * the given fasta file and filter.
     * @param parser the FastaParser to use, can not be null.
     * @param filter an id filter; can not be null.
     * @param recordFilter filter out records some other way; may be null to mean don't filter anything.
     * @param maxNumberOfRecords an {@link OptionalLong} of the maximum number of IDs will be included by the id filter.
     * 
     * @throws NullPointerException if fastaFile is null.
     */
    protected AbstractLargeFastaFileDataStore(FastaParser parser, Predicate<String> filter, 
    		Predicate<F> recordFilter, OptionalLong maxNumberOfRecords) {
        if(parser ==null){
            throw new NullPointerException("fasta parser can not be null");
        }
        if(filter ==null){
            throw new NullPointerException("filter file can not be null");
        }
        this.filter =filter;
        this.parser = parser;
        this.recordFilter = recordFilter;
        this.maxNumberOfRecords = maxNumberOfRecords.isEmpty()? null: maxNumberOfRecords.getAsLong();
    }
    
    private void checkNotYetClosed(){
        if(closed){
            throw new DataStoreClosedException("already closed");
        }
    }

    protected Predicate<String> getIdFilter(){
    	return filter==null? (s)->true: filter;
    }
    protected Predicate<F> getRecordFilter(){
    	return recordFilter==null? (f)->true: recordFilter;
    }
    
    protected FastaParser getFastaParser() {
    	return parser;
    }

	@Override
    public  void close() throws IOException {
        closed=true;
        
    }

    @Override
    public  boolean isClosed() {
        return closed;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        return get(id)!=null;
    }

    @Override
    public F get(String id)
            throws DataStoreException {
        StreamingIterator<F> iter = iterator();
        try{
	        while(iter.hasNext()){
	        	F next = iter.next();
	        	if(next.getId().equals(id)){
	        		return next;
	        	}
	        }
	        //we get here if we didn't find it
	        return null;
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

    protected abstract FastaRecordVisitor createRecordVisitor(String id, String comment, Consumer<F> callback);
    
    @Override
    public <E extends Throwable> void forEach(ThrowingBiConsumer<String, F, E> consumer) throws IOException, E {
        checkNotYetClosed();
        FastaVisitor visitor;
        if(recordFilter ==null){
            visitor = new FastaVisitor() {
                
                @Override
                public void visitEnd() {
                    
                }
                
                @Override
                public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
                        String id, String optionalComment) {
                    if(filter.test(id)){
                        return createRecordVisitor(id, optionalComment, r->{
                           try{
                               consumer.accept(id, r);
                           }catch(Throwable t){
                               Sneak.sneakyThrow(t);
                           }
                        });
                          
                    }
                    
                    return null;
                }
                
                @Override
                public void halted() {
                   
                    
                }
            };
        }
        else{
            visitor = new FastaVisitor() {
                
                @Override
                public void visitEnd() {
                    
                }
                
                @Override
                public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
                        String id, String optionalComment) {
                    if(filter.test(id)){
                        return createRecordVisitor(id, optionalComment, 
                                r-> {
                                    if(recordFilter.test(r)){
                                        try{
                                            consumer.accept(id, r);
                                        }catch(Throwable t){
                                            Sneak.sneakyThrow(t);
                                        }
                                    }
                                });
                                
                          
                    }
                    
                    return null;
                }
                
                @Override
                public void halted() {
                   
                    
                }
            };
        }
        if(maxNumberOfRecords !=null) {
        	visitor = new MaxNumberOfRecordsFastaVisitor(maxNumberOfRecords, visitor);
        }
        parser.parse(visitor);

    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        if(recordFilter==null){
            return DataStoreStreamingIterator.create(this,LargeFastaIdIterator.createNewIteratorFor(parser,filter, maxNumberOfRecords));
        }
        
        return new AdditionalRecordFilteringIdIterator(iterator());
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
        
        if(size ==null){
            if(recordFilter ==null){
                try {
                	NoAdditionalRecordFilteringSizeCounter visitor = new NoAdditionalRecordFilteringSizeCounter();      
            		parser.parse(visitor);
            		size = visitor.getSize();
                } catch (IOException e) {
                    throw new IllegalStateException("could not get record count",e);
                }
            }else{
                long temp=0;
                try(StreamingIterator<F> iter = iterator()){
                    while(iter.hasNext()){
                        iter.next();
                        temp++;
                    }
                }
                size= temp;
            }
        }   
        return size;

    }
	

    @Override
    public final StreamingIterator<F> iterator() throws DataStoreException {
        checkNotYetClosed();
        return createNewIterator(parser,filter, recordFilter);
       
    }

	protected abstract StreamingIterator<F> createNewIterator(FastaParser parser ,Predicate<String> filter, Predicate<F> recordIterator) throws DataStoreException;
   

	@Override
	public final StreamingIterator<DataStoreEntry<F>> entryIterator()
			throws DataStoreException {
		checkNotYetClosed();
		return new StreamingIterator<DataStoreEntry<F>>(){
			StreamingIterator<F> iter = iterator();
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public void close() {
				iter.close();
			}

			@Override
			public DataStoreEntry<F> next() {
				F next = iter.next();
				return new DataStoreEntry<F>(next.getId(), next);
			}

			@Override
			public void remove() {
				iter.remove();
			}
			
		};
	}

	private final class NoAdditionalRecordFilteringSizeCounter implements FastaVisitor {
	        long numDeflines=0L;

	        @Override
	        public FastaRecordVisitor visitDefline(
	                        FastaVisitorCallback callback, String id,
	                        String optionalComment) {
	                if(filter.test(id)){                                       
	                        numDeflines++;
	                }
	                
	                return null;
	                
	        }

	        public long getSize(){
	        	return numDeflines;
	        }
	        @Override
	        public void visitEnd() {
	        	//no-op
	        }

	        @Override
	        public void halted() {
	                //this shouldn't happen
	                //throw an exception so we don't
	                //return a null value or try 
	                //to reparse the size 
	                //next time it's asked 
	                throw new IllegalStateException("parser was halted when trying to compute size");               
	        }
	    }
	
	private final class AdditionalRecordFilteringIdIterator implements StreamingIterator<String> {
	        private final StreamingIterator<F> iter;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public void close() {
               iter.close();
            }

            @Override
            public String next() {
                return iter.next().getId();
            }

            public AdditionalRecordFilteringIdIterator(StreamingIterator<F> iter) {
                this.iter = iter;
            }
	        
	    }
   
}
