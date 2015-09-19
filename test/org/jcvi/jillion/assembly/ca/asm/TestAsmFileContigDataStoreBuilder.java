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

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.junit.Test;
public class TestAsmFileContigDataStoreBuilder {

	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(null, createMock(FragmentDataStore.class));
	}
	@Test(expected = NullPointerException.class)
	public void nullFrgDataStoreShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(new File("."), (FragmentDataStore) null);
	}
	@Test(expected = NullPointerException.class)
	public void nullNucleotideDataStoreShouldThrowNPE(){
		new AsmFileContigDataStoreBuilder(new File("."), (NucleotideSequenceDataStore) null);
	}
}
