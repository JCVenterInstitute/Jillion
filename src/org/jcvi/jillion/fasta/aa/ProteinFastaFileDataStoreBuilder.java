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
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceDataStore;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.fasta.aa.DefaultProteinFastaDataStore;
import org.jcvi.jillion.internal.fasta.aa.IndexedProteinFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.aa.LargeProteinFastaFileDataStore;
import org.jcvi.jillion.shared.fasta.AbstractFastaFileDataStoreBuilder;


/**
 * {@code ProteinFastaFileDataStoreBuilder}
 * is a Builder that can create new instances
 * of {@link ProteinFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class ProteinFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinSequenceDataStore, ProteinFastaFileDataStore> {
    /**
     * Create a new Builder instance
     * that will build a {@link ProteinFastaDataStore} using
     * the {@link FastaParser} object that will be parsing 
     * protein fasta encoded data.
     * 
     * @param parser the {@link FastaParser} to use
     * to visit the fasta encoded data.
     * @throws NullPointerException if the inputStreamSupplier is null.
     */
    public ProteinFastaFileDataStoreBuilder(FastaParser parser) {
            super(parser);
    }
    
    /**
     * Create a new Builder instance of 
     * which will build a {@link ProteinFastaDataStore} for the given
     * fasta file.
     * @param fastaFile the fasta file make a {@link ProteinFastaDataStore} with. 
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if fastaFile is null.
     */
    public ProteinFastaFileDataStoreBuilder(File fastaFile) throws IOException {
            super(fastaFile);
    }
    
    /**
     * Create a new Builder instance
     * that will build a {@link ProteinFastaDataStore} from the
     * protein fasta encoded data from the given {@link InputStreamSupplier}.
     * 
     * @param supplier the {@link InputStreamSupplier} to use
     * to get the inputStreams of fasta encoded data.
     * @throws NullPointerException if the inputStreamSupplier is null.
     * 
     * @since 5.0
     */
    public ProteinFastaFileDataStoreBuilder(InputStreamSupplier supplier) throws IOException {
        super(supplier);
    }
	/**
	 * Create a new {@link ProteinFastaFileDataStoreBuilder}
	 * instance that will use the given fasta encoded inputStream
	 * as input.
	 * @param in the fasta encoded data to use can not be null. 
	 * @throws IOException if the fasta file does not exist
	 * @throws NullPointerException if fastaFile is null.
	 */
	public ProteinFastaFileDataStoreBuilder(InputStream in) throws IOException{
		super(in);
	}
	
	/**
	 * Create a new {@link FastaDataStore} instance.
	 * @param fastaFile the fasta file to make the datastore for;
	 * can not be null and should exist.
	 * @param hint a {@link DataStoreProviderHint}; will never be null.
	 * @param filter a {@link DataStoreFilter}; will never be null.
	 * @return a new {@link FastaDataStore} instance; should never be null.
	 * @throws IOException if there is a problem creating the datastore from the file.
	 */
	@Override
	protected ProteinFastaFileDataStore createNewInstance(FastaParser parser, DataStoreProviderHint hint, Predicate<String> filter, 
			Predicate<ProteinFastaRecord> recordFilter, OptionalLong maxNumberofRecords)
			throws IOException {
		if(parser.isReadOnceOnly()){
			return DefaultProteinFastaDataStore.create(parser,filter, recordFilter);
		}
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultProteinFastaDataStore.create(parser,filter, recordFilter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
				return parser.canCreateMemento() ?						
						IndexedProteinFastaFileDataStore.create(parser,filter, recordFilter)
					:	DefaultProteinFastaDataStore.create(parser,filter, recordFilter);
			case ITERATION_ONLY: return LargeProteinFastaFileDataStore.create(parser,filter, recordFilter, maxNumberofRecords);
			default:
				throw new IllegalArgumentException("unknown provider hint :"+ hint);
		}
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStoreBuilder filter(Predicate<String> filter) {
		super.filter(filter);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStoreBuilder hint(DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}
	
	
	@Override
    public ProteinFastaFileDataStoreBuilder filterRecords(
            Predicate<ProteinFastaRecord> filter) {
       
        super.filterRecords(filter);
        return this;
    }
    /**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStore build() throws IOException {
		return super.build();
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStoreBuilder onlyIncludeIds(
			Set<String> ids) {
		super.onlyIncludeIds(ids);
		return this;
	}
	
	
}
