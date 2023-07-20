package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandlers;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.Sneak;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
/**
 * A {@link NucleotideFastaDataStore} implementation that wraps multiple fasta files.
 * 
 * @author dkatzel
 * @since 6.0
 *
 */
public class MultipleNucleotideFastaFileDataStore implements NucleotideFastaDataStore{

	private List<NucleotideFastaDataStore> datastores;
	private volatile boolean closed=false;
	
	@Data
	@Builder
	public static class Parameters{
		@NonNull
		@Singular
		private List<@NonNull File> fastaFiles;
		
		@Singular
		private Set<@NonNull String> includeIds;
		
		private Predicate<NucleotideFastaRecord> recordFilter;
		
		private InvalidCharacterHandler invalidCharacterHandler;
		
		public static ParametersBuilder builder() {
			return new ParametersBuilder()
						.fastaFiles(new ArrayList<>());
		}
		//needed for javadoc to work...
		public static class ParametersBuilder{
			
		}
	}
	/**
	 * Create a new instance wrapping all the given fasta files.
	 * @param fastaFiles the list of fasta files can not be null or contain null File objects.
	 * @return a new instance
	 * @throws IOException if there are problems parsing any of the fasta files.
	 */
	public static MultipleNucleotideFastaFileDataStore create(List<File> fastaFiles) throws IOException {
		return create(Parameters.builder().fastaFiles(new ArrayList<>(fastaFiles)).build());
	}
	/**
	 * Create a new instance using the given {@link Parameters} object.
	 * @param parameters the parameters to use; can not be null.
	 * @return a new instance
	 * @throws IOException if there are problems parsing any of the fasta files.
	 */
	public static MultipleNucleotideFastaFileDataStore create(Parameters parameters) throws IOException {
		List<NucleotideFastaDataStore> list = new ArrayList<>();
		
		for(File f : parameters.fastaFiles) {
			NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(f)
					.hint(DataStoreProviderHint.ITERATION_ONLY)
					.invalidCharacterHandler(parameters.invalidCharacterHandler);
			if(parameters.includeIds !=null && !parameters.includeIds.isEmpty()) {
				builder.onlyIncludeIds(parameters.includeIds);
			}
			
			if(parameters.recordFilter !=null) {
				builder.filterRecords(parameters.recordFilter);
			}
			list.add(builder.build());
			
		}
		return new MultipleNucleotideFastaFileDataStore(list);
	}
	public MultipleNucleotideFastaFileDataStore(Collection<NucleotideFastaDataStore> datastores) {
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
