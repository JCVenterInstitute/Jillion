package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ctg.TigrContigDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.junit.Test;

public abstract class AbstractTestTasmDataStore { 
	protected static TigrContigDataStore contigDataStore;
	protected static TasmContigDataStore tasmDataStore;
	
	
	
	@Test
	public void PB2() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15044"),
				tasmDataStore.get("1122071329926"));
	}

	@Test
	public void PB1() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15045"),
				tasmDataStore.get("1122071329927"));
	}

	@Test
	public void PA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15046"),
				tasmDataStore.get("1122071329928"));
	}

	@Test
	public void NP() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15047"),
				tasmDataStore.get("1122071329929"));
	}

	@Test
	public void MP() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15048"),
				tasmDataStore.get("1122071329930"));
	}

	@Test
	public void NS() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15057"),
				tasmDataStore.get("1122071329931"));
	}

	@Test
	public void HA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("26303"),
				tasmDataStore.get("1122071329932"));
	}

	@Test
	public void NA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("27233"),
				tasmDataStore.get("1122071329933"));
	}

	@Test
	public void lastContig() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("27235"),
				tasmDataStore.get("1122071329934"));
	}

	private void assertContigDataMatches(
			Contig<? extends AssembledRead> contig, TasmContig tasm) {
		assertEquals("consensus", contig.getConsensusSequence(),
				tasm.getConsensusSequence());
		assertEquals("#reads", contig.getNumberOfReads(),
				tasm.getNumberOfReads());

		TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);

	}

}
