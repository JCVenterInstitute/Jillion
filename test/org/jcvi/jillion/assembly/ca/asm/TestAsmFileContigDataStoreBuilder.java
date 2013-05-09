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
