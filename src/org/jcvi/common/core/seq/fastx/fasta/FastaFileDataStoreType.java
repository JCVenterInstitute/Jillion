package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStore;
/**
 * {@code FastaFileDataStoreType}
 * describes implementation types
 * that this factory can create.
 * @author dkatzel
 *
 */
public enum FastaFileDataStoreType{
	/**
	 * All fasta data from the fasta file is stored
	 * in a map.  This implementation allows for very fast
	 * random access but is not very 
	 * memory efficient and therefore should not be used
	 * for large fasta files.
	 */
	MAP_BACKED,
	/**
	 * The only data that is stored for the fasta is an index containing
	 * byte offsets to the various fasta records contained
	 * inside the fasta file. 
	 * <p/>
	 * This allows large files to provide random 
	 * access without taking up much memory.  The down side is each fasta record
	 * must be re-parsed each time and the fasta file must exist and not
	 * get altered during the entire lifetime of this object.
	 */
	INDEXED,
	/**
	 * This implementation doesn't store any fasta or 
	 * read information in memory.
	 * This means that each {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * requires re-parsing the fasta file which can take some time.
	 * Other methods such as {@link DataStore#getNumberOfRecords()} are lazy-loaded
	 * and are only parsed the first time they are asked for.
	 * <p/>
	 * This implementation is ideal for use cases
	 * where the contents of the fasta file
	 * will only be read once in a single pass.
	 * For example, iterating over each fasta record only once 
	 * using {@link DataStore#iterator()}.
	 * <p/>
	 * Since each method call involves re-parsing the fasta file,
	 * that file must not be modified or moved during the
	 * entire lifetime of the instance.
	 * It is recommended that instances
	 * are wrapped by {@link CachedDataStore}.
	 */
	LARGE
	;
}
