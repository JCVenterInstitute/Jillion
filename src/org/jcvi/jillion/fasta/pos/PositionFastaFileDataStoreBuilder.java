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
package org.jcvi.jillion.fasta.pos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
/**
 * Factory class that can create new instances
 * of {@link PositionFastaDataStore}s
 * using data from a given input fasta file
 * and filtering options.
 * 
 * @author dkatzel
 * 
 * @since 5.0 - added to keep API consistent with
 * other Fasta DataStore implementations
 *
 */
public class PositionFastaFileDataStoreBuilder {

	private final FastaParser parser;
	private Predicate<String> idFilter = id->true;
	private Predicate<PositionFastaRecord> recordFilter = null;
	
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	public PositionFastaFileDataStoreBuilder(File positionFastaFile) throws IOException{
		this(FastaFileParser.create(positionFastaFile));
	}
	
	public PositionFastaFileDataStoreBuilder(InputStream positionFastaFileStream) throws IOException{
		this(FastaFileParser.create(positionFastaFileStream));
	}
	
	public PositionFastaFileDataStoreBuilder(FastaParser parser){
		Objects.requireNonNull(parser);
		this.parser = parser;
	}
	/**
	 * Provide a {@link DataStoreProviderHint} to this builder
	 * to let it know the implementation preferences of the client.
	 * If no hint is given, then this builder will
	 * try to store all the fasta records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link FastaDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	protected PositionFastaFileDataStoreBuilder hint(DataStoreProviderHint hint) {
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	 /**
	 * Only include the {@link PositionFastaRecord}s which pass
	 * the given {@link Predicate} for the ID.  If a filter
	 * is not given to this builder, then all records
	 * in the fasta file will be included in the built
	 * {@link PositionFastaDataStore}.
	 * <p>
     * If both this method and {@link #filter(Predicate)}
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * <p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     * 
	 * @param filter a {@link Predicate} instance that can be
	 * used to filter out specified fasta records BY ID; can not be null. 
	 * @return this.
	 * 
	 * @throws NullPointerException if filter is null.
	 * 
	 * @apiNote This is different than {@link #filterRecords(Predicate)}
     * because the latter needs to parse the entire record before
     * filtering can be determined while this filter only needs the ID. If you are only filtering
     * by ID, use this method which may have better
     * performance since the position values don't have to be parsed
     * on reads that aren't accepted by the id filter.
	 * 
	 * @see #filterRecords(Predicate)
	 */
	public PositionFastaFileDataStoreBuilder filter(Predicate<String> filter) {
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.idFilter = filter;
		return this;
	}
	
	/**
     * Only include the {@link PositionFastaRecord}s which pass
     * the given {@link Predicate}.  If no predicates
     * are given to this builder, then all records
     * in the fasta file will be included in the built
     * {@link PositionFastaDataStore}.
     * <p>
     * If both this method and {@link #filter(Predicate)} to filter by ID
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * <p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     * 
     * @param filter a {@link Predicate} instance that can be
     * used to filter out specified fasta records; can not be null. 
     * 
     * @return this.
     * @throws NullPointerException if filter is null.
     * 
     * @apiNote This is different than {@link #filter(Predicate)}
     * because the latter can only filter by ID. If you are only filtering
     * by ID, use {@link #filter(Predicate)} which may have better
     * performance since the position values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     * <p>
     * Also, we had to keep the
     * old filter method to maintain compatibility with old versions of Jillion
     * 
     * @since 5.0
     * @see #filter(Predicate)
     */
    public PositionFastaFileDataStoreBuilder filterRecords(Predicate<PositionFastaRecord> filter) {
        if(filter==null){
                throw new NullPointerException("filter can not be null");
        }
        this.recordFilter = filter;
        return this;
    } 
	
    /**
     * Create a new {@link PositionFastaDataStore} object
     * from the records in the provided fasta file
     * but only including the records that pass the filters.
     * 
     * @return a new {@link PositionFastaDataStore}; will never be null.
     * 
     * @throws IOException if there is a problem parsing the input fasta data.
     */
	public PositionFastaDataStore build() throws IOException{
		if(hint == DataStoreProviderHint.ITERATION_ONLY){
			return LargePositionFastaFileDataStore.create(parser, idFilter, recordFilter);
		}
		return DefaultPositionFastaFileDataStore.create(parser, idFilter, recordFilter);
	}
}
