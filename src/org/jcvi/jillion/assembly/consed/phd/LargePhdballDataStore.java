package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

final class LargePhdballDataStore implements PhdDataStore{

	

	private volatile boolean closed=false;
	private final File phdFile;
	private final DataStoreFilter filter;
	
	private Long numberOfRecords =null;
	
	public LargePhdballDataStore(File phdFile, DataStoreFilter filter) throws FileNotFoundException {
		if(!phdFile.exists()){
			throw new FileNotFoundException(phdFile.getAbsolutePath());
		}
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		this.phdFile = phdFile;
		this.filter = filter;
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		verifyNotClosed();
		return DataStoreStreamingIterator.create(this,
				PhdBallIdIterator.createNewIterator(phdFile, filter));
	}

	@Override
	public Phd get(String id) throws DataStoreException {
		verifyNotClosed();
		if(!filter.accept(id)){
			return null;
		}
		StreamingIterator<Phd> iter =null;
		try{
			iter = iterator();
			while(iter.hasNext()){
				Phd phd = iter.next();
				if(phd.getId().equals(id)){
					return phd;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		return null;
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		verifyNotClosed();
		if(!filter.accept(id)){
			return false;
		}
		StreamingIterator<String> idIter =null;
		try{
			idIter = idIterator();
			while(idIter.hasNext()){
				if(idIter.next().equals(id)){
					return true;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(idIter);
		}
		return false;
	}

	@Override
	public synchronized long getNumberOfRecords() throws DataStoreException {
		verifyNotClosed();
		if(numberOfRecords==null){
			StreamingIterator<String> idIter =null;
			long count=0L;
			try{
				idIter = idIterator();
				while(idIter.hasNext()){
					idIter.next();
					count++;
				}
				numberOfRecords=count;				
			}finally{
				IOUtil.closeAndIgnoreErrors(idIter);
			}
		}
		return numberOfRecords;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public StreamingIterator<Phd> iterator() throws DataStoreException {
		verifyNotClosed();
		return DataStoreStreamingIterator.create(this,
				PhdBallIterator.createNewIterator(phdFile, filter));
	}

	@Override
	public void close() throws IOException {
		closed = true;		
	}
	
	private void verifyNotClosed(){
		if(closed){
			throw new DataStoreClosedException("data store is closed");
		}
	}

	
}
