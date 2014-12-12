package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;
public class TestAlnFileParserLowercaseBases {

	@Test
	public void lowercase() throws IOException, DataStoreException{
		File in =new ResourceHelper(TestAlnFileParserLowercaseBases.class).getFile("files/mafft.aln");
		
		NucleotideSequenceDataStore datastore = GappedNucleotideAlignmentDataStore.createFromAlnFile(in);
		
		assertEquals(5, datastore.getNumberOfRecords());
		
		assertEquals(NucleotideSequenceTestUtil.create("--------------------------------------aatatcaagaaatcaag-----"),
										datastore.get("read1"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------aaatatcaagaaatcaag-----"),
				datastore.get("read2"));
		assertEquals(NucleotideSequenceTestUtil.create("--------------------------------------aatatcaagaaatcaag-----"),
				datastore.get("read3"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------aaatatcaagaaatcaag-----"),
				datastore.get("read4"));
		assertEquals(NucleotideSequenceTestUtil.create("-------------------------------------------tggttgaccaggtctaa"),
				datastore.get("read5"));
		
		
	}
}
