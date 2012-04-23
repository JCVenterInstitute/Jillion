package org.jcvi.common.core.assembly.asm;


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
import static org.junit.Assert.*;

public abstract class AbstractTestAsmUnitigDataStore extends AbstractTestAsmDataStore<UnitigDataStore>{

	@Test
	public void parseFluAssembly() throws Exception{
		File asmFile = resources.getFile("files/giv_15048.asm");
		File frgFile = resources.getFile("files/giv_15048.frg");
		FragmentDataStore frgDataStore = IndexedFragmentDataStore.create(frgFile);
		UnitigDataStore datastore = createDataStore(asmFile, frgDataStore);
	
		assertEquals(1, datastore.getNumberOfRecords());
		AsmUnitig unitig = datastore.get("7180000000000");
		NucleotideSequence consensus = unitig.getConsensus();
		assertEquals(1016, consensus.getLength());
		assertEquals(20, unitig.getNumberOfReads());
		
		String expectedConsensus =
		"GTAGCGAAAGCAGGTAGATATTTAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCG" + 
		"TCCCGTCAGGCCCCCTCAAAGCCGAGATCGCGCAGAGAATTGAAG-ATGTGTTTGCAGGGAAAAACACCG" + 
		"ATCTTGAGGCACTCATGGAATGGCTAAAGACAAGACCAATCCTGTCACCTCTGACTAAGGGGATTTTAGG" + 
		"ATTTGTGTTCACGCTCA-CCGTGCCCAGTGAGCGAGGACTGCAGCGTAGACGCTTTGTCCAGAATGCCCT" + 
		"TAATGGGAATGGGGATCCAAACAACATGGACAGAGCGGTCAAACTGTACAGAAAGCTTAAAAGGGAAATA" + 
		"ACATTTCATGGGGCAAAAGAAGTGGCACTCAGTTATTCAACCGGTGCACTTGCCAGTTGCATGGGCCTCA" + 
		"TATACAACAGGATGGGGACTGTGACCACTGAAGTGGCATTTGGTCTAGTGTGCGCCACGTGTGAGCAGAT" + 
		"TGCTGATTCCCAGCATCGGTCTCACAGACAAATGGTGACAACAACCAATCCCCTAATCAGACACGAGAAC" + 
		"AGAATGGTATTGGCCAGTACTACGGCTAAGGCCATGGAGCAAATGGCAGGGTCAAGTGAGCAGGCAGCAG" + 
		"AGGCTATGGAGGTTGCTAGTCAGGCCAGACAGATGGTGCAGGCAATGAGGACCATTGGGACTCATCCTAG" + 
		"CTCCAGTGCTGGTCTAAAAGATGATCTTCTTGAAAATTTGCAGGCCTACCAGAAACGGATGGGAGTGCAG" + 
		"ATGCAGCGATTCAAGTGATCCTCTCGTTATTGCCGCAAGTATCATTGGGATCTTGCACTTGATATTGTGG" + 
		"ATTCTTGATCGTCTTTTCTTCAAATGCATTTATCGTCGCCTTAAATACGGTTTGAAAAGAGGGCCTTCTA" + 
		"C-GGAAGGAGTGCCTGAGTCTATGAGGGAAGAATATCGGCAGGAACAGCAGAGTGCTGTGGATGTTGACG" + 
		"ATGGTCATTTTGTCAACATAGAGCTGGAGTAAAAAC";
		
		assertEquals(expectedConsensus, unitig.getConsensus().toString());
		
	//spot check a few reads
			verifyAReverseGappedRead(unitig,frgDataStore);
			verifyAForwardGappedRead(unitig, frgDataStore);
		}

			private void verifyAForwardGappedRead(AsmUnitig unitig,
					FragmentDataStore frg) throws DataStoreException {
				AsmPlacedRead read =unitig.getRead("1099820534711");
				assertEquals(Range.create(33,989), read.asRange());
				assertEquals(Direction.FORWARD, read.getDirection());
				assertEquals(Arrays.asList(82, 193 ,771 ),
						read.getNucleotideSequence().getGapOffsets());
				//clr 21,975
				Range expectedValidRange = Range.create(CoordinateSystem.SPACE_BASED,21,975);
				assertEquals(expectedValidRange, read.getValidRange());
				
				Fragment actualFragment =frg.get("1099820534711");
				//manually build gapped valid range and insert the gaps
				NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
											actualFragment.getBasecalls())
											.subSequence(expectedValidRange)
											.insert(82, Nucleotide.Gap)
											.insert(193, Nucleotide.Gap)
											.insert(771, Nucleotide.Gap)
											.build();
				
				assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
			}

			private void verifyAReverseGappedRead(AsmUnitig unitig, FragmentDataStore frg) throws DataStoreException {
				AsmPlacedRead read =unitig.getRead("1100010859106");
				assertEquals(Range.create(0,729), read.asRange());
				assertEquals(Direction.REVERSE, read.getDirection());
				
				assertEquals(Arrays.asList(115,226),
						read.getNucleotideSequence().getGapOffsets());
				//clr:27,755
				Range expectedValidRange = Range.create(CoordinateSystem.SPACE_BASED,27,755);
				assertEquals(expectedValidRange, read.getValidRange());
				Fragment actualFragment =frg.get("1100010859106");
				//manually build the reverse complemented gapped valid range and insert the gaps
				NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
											actualFragment.getBasecalls())
											.reverseComplement()
											.subSequence(AssemblyUtil.reverseComplimentValidRange(expectedValidRange, actualFragment.getLength()))
											.insert(115, Nucleotide.Gap)
											.insert(226, Nucleotide.Gap)
											.build();
				
				assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
			}
}
