/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.asm;


import java.io.File;
import java.util.Arrays;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.asm.AsmAssembledRead;
import org.jcvi.jillion.assembly.asm.AsmUnitig;
import org.jcvi.jillion.assembly.asm.UnitigDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.frg.Fragment;
import org.jcvi.jillion.trace.frg.FragmentDataStore;
import org.jcvi.jillion.trace.frg.IndexedFragmentDataStore;
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
		NucleotideSequence consensus = unitig.getConsensusSequence();
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
		
		assertEquals(expectedConsensus, unitig.getConsensusSequence().toString());
		
	//spot check a few reads
			verifyAReverseGappedRead(unitig,frgDataStore);
			verifyAForwardGappedRead(unitig, frgDataStore);
		}

			private void verifyAForwardGappedRead(AsmUnitig unitig,
					FragmentDataStore frg) throws DataStoreException {
				AsmAssembledRead read =unitig.getRead("1099820534711");
				assertEquals(Range.of(33,989), read.asRange());
				assertEquals(Direction.FORWARD, read.getDirection());
				assertEquals(Arrays.asList(82, 193 ,771 ),
						read.getNucleotideSequence().getGapOffsets());
				//clr 21,975
				Range expectedValidRange = Range.of(CoordinateSystem.SPACE_BASED,21,975);
				assertEquals(expectedValidRange, read.getReadInfo().getValidRange());
				
				Fragment actualFragment =frg.get("1099820534711");
				//manually build gapped valid range and insert the gaps
				NucleotideSequence expectedGappedSequence =new NucleotideSequenceBuilder(
											actualFragment.getNucleotideSequence())
											.trim(expectedValidRange)
											.insert(82, Nucleotide.Gap)
											.insert(193, Nucleotide.Gap)
											.insert(771, Nucleotide.Gap)
											.build();
				
				assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
			}

			private void verifyAReverseGappedRead(AsmUnitig unitig, FragmentDataStore frg) throws DataStoreException {
				AsmAssembledRead read =unitig.getRead("1100010859106");
				assertEquals(Range.of(0,729), read.asRange());
				assertEquals(Direction.REVERSE, read.getDirection());
				
				assertEquals(Arrays.asList(115,226),
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
											.insert(115, Nucleotide.Gap)
											.insert(226, Nucleotide.Gap)
											.build();
				
				assertEquals(expectedGappedSequence.toString(), read.getNucleotideSequence().toString());
			}
}
