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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.IOException;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.tasm.TasmContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;

public class TestLargeTasmDataStore extends AbstractTestTasmDataStore{

	static{
		ResourceHelper resources = new ResourceHelper(TestLargeTasmDataStore.class);

		NucleotideFastaDataStore fullLengthFastas;
		try {
			fullLengthFastas = new NucleotideFastaFileDataStoreBuilder(
					resources.getFile("files/giv-15050.fasta")).hint(
					DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
					.build();
			
			contigDataStore = new TigrContigFileDataStoreBuilder(
					resources.getFile("files/giv-15050.contig"),
					fullLengthFastas).build();
			tasmDataStore = new TasmContigFileDataStoreBuilder(resources.getFile("files/giv-15050.tasm"),	fullLengthFastas)
								.hint(DataStoreProviderHint.ITERATION_ONLY)						
								.build();
		} catch (IOException e) {
			throw new IllegalStateException("error creating datastores",e);
		}
	}

}
