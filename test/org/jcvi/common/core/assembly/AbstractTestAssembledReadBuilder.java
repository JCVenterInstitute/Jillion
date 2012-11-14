package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
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
			NucleotideSequence reference, String readId, NucleotideSequence validBases,
                            int offset, Direction dir, Range initialClearRange,
                            int ungappedFullLength);
	@Test
	public void noChanges(){
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							initialReadSequence, 0, dir, 
							initialClearRange, initialUngappedFullLength)
							.build();
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.append("ACGT").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.prepend("ACGT").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.insert(4,"ACGT").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceBaseWithNonGapShouldOnlyAffectSequence(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Thymine);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(4,Nucleotide.Thymine).build(),
							0, dir, 
							initialClearRange, 
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceBaseWithGapShouldOnlyShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Gap);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(4,Nucleotide.Gap).build(),
							0, dir, 
							new Range.Builder(initialClearRange).contractEnd(1).build(),
							initialUngappedFullLength-1)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceFirstBaseWithGapShouldOnlyShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(0,Nucleotide.Gap);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(0,Nucleotide.Gap).build(),
							0, dir, 
							new Range.Builder(initialClearRange).contractEnd(1).build(),
							initialUngappedFullLength-1)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void deleteSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		Range deleteRange = Range.of(CoordinateSystem.RESIDUE_BASED,1,4);
		builder.delete(deleteRange);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.delete(deleteRange).build(),
							0, dir, 
							new Range.Builder(initialClearRange).contractEnd(4).build(), 
							initialUngappedFullLength-4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.append("AC-T").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(),
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.prepend("AC-T").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(),
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.insert(4,"AC-T").build(),
							0, dir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(),
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void shiftShouldOnlyAffectStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.shift(4);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							initialReadSequence,
							4, dir, 
							initialClearRange, 
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void trimSequenceShouldAffectValidRangeAndUngappedFullLengthAndStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, dir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		Range trimRange = Range.of(CoordinateSystem.RESIDUE_BASED, 2, 6);
		builder.trim(trimRange);
		
		
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.trim(trimRange).build(),
							1, dir, 
							new Range.Builder(initialClearRange).contractBegin(1).contractEnd(2).build(),
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void trimReverseSequenceShouldAffectValidRangeAndUngappedFullLengthAndStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, Direction.REVERSE, 
									initialClearRange, 
									initialUngappedFullLength);
		
		Range trimRange = Range.of(CoordinateSystem.RESIDUE_BASED, 2, 6);
		builder.trim(trimRange);
		
		
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.trim(trimRange).build(),
							1, Direction.REVERSE, 
							new Range.Builder(initialClearRange).contractEnd(1).contractBegin(2).build(),
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	
	@Test
	public void appendReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.append("ACGT").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.prepend("ACGT").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"ACGT");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.insert(4,"ACGT").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(4).build(), 
							initialUngappedFullLength+4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void replaceReverseSeqBaseWithNonGapShouldOnlyAffectSequence(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Thymine);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(4,Nucleotide.Thymine).build(),
							0, revDir, 
							initialClearRange, 
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceReverseSeqBaseWithGapShouldShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(4,Nucleotide.Gap);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(4,Nucleotide.Gap).build(),
							0, revDir, 
							new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void replaceReverseSeqFirstBaseWithGapShouldShrinkClearRangeAndUngappedFullLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.replace(0,Nucleotide.Gap);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.replace(0,Nucleotide.Gap).build(),
							0, revDir, 
							new Range.Builder(initialClearRange).contractEnd(1).build(), 
							initialUngappedFullLength-1)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	@Test
	public void deleteReverseSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		Range deleteRange = Range.of(CoordinateSystem.RESIDUE_BASED,1,4);
		builder.delete(deleteRange);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.delete(deleteRange).build(),
							0, revDir, 
							new Range.Builder(initialClearRange).contractEnd(4).build(), 
							initialUngappedFullLength-4)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void appendReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.append("AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.append("AC-T").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void prependReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.prepend("AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.prepend("AC-T").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void insertReverseGappedSequenceShouldAlsoUpdateClearRangeAndUngappedLength(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.insert(4,"AC-T");
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							new NucleotideSequenceBuilder(initialReadSequence)
												.insert(4,"AC-T").build(),
							0, revDir, 
							new Range.Builder(initialClearRange).expandEnd(3).build(), 
							initialUngappedFullLength+3)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
	
	@Test
	public void shiftReverseShouldOnlyAffectStartOffset(){
		
		
		
		AssembledReadBuilder<R> builder = createReadBuilder(consensus, readId, 
									initialReadSequence, 0, revDir, 
									initialClearRange, 
									initialUngappedFullLength);
		
		builder.shift(4);
		R built = builder.build();
		R expected = createReadBuilder(consensus, readId, 
							initialReadSequence,
							4, revDir, 
							initialClearRange, 
							initialUngappedFullLength)
							.build();
		
		AssemblyTestUtil.assertPlacedReadCorrect(expected, built);
	}
}
