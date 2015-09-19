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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.AbstractTestFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;

public class TestNucleotideFastaFileDataStoreBuilder extends AbstractTestFastaFileDataStoreBuilder
<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideFastaDataStore>{
	
	public TestNucleotideFastaFileDataStoreBuilder() throws IOException{
		super(new ResourceHelper(TestNucleotideFastaFileDataStoreBuilder.class), "files/19150.fasta");
	}

	@Override
	protected NucleotideFastaDataStore createDataStoreFromFile(File fasta)
			throws IOException {
		return new NucleotideFastaFileDataStoreBuilder(fasta).build();

	}

	@Override
	protected NucleotideFastaDataStore createDataStoreFromStream(InputStream in)
			throws FileNotFoundException, IOException {

		return new NucleotideFastaFileDataStoreBuilder(in).build();

	}
	
	@Override
	protected NucleotideFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint) throws IOException {
		return new NucleotideFastaFileDataStoreBuilder(fasta)
						.hint(hint)	
						.build();
	}

	@Override
	protected NucleotideFastaDataStore createDataStoreFromFile(File fasta,
			Predicate<String> filter) throws IOException {
		return new NucleotideFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.build();
	}

	@Override
	protected NucleotideFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint, Predicate<String> filter)
			throws IOException {
		return new NucleotideFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.hint(hint)	
		.build();
	}

	@Override
	protected Class<?> getClassImplForRanomdAccessOptizeMem() {
		return IndexedNucleotideSequenceFastaFileDataStore.class;
	}

	@Override
	protected Class<?> getClassImplForIterationOnly() {
		return LargeNucleotideSequenceFastaFileDataStore.class;
	}

	@Override
	protected NucleotideFastaDataStore createDataStoreFromStream(InputStream in,DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		
			return new NucleotideFastaFileDataStoreBuilder(in)
											.hint(hint)
											.build();
		
	}
	
	@Override
	protected NucleotideFastaDataStore createDataStoreFromStream(DataStoreProviderHint hint,
			Predicate<String> filter, InputStream in) throws IOException {
		return new NucleotideFastaFileDataStoreBuilder(in)
										.hint(hint)
										.filter(filter)
										.build();
	}
}
