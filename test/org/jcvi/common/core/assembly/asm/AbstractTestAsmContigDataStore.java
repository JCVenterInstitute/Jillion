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
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public abstract class AbstractTestAsmContigDataStore extends AbstractTestAsmDataStore<AsmContigDataStore>{
	
	@Test
	public void parseFluAssembly() throws Exception{
		File asmFile = resources.getFile("files/giv_15048.asm");
		File frgFile = resources.getFile("files/giv_15048.frg");
		FragmentDataStore frgDataStore = IndexedFragmentDataStore.create(frgFile);
		AsmContigDataStore datastore = createDataStore(asmFile, frgDataStore);
	
		assertEquals(1, datastore.getNumberOfRecords());
		AsmContig contig = datastore.get("7180000000001");
		NucleotideSequence consensus = contig.getConsensusSequence();
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
		AsmAssembledRead read =contig.getRead("1099820534711");
		assertEquals(Range.of(33,990), read.asRange());
		assertEquals(Direction.FORWARD, read.getDirection());
		
		assertEquals(Arrays.asList(47, 82, 193 ,771 ),
				read.getNucleotideSequence().getGapOffsets());
		//clr 21,975
		Range expectedValidRange = Range.of(CoordinateSystem.SPACE_BASED,21,975);
		assertEquals(expectedValidRange, read.getReadInfo().getValidRange());
		
		Fragment actualFragment =frg.get("1099820534711");
		//manually build gapped valid range and insert the gaps
		NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
									actualFragment.getNucleotideSequence())
									.trim(expectedValidRange)
									.insert(47, Nucleotide.Gap)
									.insert(82, Nucleotide.Gap)
									.insert(193, Nucleotide.Gap)
									.insert(771, Nucleotide.Gap)
									.build();
		
		assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
	}

	private void verifyAReverseGappedRead(AsmContig contig, FragmentDataStore frg) throws DataStoreException {
		AsmAssembledRead read =contig.getRead("1100010859106");
		assertEquals(Range.of(0,730), read.asRange());
		assertEquals(Direction.REVERSE, read.getDirection());
		
		assertEquals(Arrays.asList(80,115,226),
				read.getNucleotideSequence().getGapOffsets());
		//clr:27,755
		Range expectedValidRange = Range.of(CoordinateSystem.SPACE_BASED,27,755);
		assertEquals(expectedValidRange, read.getReadInfo().getValidRange());
		Fragment actualFragment =frg.get("1100010859106");
		Range reverseValidRange = AssemblyUtil.reverseComplementValidRange(expectedValidRange, actualFragment.getNucleotideSequence().getUngappedLength());
		//manually build the reverse complemented gapped valid range and insert the gaps
		NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
									actualFragment.getNucleotideSequence())
									.reverseComplement()
									.trim(reverseValidRange)
									.insert(80, Nucleotide.Gap)
									.insert(115, Nucleotide.Gap)
									.insert(226, Nucleotide.Gap)
									.build();
		
		assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
	}
}
