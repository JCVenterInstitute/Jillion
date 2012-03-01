package org.jcvi.common.core.assembly.asm;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.frg.Fragment;
import org.jcvi.common.core.seq.read.trace.frg.FragmentDataStore;
import org.jcvi.common.core.seq.read.trace.frg.IndexedFragmentDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

public abstract class AbstractTestAsmContigDataStore {
ResourceFileServer resources = new ResourceFileServer(AbstractTestAsmContigDataStore.class);
	protected abstract AsmContigDataStore createDataStore(File asmFile, FragmentDataStore frgDataStore) throws Exception;
	
	@Test
	public void parseFluAssembly() throws Exception{
		File asmFile = resources.getFile("files/giv_15048.asm");
		File frgFile = resources.getFile("files/giv_15048.frg");
		FragmentDataStore frgDataStore = IndexedFragmentDataStore.create(frgFile);
		AsmContigDataStore datastore = createDataStore(asmFile, frgDataStore);
	
		assertEquals(1, datastore.size());
		AsmContig contig = datastore.get("7180000000001");
		NucleotideSequence consensus = contig.getConsensus();
		assertEquals(1017, consensus.getLength());
		assertEquals(20, contig.getNumberOfReads());
		
		String expectedConsensus =
		"GTAGCGAAAGCAGGTAGATATTTAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCG" +		
		"TCCCGTCAGG-CCCCCTCAAAGCCGAGATCGCGCAGAGAATTGAAG-ATGTGTTTGCAGGGAAAAACACC" +
		"GATCTTGAGGCACTCATGGAATGGCTAAAGACAAGACCAATCCTGTCACCTCTGACTAAGGGGATTTTAG" +
		"GATTTGTGTTCACGCTCA-CCGTGCCCAGTGAGCGAGGACTGCAGCGTAGACGCTTTGTCCAGAATGCCC" +
		"TTAATGGGAATGGGGATCCAAACAACATGGACAGAGCGGTCAAACTGTACAGAAAGCTTAAAAGGGAAAT" +
		"AACATTTCATGGGGCAAAAGAAGTGGCACTCAGTTATTCAACCGGTGCACTTGCCAGTTGCATGGGCCTC" +
		"ATATACAACAGGATGGGGACTGTGACCACTGAAGTGGCATTTGGTCTAGTGTGCGCCACGTGTGAGCAGA" +
		"TTGCTGATTCCCAGCATCGGTCTCACAGACAAATGGTGACAACAACCAATCCCCTAATCAGACACGAGAA" +
		"CAGAATGGTATTGGCCAGTACTACGGCTAAGGCCATGGAGCAAATGGCAGGGTCAAGTGAGCAGGCAGCA" +
		"GAGGCTATGGAGGTTGCTAGTCAGGCCAGACAGATGGTGCAGGCAATGAGGACCATTGGGACTCATCCTA" +
		"GCTCCAGTGCTGGTCTAAAAGATGATCTTCTTGAAAATTTGCAGGCCTACCAGAAACGGATGGGAGTGCA" +
		"GATGCAGCGATTCAAGTGATCCTCTCGTTATTGCCGCAAGTATCATTGGGATCTTGCACTTGATATTGTG" +
		"GATTCTTGATCGTCTTTTCTTCAAATGCATTTATCGTCGCCTTAAATACGGTTTGAAAAGAGGGCCTTCT" +
		"AC-GGAAGGAGTGCCTGAGTCTATGAGGGAAGAATATCGGCAGGAACAGCAGAGTGCTGTGGATGTTGAC" +
		"GATGGTCATTTTGTCAACATAGAGCTGGAGTAAAAAC";
		
		assertEquals(expectedConsensus, consensus.toString());
		
		//spot check a few reads
		verifyAReverseGappedRead(contig,frgDataStore);
		verifyAForwardGappedRead(contig, frgDataStore);
	}

	private void verifyAForwardGappedRead(AsmContig contig,
			FragmentDataStore frg) throws DataStoreException {
		AsmPlacedRead read =contig.getPlacedReadById("1099820534711");
		assertEquals(Range.buildRange(33,990), read.asRange());
		assertEquals(Direction.FORWARD, read.getDirection());
		
		assertEquals(Arrays.asList(47, 82, 193 ,771 ),
				read.getNucleotideSequence().getGapOffsets());
		//clr 21,975
		Range expectedValidRange = Range.buildRange(CoordinateSystem.SPACE_BASED,21,975);
		assertEquals(expectedValidRange, read.getValidRange());
		
		Fragment actualFragment =frg.get("1099820534711");
		//manually build gapped valid range and insert the gaps
		NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
									actualFragment.getBasecalls())
									.subSequence(expectedValidRange)
									.insert(47, Nucleotide.Gap)
									.insert(82, Nucleotide.Gap)
									.insert(193, Nucleotide.Gap)
									.insert(771, Nucleotide.Gap)
									.build();
		
		assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
	}

	private void verifyAReverseGappedRead(AsmContig contig, FragmentDataStore frg) throws DataStoreException {
		AsmPlacedRead read =contig.getPlacedReadById("1100010859106");
		assertEquals(Range.buildRange(0,730), read.asRange());
		assertEquals(Direction.REVERSE, read.getDirection());
		
		assertEquals(Arrays.asList(80,115,226),
				read.getNucleotideSequence().getGapOffsets());
		//clr:27,755
		Range expectedValidRange = Range.buildRange(CoordinateSystem.SPACE_BASED,27,755);
		assertEquals(expectedValidRange, read.getValidRange());
		Fragment actualFragment =frg.get("1100010859106");
		//manually build the reverse complimented gapped valid range and insert the gaps
		NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
									actualFragment.getBasecalls())
									.reverseCompliment()
									.subSequence(AssemblyUtil.reverseComplimentValidRange(expectedValidRange, actualFragment.getLength()))
									.insert(80, Nucleotide.Gap)
									.insert(115, Nucleotide.Gap)
									.insert(226, Nucleotide.Gap)
									.build();
		
		assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
	}
}
