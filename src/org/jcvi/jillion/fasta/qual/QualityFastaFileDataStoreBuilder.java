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
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.fasta.qual.IndexedQualityFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;

/**
 * {@code QualityFastaFileDataStoreBuilder}
 * is a factory class that can create new instances
 * of {@link QualityFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class QualityFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<PhredQuality, QualitySequence, QualityFastaRecord, QualityFastaDataStore>{

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public QualityFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		super(fastaFile);
	}
	
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public QualityFastaFileDataStoreBuilder(InputStream fastaFileAsStream)
			throws IOException {
		super(fastaFileAsStream);
	}

	
	@Override
	protected QualityFastaDataStore createNewInstance(FastaParser parser,
			DataStoreProviderHint hint,Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter)
			throws IOException {
		if(parser.isReadOnceOnly()){
			return DefaultQualityFastaFileDataStore.create(parser,filter, recordFilter); 
		}
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultQualityFastaFileDataStore.create(parser,filter, recordFilter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY: 
				return parser.canCreateMemento() ?
						IndexedQualityFastaFileDataStore.create(parser,filter, recordFilter)
						: DefaultQualityFastaFileDataStore.create(parser,filter, recordFilter);
						
			case ITERATION_ONLY: return LargeQualityFastaFileDataStore.create(parser,filter, recordFilter);
			default:
				throw new IllegalArgumentException("unknown hint : "+ hint);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualityFastaFileDataStoreBuilder filter(Predicate<String> filter) {
		super.filter(filter);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualityFastaFileDataStoreBuilder hint(DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualityFastaFileDataStoreBuilder filterRecords(Predicate<QualityFastaRecord> filter) {
		super.filterRecords(filter);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public QualityFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
