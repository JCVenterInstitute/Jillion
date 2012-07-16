package org.jcvi.common.core.assembly.clc.cas.var;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.common.core.assembly.clc.cas.var.Variation.Type;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDefaultReferenceVariationsDataStore {

	private ReferenceVariationsDataStore sut;
	public TestDefaultReferenceVariationsDataStore() throws IOException{
		ResourceFileServer resources = new ResourceFileServer(TestDefaultReferenceVariationsDataStore.class);
	
		File logFile =resources.getFile("giv3_AK_30279_hybrid_edited_refs_find_variations.log");
		sut = DefaultReferenceVariationsDataStore.createFromLogFile(logFile);
	}
	@Test
	public void allReferencesAccountedFor() throws DataStoreException{
		assertEquals(8, sut.getNumberOfRecords());
	}
	
	@Test
	public void idIterator() throws DataStoreException{
		Iterator<String> expectedIter = Arrays.asList("HA","MP","NA","NP","NS","PA","PB1","PB2").iterator();
	
		CloseableIterator<String> actual = sut.idIterator();
		while(expectedIter.hasNext()){
			assertEquals(expectedIter.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	@Test
	public void referenceWithNoVariants() throws DataStoreException{
		ReferenceVariations rv = sut.get("NA");
		assertEquals("NA",rv.getReferenceId());
		
		CloseableIterator<Variation> iter = rv.getVariationIterator();
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void oneVariant() throws DataStoreException{
		ReferenceVariations rv = sut.get("PA");
		assertEquals("PA",rv.getReferenceId());
		
		CloseableIterator<Variation> iter = rv.getVariationIterator();
		assertTrue(iter.hasNext());
		Variation variation = iter.next();
		assertEquals(new DefaultVariation.Builder(2205, Type.DELETION, Nucleotide.Guanine, Nucleotide.Gap)
						.addHistogramRecord(Nucleotide.Adenine, 5)
						.addHistogramRecord(Nucleotide.Cytosine, 0)
						.addHistogramRecord(Nucleotide.Guanine, 13)
						.addHistogramRecord(Nucleotide.Thymine, 0)
						.addHistogramRecord(Nucleotide.Unknown, 0)
						.addHistogramRecord(Nucleotide.Gap, 72)
						.build(),
						
				variation);
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void severalVariants() throws DataStoreException{
		ReferenceVariations rv = sut.get("NP");
		assertEquals("NP",rv.getReferenceId());
		
		CloseableIterator<Variation> iter = rv.getVariationIterator();
		assertTrue(iter.hasNext());
		assertEquals(new DefaultVariation.Builder(55, Type.NO_CHANGE, Nucleotide.Gap, Nucleotide.Gap)
						.addHistogramRecord(Nucleotides.parse("GG"), 4)
						.addHistogramRecord(Nucleotide.Gap, 12)
						.build(),
						
						iter.next());
		
		assertEquals(new DefaultVariation.Builder(60, Type.NO_CHANGE, Nucleotide.Gap, Nucleotide.Gap)
						.addHistogramRecord(Nucleotides.parse("G"), 4)
						.addHistogramRecord(Nucleotide.Gap, 12)
						.build(),
						
						iter.next());
		assertEquals(new DefaultVariation.Builder(1284, Type.NO_CHANGE, Nucleotide.Thymine, Nucleotide.Thymine)
							.addHistogramRecord(Nucleotide.Adenine, 0)
							.addHistogramRecord(Nucleotide.Cytosine, 1)
							.addHistogramRecord(Nucleotide.Guanine, 0)
							.addHistogramRecord(Nucleotide.Thymine, 39)
							.addHistogramRecord(Nucleotide.Unknown, 0)
							.addHistogramRecord(Nucleotide.Gap, 11)
						.build(),
						
						iter.next());
		assertFalse(iter.hasNext());
	}
}
