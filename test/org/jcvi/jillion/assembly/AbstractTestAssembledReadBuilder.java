/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public abstract class AbstractTestAssembledReadBuilder<R extends AssembledRead> {

	private final NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGTACGT")
												.build();
	
	private final NucleotideSequence initialReadSequence = new NucleotideSequenceBuilder("ACGTACGT")
															.build();
	private final String readId = "readId";
	private final Direction dir = Direction.FORWARD;
	private final Direction revDir = Direction.REVERSE;
	
	private final int initialNonValidLeft=4;
	
	private final int initialNonValidRight=5;
	Range initialClearRange = Range.of(CoordinateSystem.RESIDUE_BASED, initialNonValidLeft,initialNonValidRight+ 8);
	int initialUngappedFullLength = 8 + initialNonValidLeft+initialNonValidLeft;
	
	protected abstract AssembledReadBuilder<R> createReadBuilder(
			String readId, NucleotideSequence validBases, int offset,
                            Direction dir, Range initialClearRange, int ungappedFullLength);
	@Test
	public void noChanges(){
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, initialReadSequence, 
							0, dir, initialClearRange, 
							initialUngappedFullLength)
							.build(consensus);
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.append("ACGT").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.prepend("ACGT").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.insert(4,"ACGT").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceBaseWithNonGapShouldOnlyAffectSequence(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Thymine);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(4,Nucleotide.Thymine).build(), 
							0,
							dir, initialClearRange, 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceBaseWithGapShouldOnlyShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Gap);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(4,Nucleotide.Gap).build(), 
							0,
							dir, new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceFirstBaseWithGapShouldOnlyShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(0,Nucleotide.Gap);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(0,Nucleotide.Gap).build(), 
							0,
							dir, new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void deleteSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		Range deleteRange = Range.of(CoordinateSystem.RESIDUE_BASED,1,4);
		builder.delete(deleteRange);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.delete(deleteRange).build(), 
							0,
							dir, new Range.Builder(initialClearRange).contractEnd(4).build(), 
							initialUngappedFullLength-4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.append("AC-T").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.prepend("AC-T").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.insert(4,"AC-T").build(), 
							0,
							dir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void shiftShouldOnlyAffectStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.shift(4);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, initialReadSequence, 
							4,
							dir, initialClearRange, 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void trimSequenceThatLeavesStartingGapShouldHaveAdjustedValidRange(){
		
		Range initialClearRange = new Range.Builder(5)
										.shift(3)
										.build();
		AssembledReadBuilder<R> builder = createReadBuilder(readId, 
				new NucleotideSequenceBuilder("AC--GT-T").build(),
				
				0, dir, initialClearRange, 8);
		
		builder.trim(Range.of(2,7));
		
		assertEquals("--GT-T", builder.getCurrentNucleotideSequence().toString());
		assertEquals(new Range.Builder(initialClearRange)
									.contractBegin(2)
									.build(), 
									builder.getClearRange());
	}
	
	@Test
	public void trimSequenceThatLeavesEndingGapShouldHaveAdjustedValidRange(){
		
		Range initialClearRange = new Range.Builder(5)
										.shift(3)
										.build();
		AssembledReadBuilder<R> builder = createReadBuilder(readId, 
				new NucleotideSequenceBuilder("AC--GT-T").build(),
				
				0, dir, initialClearRange, 8);
		
		builder.trim(Range.of(0,6));
		
		assertEquals("AC--GT-", builder.getCurrentNucleotideSequence().toString());
		assertEquals(new Range.Builder(initialClearRange)
									.contractEnd(1)
									.build(), 
									builder.getClearRange());
	}
	
	
	
	@Test
	public void trimSequenceShouldAffectValidRangeAndUngappedFullLengthAndStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, dir, initialClearRange, 
									initialUngappedFullLength);
		
		Range trimRange = Range.of(CoordinateSystem.RESIDUE_BASED, 2, 6);
		builder.trim(trimRange);
		
		
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.trim(trimRange).build(), 
							1,
							dir, new Range.Builder(initialClearRange).contractBegin(1).contractEnd(2).build(), 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void trimReverseSequenceShouldAffectValidRangeAndUngappedFullLengthAndStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, Direction.REVERSE, initialClearRange, 
									initialUngappedFullLength);
		
		Range trimRange = Range.of(CoordinateSystem.RESIDUE_BASED, 2, 6);
		builder.trim(trimRange);
		
		
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.trim(trimRange).build(), 
							1,
							Direction.REVERSE, new Range.Builder(initialClearRange).contractEnd(1).contractBegin(2).build(), 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	
	@Test
	public void appendReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.append("ACGT").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.prepend("ACGT").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"ACGT");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.insert(4,"ACGT").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceReverseSeqBaseWithNonGapShouldOnlyAffectSequence(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Thymine);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(4,Nucleotide.Thymine).build(), 
							0,
							revDir, initialClearRange, 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceReverseSeqBaseWithGapShouldShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Gap);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(4,Nucleotide.Gap).build(), 
							0,
							revDir, new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceReverseSeqFirstBaseWithGapShouldShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(0,Nucleotide.Gap);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.replace(0,Nucleotide.Gap).build(), 
							0,
							revDir, new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void deleteReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		Range deleteRange = Range.of(CoordinateSystem.RESIDUE_BASED,1,4);
		builder.delete(deleteRange);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.delete(deleteRange).build(), 
							0,
							revDir, new Range.Builder(initialClearRange).contractEnd(4).build(), 
							initialUngappedFullLength-4)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.append("AC-T").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.prepend("AC-T").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"AC-T");
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, new NucleotideSequenceBuilder(initialReadSequence)
							.insert(4,"AC-T").build(), 
							0,
							revDir, new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void shiftReverseShouldOnlyAffectStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(readId, initialReadSequence, 
									0, revDir, initialClearRange, 
									initialUngappedFullLength);
		
		builder.shift(4);
		R built = builder.build(consensus);
		R expected = createReadBuilder(readId, initialReadSequence, 
							4,
							revDir, initialClearRange, 
							initialUngappedFullLength)
							.build(consensus);
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
}
