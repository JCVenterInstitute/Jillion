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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilder;
/**
 * {@code NucleotideFastaFileDataStoreBuilder}
 * is a factory class that can create new instances
 * of {@link NucleotideFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class NucleotideFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideFastaDataStore>{

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public NucleotideFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		super(fastaFile);
	}
	
	public NucleotideFastaFileDataStoreBuilder(FastaParser parser) {
		super(parser);
	}

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public NucleotideFastaFileDataStoreBuilder(InputStream fastaFileStream)
			throws IOException {
		super(fastaFileStream);
	}
	@Override
	protected NucleotideFastaDataStore createNewInstance(
			FastaParser parser, DataStoreProviderHint providerHint, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter)
			throws IOException {
		if(parser.isReadOnceOnly()){
			return DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter);	
		}else{
			switch(providerHint){
				case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter);
				case RANDOM_ACCESS_OPTIMIZE_MEMORY: 
							return parser.canCreateMemento()?
										IndexedNucleotideSequenceFastaFileDataStore.create(parser,filter, recordFilter)
										:
										DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter);
				case ITERATION_ONLY: return LargeNucleotideSequenceFastaFileDataStore.create(parser,filter, recordFilter);
				default:
					throw new IllegalArgumentException("unknown provider hint : "+ providerHint);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStoreBuilder filter( Predicate<String> filter) {
		super.filter(filter);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStoreBuilder hint(DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}

	@Override
    public NucleotideFastaFileDataStoreBuilder filterRecords(Predicate<NucleotideFastaRecord> filter) {
        super.filterRecords(filter);
        return this;
    }

    /**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
