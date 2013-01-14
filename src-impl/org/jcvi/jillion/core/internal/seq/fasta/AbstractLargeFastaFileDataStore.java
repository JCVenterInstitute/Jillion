package org.jcvi.jillion.core.internal.seq.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.jcvi.common.core.seq.fasta.FastaDataStore;
import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.common.core.seq.fasta.FastaUtil;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public abstract class AbstractLargeFastaFileDataStore<T extends Symbol,S extends Sequence<T>, F extends FastaRecord<T, S>> implements FastaDataStore<T,S,F>{

    private final File fastaFile;
    private final DataStoreFilter filter;
    private Long size;
    private volatile boolean closed=false;
    
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
    protected AbstractLargeFastaFileDataStore(File fastaFile, DataStoreFilter filter) {
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        if(filter ==null){
            throw new NullPointerException("filter file can not be null");
        }
        this.filter =filter;
        this.fastaFile = fastaFile;
    }
    
    private void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    
    /**
	 * Get the {@link DataStoreFilter} used by this builder.
	 * @return a {@link DataStoreFilter} instance, never null.
	 */
	protected final DataStoreFilter getFilter() {
		return filter;
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

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,LargeFastaIdIterator.createNewIteratorFor(fastaFile,filter));
        
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                size= countFilteredIds();            
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("could not get record count");
            }
        }   
        return size;

    }
	private long countFilteredIds() throws FileNotFoundException {
		Scanner scanner = new Scanner(fastaFile, IOUtil.UTF_8_NAME);
		long counter =0;
		while(scanner.hasNextLine()){
		    String line = scanner.nextLine();
		    
		    Matcher matcher = FastaUtil.ID_LINE_PATTERN.matcher(line);
		    if(matcher.find()){
		    	String id = matcher.group(1);
		    	if(filter.accept(id)){
		    		counter++;
		    	}
		    }
		}
		return counter;
	}

    @Override
    public final StreamingIterator<F> iterator() {
        checkNotYetClosed();
        return createNewIterator(fastaFile);
       
    }

	protected abstract StreamingIterator<F> createNewIterator(File fastaFile);
   


   
}
