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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.fasta.AbstractTestFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.aa.IndexedProteinFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.aa.LargeProteinFastaFileDataStore;

public class TestProteinFastaFileDataStoreBuilder extends AbstractTestFastaFileDataStoreBuilder
<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinFastaDataStore>{
	
	public TestProteinFastaFileDataStoreBuilder() throws IOException{
		super(new ResourceHelper(TestProteinFastaFileDataStoreBuilder.class), "files/example.aa.fasta");
	}

	@Override
	protected ProteinFastaDataStore createDataStoreFromFile(File fasta)
			throws IOException {
		return new ProteinFastaFileDataStoreBuilder(fasta).build();

	}

	@Override
	protected ProteinFastaDataStore createDataStoreFromStream(InputStream in)
			throws FileNotFoundException, IOException {

		return new ProteinFastaFileDataStoreBuilder(in).build();

	}
	
	@Override
	protected ProteinFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint) throws IOException {
		return new ProteinFastaFileDataStoreBuilder(fasta)
						.hint(hint)	
						.build();
	}

	@Override
	protected ProteinFastaDataStore createDataStoreFromFile(File fasta,
			Predicate<String> filter) throws IOException {
		return new ProteinFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.build();
	}

	@Override
	protected ProteinFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint, Predicate<String> filter)
			throws IOException {
		return new ProteinFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.hint(hint)	
		.build();
	}

	@Override
	protected Class<?> getClassImplForRanomdAccessOptizeMem() {
		return IndexedProteinFastaFileDataStore.Impl.class;
	}

	@Override
	protected Class<?> getClassImplForIterationOnly() {
		return LargeProteinFastaFileDataStore.class;
	}

	@Override
	protected ProteinFastaDataStore createDataStoreFromStream(InputStream in,DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		
			return new ProteinFastaFileDataStoreBuilder(in)
											.hint(hint)
											.build();
		
	}
	
	@Override
	protected ProteinFastaDataStore createDataStoreFromStream(DataStoreProviderHint hint,
			Predicate<String> filter, InputStream in) throws IOException {
		return new ProteinFastaFileDataStoreBuilder(in)
										.hint(hint)
										.filter(filter)
										.build();
	}
}
