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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

/**
 * {@code AceFileDataStoreBuilder} is a builder
 * that can create new {@link AceFileDataStore}
 * instances using data from a given 
 * ace file.  The iteration order of {@link AceFileDataStore#iterator()}
 * and {@link AceFileDataStore#idIterator()}
 * is the order of that the contigs appear 
 * in the ace file.
 * @author dkatzel
 *
 */
public final class AceFileDataStoreBuilder {
	/**
	 * Reference to ace file to be visited,
	 * will be null if we use an inputStream instead. 
	 */
	private final File aceFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	/**
	 * Reference to the {@link InputStream} of ace data to be visited,
	 * will be null if we use an file instead. 
	 */
	private final InputStream aceStream;
	/**
	 * Create a new instance of {@code AceFileDataStoreBuilder}
	 * which will build a {@link AceFileDataStore} for the given
	 * ace file.
	 * @param aceFile the ace file make a {@link AceFileDataStore} with. 
	 * @throws IOException if the ace file does not exist, or can not be read.
	 * @throws NullPointerException if aceFile is null.
	 */
	public AceFileDataStoreBuilder(File aceFile) throws IOException{
		if(aceFile ==null){
			throw new NullPointerException("ace file can not be null");
		}
		if(!aceFile.exists()){
			throw new FileNotFoundException("ace file must exist");
		}
		if(!aceFile.canRead()){
			throw new IOException("ace file is not readable");
		}
		this.aceFile = aceFile;
		this.aceStream = null;
	}
	
	/**
	 * Create a new instance of {@code AceFileDataStoreBuilder}
	 * which will build a {@link AceFileDataStore} for the given
	 * {@link InputStream} containing .ace encoded data.
	 * @param aceStream the {@link InputStream} that will
	 * be used to make a {@link AceFileDataStore}.
	 * The stream must be uncompressed.
	 * 
	 * @throws NullPointerException if aceFile is aceStream. 
	 */
	public AceFileDataStoreBuilder(InputStream aceStream){
		if(aceStream ==null){
			throw new NullPointerException("inputstream can not be null");
		}		
		this.aceFile = null;
		this.aceStream = aceStream;
	}
	/**
	 * Only include the {@link AceContig}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the ace file will be included in the built
	 * {@link AceFileDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified {@link AceContig}s; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	public AceFileDataStoreBuilder filter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	/**
	 * Provide a {@link DataStoreProviderHint} to this builder
	 * to let it know the implementation preferences of the client.
	 * If no hint is given, then this builder will
	 * try to store all the {@link AceContig} records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link AceFileDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public AceFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given ace file and return
	 * a new instance of a {@link AceFileDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link AceContig}s will be included in this {@link AceFileDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link AceContig}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link AceFileDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * ace file. 
	 * @see #hint(DataStoreProviderHint)
	 */
	public AceFileDataStore build() throws IOException {
		if(aceStream ==null){
			return buildFromFile();
		}
		//since it's a stream we won't be able to re-seek so any
		//request will require a storing everything in memory.
		return DefaultAceFileDataStore.create(aceStream,filter);

	}

	private AceFileDataStore buildFromFile() throws IOException,
			FileNotFoundException {
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultAceFileDataStore.create(aceFile,filter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY: return IndexedAceFileDataStore.create(aceFile,filter);
			case ITERATION_ONLY: return LargeAceFileDataStore.create(aceFile,filter);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}
}
