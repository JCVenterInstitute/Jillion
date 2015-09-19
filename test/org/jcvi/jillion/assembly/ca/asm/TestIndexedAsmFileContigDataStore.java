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
package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ca.asm.AsmContigDataStore;
import org.jcvi.jillion.assembly.ca.asm.IndexedAsmFileContigDataStore;
import org.jcvi.jillion.assembly.ca.frg.Fragment;
import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

public class TestIndexedAsmFileContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws IOException {
		return IndexedAsmFileContigDataStore.create(asmFile, DataStoreUtil.adapt(NucleotideSequenceDataStore.class, frgDataStore, 
				new DataStoreUtil.AdapterCallback<Fragment, NucleotideSequence>() {

					@Override
					public NucleotideSequence get(Fragment from) {
						return from.getNucleotideSequence();
					}
			
		}),
		DataStoreFilters.alwaysAccept());
	}

}
