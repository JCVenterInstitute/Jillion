package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

public final class ChainedDataStore {

	private ChainedDataStore(){
		//can not instantiate
	}
	public static <T,D extends DataStore<T>> D create(Class<D> classType,Collection<D> delegates){
       DataStore<T> wrappedDataStore = new WrapperDataStore<T,D>(delegates);
       return DataStoreAdapter.adapt(classType, wrappedDataStore);
    }
	
	private static class WrapperDataStore<T, D extends DataStore<T>> implements DataStore<T>{

		private final List<D> delegates;
		
		
		public WrapperDataStore(Collection<D> delegates) {
			
			if(delegates.isEmpty()){
				throw new IllegalArgumentException("must be at least one DataStore");
			}
			this.delegates = new ArrayList<D>(delegates);
			for(DataStore<T> delegate : delegates){
				if(delegate==null){
					throw new NullPointerException("DataStore can not be null");
				}
			}
		}

		@Override
		public void close() throws IOException {
			for(DataStore<T> delegate : delegates){
				IOUtil.closeAndIgnoreErrors(delegate);
			}
			
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			List<StreamingIterator<String>> iterators = new ArrayList<StreamingIterator<String>>();
			for(DataStore<T> delegate : delegates){
				iterators.add(delegate.idIterator());
			}
			return IteratorUtil.createChainedStreamingIterator(iterators);
		}

		@Override
		public T get(String id) throws DataStoreException {
			for(DataStore<T> delegate : delegates){
				T ret= delegate.get(id);
				if(ret !=null){
					return ret;
				}
			}
			return null;
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			for(DataStore<T> delegate : delegates){
				if(delegate.contains(id)){
					return true;
				}
			}
			return false;
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			long total=0L;
			for(DataStore<T> delegate : delegates){
				total +=delegate.getNumberOfRecords();
			}
			return total;
		}

		@Override
		public boolean isClosed() {
			for(DataStore<T> delegate : delegates){
				if(delegate.isClosed()){
					return true;
				}
			}
			return false;
		}

		@Override
		public StreamingIterator<T> iterator() throws DataStoreException {
			List<StreamingIterator<T>> iterators = new ArrayList<StreamingIterator<T>>();
			for(DataStore<T> delegate : delegates){
				iterators.add(delegate.iterator());
			}
			return IteratorUtil.createChainedStreamingIterator(iterators);
		}
		
	}
}
