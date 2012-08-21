package org.jcvi.common.core.assembly.clc.cas.var;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code DefaultReferenceVariationsDataStore} is an 
 * implementation of {@link ReferenceVariationsDataStore}
 * that stores all varition information for all of its 
 * references in memory.  This can be quite memory intensive and is not
 * recommended when many variations are found.
 * @author dkatzel
 *
 */
public final class DefaultReferenceVariationsDataStore implements ReferenceVariationsDataStore{

	private final DataStore<ReferenceVariations> delegate;
	/**
	 * Parse the given find_variations log file and 
	 * create a new {@link ReferenceVariationsDataStore} instance containing
	 * all the variations found for all the references in the file. 
	 * @param logFile the find variations file to parse; 
	 * must exist and can not be null.
	 * @return a new {@link ReferenceVariationsDataStore} as described above.
	 * @throws IOException if there is a problem finding or parsing the file.
	 */
	public static ReferenceVariationsDataStore createFromLogFile(File logFile) throws IOException{
		Builder builder = new Builder();
		VariationLogFileParser.parse(logFile, builder);
		return builder.build();
	}
	
	private DefaultReferenceVariationsDataStore(
			DataStore<ReferenceVariations> delegate) {
		this.delegate = delegate;
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}

	@Override
	public ReferenceVariations get(String id) throws DataStoreException {
		return delegate.get(id);
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return delegate.getNumberOfRecords();
	}

	@Override
	public boolean isClosed(){
		return delegate.isClosed();
	}

	@Override
	public StreamingIterator<ReferenceVariations> iterator()
			throws DataStoreException {
		return delegate.iterator();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
		
	}
	private static class Builder implements VariationLogFileVisitor, org.jcvi.common.core.util.Builder<ReferenceVariationsDataStore> {

	    private SortedMap<Long, Variation> currentMap=null;
	    private final Map<String, ReferenceVariations> map = new LinkedHashMap<String, ReferenceVariations>();
	    private String currentId;
	    
	    @Override
		public ReferenceVariationsDataStore build() {
			return new DefaultReferenceVariationsDataStore(MapDataStoreAdapter.adapt(map));
		}

		/**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean visitReference(String id) {
	        if(currentMap !=null){
	        	map.put(currentId, new DefaultReferenceVariations(currentId, currentMap));
	        }
	        currentMap=new TreeMap<Long, Variation>();
	        currentId =id;
	        return true;
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitVariation(Variation variation) {
	        long coordinate = variation.getCoordinate();
	        currentMap.put(coordinate, variation);
	        
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitLine(String line) {
	        
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitEndOfFile() {
	    	if(currentMap !=null){
	    		map.put(currentId, new DefaultReferenceVariations(currentId, currentMap));
	        }
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitFile() {
	        
	    }
	    
	    
	    
	}
}
