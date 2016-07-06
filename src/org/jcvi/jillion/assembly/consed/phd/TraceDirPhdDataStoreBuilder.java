package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;

/**
 * Class that can make a {@link PhdDataStore}
 * from a folder of trace files (ab1, ztr, scf etc).
 * 
 * @author dkatzel
 *
 */
public class TraceDirPhdDataStoreBuilder {

	
	private final File dir;
	private Function<File, String> nameConverter = f -> FileUtil.getBaseName(f);
	
	private Function<File, Date> dateFunction = f-> new Date(f.lastModified());
	
	
	public TraceDirPhdDataStoreBuilder(File traceDir) throws IOException{
		IOUtil.verifyIsReadable(traceDir);
		this.dir = traceDir;
	}
	/**
	 * Function to compute the date for the trace.
	 * If this method is not called, the default implementation
	 * will use the File's last modified timestamp.
	 * 
	 * @param dateFunction the date function to use; can not be null.
	 * @return this.
	 * 
	 * @throws NullPointerException if the function is null.
	 */
	public TraceDirPhdDataStoreBuilder dateFunction(Function<File, Date> dateFunction){
		Objects.requireNonNull(dateFunction);
		this.dateFunction = dateFunction;
		
		return this;
	}

	/**
	 * Function to compute the Name to use for this trace so it can
	 * be retrieved by {@link PhdDataStore#get(String)}.
	 * If this method is not called, the default implementation
	 * will use the file name up to but not incuding the extension (the base name).
	 * 
	 * @param nameConverter the function to use; can not be null.
	 * @return this.
	 * 
	 * @throws NullPointerException if the function is null.
	 */
	public TraceDirPhdDataStoreBuilder nameConverter(Function<File, String> nameConverter){
		Objects.requireNonNull(nameConverter);
		this.nameConverter = nameConverter;
		
		return this;
	}
	/**
	 * Build a new {@link PhdDataStore} using the given configuration so far.
	 * @return a new PhdDataStore; will never be null.
	 * @throws IOException if there is a problem parsing the trace files.
	 */
	public PhdDataStore build() throws IOException{
		return new TraceDirPhdDataStore(this);
	}
	
	
	private static final class TraceDirPhdDataStore implements PhdDataStore{

		private final Map<String, File> fileMapping =new LinkedHashMap<>();
		
		private final Function<File, Date> dateFunction;
		
		public TraceDirPhdDataStore(TraceDirPhdDataStoreBuilder builder) throws IOException{
			
			File dir = builder.dir;
			Function<File, String> nameConverter = builder.nameConverter;
			
			this.dateFunction = builder.dateFunction;
			
			File[] files = dir.listFiles();
			if(files ==null){
				return;
			}
			for(File f : files){
				if(f.isDirectory()){
					//TODO handle nested directories
					continue;
				}
				String id = nameConverter.apply(f);
				if(id !=null){
					fileMapping.put(id, f);
				}
			}
		}
		
		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return DataStoreStreamingIterator.create(this, IteratorUtil.createStreamingIterator(fileMapping.keySet()));
		}

		@Override
		public Phd get(String id) throws DataStoreException {
			File f = fileMapping.get(id);
			if(f ==null){
				return null;
			}
			try{
			return asPhd(f, id);
			}catch(IOException e){
				throw new DataStoreException(e.getMessage(), e);
			}
		}
		
		private Phd asPhd(File f, String id) throws IOException{
			Chromatogram chromo = ChromatogramFactory.create(id, f);
			
			Date chromoDate = dateFunction.apply(f);
			return new PhdBuilder(chromo)
					.comments(PhdUtil.createPhdTimeStampAndChromatFileCommentsFor(chromoDate, f.getName()))
					.build();
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return fileMapping.containsKey(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			return fileMapping.size();
		}

		@Override
		public boolean isClosed() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public StreamingIterator<Phd> iterator() throws DataStoreException {
			return DataStoreStreamingIterator.create(this, new StreamingIterator<Phd>() {
				StreamingIterator<String> idIter = idIterator();

				@Override
				public boolean hasNext() {
					return idIter.hasNext();
				}

				@Override
				public void close() {
					idIter.close();
				}

				@Override
				public Phd next() {
					
					try {
						return get(idIter.next());
					} catch (DataStoreException e) {
						throw new IllegalStateException(e);
					}
				}
				
				
			});
		}

		@Override
		public StreamingIterator<DataStoreEntry<Phd>> entryIterator() throws DataStoreException {
			return DataStoreStreamingIterator.create(this,
					new StreamingIterator<DataStoreEntry<Phd>>() {
				StreamingIterator<Phd> iter = iterator();

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public void close() {
					iter.close();
				}

				@Override
				public DataStoreEntry<Phd> next() {
					Phd phd = iter.next();
					return new DataStoreEntry<Phd>(phd.getId(), phd);
				}
				
			});
		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

	}
}
