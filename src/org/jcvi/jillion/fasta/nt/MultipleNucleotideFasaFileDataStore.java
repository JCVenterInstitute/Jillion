package org.jcvi.jillion.fasta.nt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.Sneak;

public class MultipleNucleotideFasaFileDataStore implements NucleotideFastaDataStore{

	private List<NucleotideFastaDataStore> datastores;
	private volatile boolean closed=false;
	
	
	public MultipleNucleotideFasaFileDataStore(Collection<NucleotideFastaDataStore> datastores) {
		this.datastores = new ArrayList<>(datastores);
	}

	
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return IteratorUtil.chainStreamingSuppliers(datastores.stream()
				.map(this::idIteratorSupplierFor).collect(Collectors.toList()));

	}
	
	private Supplier<StreamingIterator<String>> idIteratorSupplierFor(NucleotideFastaDataStore d){
		
			return ()-> {
				try{
					return d.idIterator();
				}catch(DataStoreException e) {
					return Sneak.sneakyThrow(e);
				}
			};
	}
	
	private Supplier<StreamingIterator<NucleotideFastaRecord>> iteratorSupplierFor(NucleotideFastaDataStore d){
		
		return ()-> {
			try{
				return d.iterator();
			}catch(DataStoreException e) {
				return Sneak.sneakyThrow(e);
			}
		};
	}
	
	private Supplier<StreamingIterator<DataStoreEntry<NucleotideFastaRecord>>> entryIteratorSupplierFor(NucleotideFastaDataStore d){
		
		return ()-> {
			try{
				return d.entryIterator();
			}catch(DataStoreException e) {
				return Sneak.sneakyThrow(e);
			}
		};
	}
	

	@Override
	public NucleotideFastaRecord get(String id) throws DataStoreException {
		for(NucleotideFastaDataStore d : datastores) {
			NucleotideFastaRecord r = d.get(id);
			if(r !=null) {
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		for(NucleotideFastaDataStore d : datastores) {
			if(d.contains(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		long total= 0L;
		for(NucleotideFastaDataStore d : datastores) {
			total+= d.getNumberOfRecords();
		}
		return total;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public StreamingIterator<NucleotideFastaRecord> iterator() throws DataStoreException {
		return IteratorUtil.chainStreamingSuppliers(datastores.stream()
				.map(this::iteratorSupplierFor).collect(Collectors.toList()));
	}

	@Override
	public StreamingIterator<DataStoreEntry<NucleotideFastaRecord>> entryIterator() throws DataStoreException {
		return IteratorUtil.chainStreamingSuppliers(datastores.stream()
				.map(this::entryIteratorSupplierFor).collect(Collectors.toList()));
	}

	@Override
	public void close() throws IOException {
		closed=true;
		for(DataStore<?> d: datastores) {
			IOUtil.closeAndIgnoreErrors(d);
		}
		
	}

}
