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
package org.jcvi.jillion.shared.fasta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecord;
/**
 * Abstract class that creates a Builder to make a {@link FastaDataStore} instance.
 * Subclasses should override the abstract methods to make the correct type of datastore.
 * @author dkatzel
 *
 * @param <T> the type of element in the sequence.
 * @param <S> the {@link Sequence} type.
 * @param <F> the {@link FastaRecord} type.
 * @param <D> the {@link FastaDataStore} type.
 */
public abstract class AbstractFastaFileDataStoreBuilder<T, S extends Sequence<T>, F extends FastaRecord<T,S>,SD extends DataStore<S>, D extends FastaDataStore<T,S, F, SD>> {

	private final FastaParser parser;
	private Predicate<String> filter = id->true;
	private Predicate<F> recordFilter = null;
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	protected AbstractFastaFileDataStoreBuilder(File fastaFile) throws IOException{

		this.parser = FastaFileParser.create(fastaFile);
	}
	
	/**
         * Create a new Builder instance
         * that will build a {@link FastaDataStore} from the
         * data from the given {@link InputStreamSupplier}.
         * 
         * @param inputStreamSupplier the {@link InputStreamSupplier} to use
         * to get the inputStreams of fasta encoded data.
         * @throws NullPointerException if the inputStreamSupplier is null.
         * 
         * @since 5.0
         */
        protected AbstractFastaFileDataStoreBuilder(InputStreamSupplier inputStreamSupplier) throws IOException{

                this.parser = FastaFileParser.create(inputStreamSupplier);
        }

	
	
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file {@link InputStream}.
	 * @param fastaFileAsStream the fasta file as an {@link InputStream}
	 * to make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	protected AbstractFastaFileDataStoreBuilder(InputStream fastaFileAsStream) throws IOException{
		
		this.parser = FastaFileParser.create(fastaFileAsStream);
		
	}
	protected AbstractFastaFileDataStoreBuilder(FastaParser parser){
		if(parser==null){
			throw new NullPointerException("fasta parser can not be null");
		}
		this.parser = parser;
		
	}
	
	
	 /**
	 * Only include the {@link FastaRecord}s which pass
	 * the given {@link Predicate} for the ID.  If a filter
	 * is not given to this builder, then all records
	 * in the fasta file will be included in the built
	 * {@link FastaDataStore}.
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
     * performance since the sequence values don't have to be parsed
     * on reads that aren't accepted by the id filter.
	 * 
	 * @see #filterRecord(Predicate)
	 */
	protected AbstractFastaFileDataStoreBuilder<T, S, F, SD, D> filter(Predicate<String> filter) {
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	
	/**
     * Only include the {@link FastaRecord}s which pass
     * the given {@link Predicate}.  If no predicates
     * are given to this builder, then all records
     * in the fasta file will be included in the built
     * {@link FastaDataStore}.
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
     * performance since the sequence values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     * <p>
     * Also, we had to keep the
     * old filter method to maintain compatibility with old versions of Jillion
     * 
     * @since 5.0
     * @see #filter(Predicate)
     */
        protected AbstractFastaFileDataStoreBuilder<T, S, F, SD, D> filterRecords(Predicate<F> filter) {
                if(filter==null){
                        throw new NullPointerException("filter can not be null");
                }
                this.recordFilter = filter;
                return this;
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
	protected AbstractFastaFileDataStoreBuilder<T, S, F, SD, D> hint(DataStoreProviderHint hint) {
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}

	/**
	 * Parse the given fasta file and return
	 * a new instance of a {@link FastaDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link FastaRecord}s will be included in this {@link FastaDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link FastaRecord}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link FastaDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * fasta file.
	 * @see #hint(DataStoreProviderHint)
	 */
	protected D build() throws IOException {
		return createNewInstance(parser, hint, filter, recordFilter);
	}

	/**
	 * Create a new {@link FastaDataStore} instance.
	 * @param parser the {@link FastaParser} to use to make the datastore for;
	 * can not be null.
	 * @param hint a {@link DataStoreProviderHint}; will never be null.
	 * @param filter a {@link Predicate}; will never be null.
	 * @param recordFilter a {@link Predicate}; can be null if no additional filtering is used.
	 * @return a new {@link FastaDataStore} instance; should never be null.
	 * @throws IOException if there is a problem creating the datastore from the file.
	 */
	protected abstract D createNewInstance(FastaParser parser, DataStoreProviderHint hint, Predicate<String> filter, Predicate<F> recordFilter) throws IOException;
			



}
