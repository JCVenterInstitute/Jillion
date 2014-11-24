package org.jcvi.jillion.assembly.util.slice;

import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.createSlice;
import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.seq;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestVariableNucleotideSliceMap {

	
	@Test
	public void noGapsSkipIncompleteNothingToSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void readHasNegativeOffset(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("NNACGTTA"), 3, Range.of(2,7))
											.add(2, new NucleotideSequenceBuilder("ACGTTAnnn").build())
											.add(5, new NucleotideSequenceBuilder(   "TTA").build())
											.add(5, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void veryGappy(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(
				                                        //codon splits            |   |     |      | 
				                                                              seq("AC-G-T--AC--T-G-T"), 3)
											.add(0, new NucleotideSequenceBuilder("AC-G-T--AC--T-G-T").build())
											.add(3, new NucleotideSequenceBuilder(   "G-T--AC--T-G-T").build())
											.add(10, new NucleotideSequenceBuilder(         "NNTTGGT").build())
											.add(12, new NucleotideSequenceBuilder(           "TTGGT").build())
											.add(5, new NucleotideSequenceBuilder(     "TTTAC--T-G-T").build())
											.build();
		
		assertEquals(17, actual.getConsensusLength());
		assertEquals(3, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("AC-G"),"AC-G"), actual.getSlice(0));
		assertEquals(createSlice(seq("-T--AC"),"-T--AC", "-T--AC"), actual.getSlice(1));
		assertEquals(createSlice(seq("--T-G-T"),"--T-G-T", "--T-G-T","NNTTGGT","--T-G-T"), actual.getSlice(2));
	}
	
	
	
	@Test
	public void readStartsInFrame1AndGoesBeyondReferenceSeqShouldIgnoreDownstream(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTANNNNNN").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	@Test
	public void readStartsInFrame2AndGoesBeyondReferenceSeqShouldIgnoreDownstream(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(1, new NucleotideSequenceBuilder( "CGTTANNNN").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	@Test
	public void readStartsInFrame2WithGapsAndGoesBeyondReferenceSeqShouldIgnoreDownstream(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(1, new NucleotideSequenceBuilder( "C-TTANNNN").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void readStartsInFrame3AndGoesBeyondReferenceSeqShouldIgnoreDownstream(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(2, new NucleotideSequenceBuilder(  "GTTANNNN").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void consensusHasGaps(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("AC-GTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("AC-GTTA").build())
											.add(4, new NucleotideSequenceBuilder(   "TTA").build())
											.add(4, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(7, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("AC-G"),"AC-G"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void consensusHasGapsMakeMapOfSubRange(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("AC-GTTANNNN"), 3, Range.of(0,6))
											.add(0, new NucleotideSequenceBuilder("AC-GTTA").build())
											.add(4, new NucleotideSequenceBuilder(   "TTA").build())
											.add(4, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(7, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("AC-G"),"AC-G"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void consensusHasGapsMakeMapOfSubRangeWithReadExtendingBeyond(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("AC-GTTANNNN"), 3, Range.of(0,6))
											.add(0, new NucleotideSequenceBuilder("AC-GTTATTTTTTTTTTTTTTT").build())
											.add(3, new NucleotideSequenceBuilder(   "GT-ATTTTT").build())
											.add(4, new NucleotideSequenceBuilder(    "GGACCCCCCCCCCCCCCCCCCCCCCCCC").build())
											.build();
		
		assertEquals(7, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("AC-G"),"AC-G"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"T-A", "TTA", "GGA"), actual.getSlice(1));
	}
	
	
	
	@Test
	public void readHasInternalGapsSkipIncompleteNothingToSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "T-A").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "T-A", "GGA"), actual.getSlice(1));
	}
	
	
	
	@Test
	public void readEndsInsideSliceShouldShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "TT").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "GGA"), actual.getSlice(1));
	}
	
	
	
	@Test
	public void readStartsFrame3InsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(2, new NucleotideSequenceBuilder(  "GTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	@Test
	public void readStartsFrame2InsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(1, new NucleotideSequenceBuilder( "CGTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void readStartsCondon2Frame2InsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTACCC"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTACCC").build())
											.add(1, new NucleotideSequenceBuilder( "CGTTACCC").build())
											.add(3, new NucleotideSequenceBuilder(   "GGACCC").build())
											.add(7, new NucleotideSequenceBuilder(       "CC").build())
											
											.build();
		
		assertEquals(9, actual.getConsensusLength());
		assertEquals(3, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
		assertEquals(createSlice(seq("CCC"),"CCC", "CCC", "CCC"), actual.getSlice(2));
	}
	@Test
	public void readStartsCondon2Frame3InsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTACCC"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTACCC").build())
											.add(1, new NucleotideSequenceBuilder( "CGTTACCC").build())
											.add(3, new NucleotideSequenceBuilder(   "GGACCC").build())
											.add(8, new NucleotideSequenceBuilder(        "C").build())
											
											.build();
		
		assertEquals(9, actual.getConsensusLength());
		assertEquals(3, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
		assertEquals(createSlice(seq("CCC"),"CCC", "CCC", "CCC"), actual.getSlice(2));
	}
	@Test
	public void readStartsCondon2Frame1InsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTACCC"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTACCC").build())
											.add(1, new NucleotideSequenceBuilder( "CGTTACCC").build())
											.add(3, new NucleotideSequenceBuilder(   "GGACCC").build())
											.add(6, new NucleotideSequenceBuilder(      "CCC").build())
											
											.build();
		
		assertEquals(9, actual.getConsensusLength());
		assertEquals(3, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
		assertEquals(createSlice(seq("CCC"),"CCC", "CCC", "CCC", "CCC"), actual.getSlice(2));
	}
	
	@Test
	public void gappedReadStartsInsideSliceShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(     seq("ACGTTA"), 3)
																.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
																.add(1, new NucleotideSequenceBuilder( "C-TTA").build())
																.add(3, new NucleotideSequenceBuilder(   "GGA").build())
																.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
	}
	
	
	@Test
	public void mixOfLeadingTrailingAndInternalGapsShouldSkip(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTA"), 3)
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(2, new NucleotideSequenceBuilder(  "GTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "G-A").build())
											.add(3, new NucleotideSequenceBuilder(   "GT").build())
											.add(2, new NucleotideSequenceBuilder(  "G-T").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG" ), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "G-A"), actual.getSlice(1));
	}
	@Test
	public void mixOfLeadingTrailingAndInternalGapsAndConsensusGaps(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(
				                                                              seq("A-C-GTT-A"), 3)
											.add(0, new NucleotideSequenceBuilder("ACC-GTTAA").build())
											.add(0, new NucleotideSequenceBuilder("A-CGGTT-A").build())
											.add(4, new NucleotideSequenceBuilder(    "GTT-A").build())
											.add(4, new NucleotideSequenceBuilder(    "G---A").build())
											.add(4, new NucleotideSequenceBuilder(    "G-TAA").build())
											.add(3, new NucleotideSequenceBuilder(   "G-T").build())
											.build();
		
		assertEquals(9, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("A-C-G"), "ACC-G","A-CGG" ), actual.getSlice(0));
		assertEquals(createSlice(seq("TT-A"), "TTAA", "TT-A", "TT-A","---A","-TAA"), actual.getSlice(1));
	}
	
	@Test
	public void consensusSubset(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("NA-C-GTT-A"), 3, Range.of(1, 9))
											.add(0, new NucleotideSequenceBuilder("NACC-GTTAA").build())
											.add(0, new NucleotideSequenceBuilder("NA-CGGTT-A").build())
											.add(5, new NucleotideSequenceBuilder(     "GTT-A").build())
											.add(5, new NucleotideSequenceBuilder(     "G---A").build())
											.add(5, new NucleotideSequenceBuilder(     "G-TAA").build())
											.add(5, new NucleotideSequenceBuilder(     "G-T").build())
											.build();
		
		assertEquals(9, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("A-C-G"), "ACC-G","A-CGG" ), actual.getSlice(0));
		assertEquals(createSlice(seq("TT-A"), "TTAA", "TT-A","TT-A", "---A", "-TAA"), actual.getSlice(1));
	}
	
	
	@Test
	public void splicedExons(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("AC-GTNTANNNN"), 3, Range.of(0,4), Range.of(6,7))
											.add(0, new NucleotideSequenceBuilder("AC-GT"+"N"+"TATTTTTTTTTTTTTTT").build())
											.add(3, new NucleotideSequenceBuilder(   "GT"+"N"+"-ATTTTT").build())
											.add(4, new NucleotideSequenceBuilder(    "G"+"N"+"GACCCCCCCCCCCCCCCCCCCCCCCCC").build())
											.build();
		
		assertEquals(7, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("AC-G"),"AC-G"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"T-A", "TTA", "GGA"), actual.getSlice(1));
	}
	
	@Test
	public void readStartsAfterCdsShouldBeSkipped(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("ACGTTANNNN"), 3, Range.ofLength(6))
											.add(0, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(3, new NucleotideSequenceBuilder(   "TTA").build())
											.add(3, new NucleotideSequenceBuilder(   "GGA").build())											
											//problem read
											.add(6, new NucleotideSequenceBuilder(      "NNNN").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
		
	}
	
	@Test
	public void readStartsBeforeCdsButIntersectsItToo(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("NNACGTTANNNN"), 3, Range.of(2,7))
											.add(2, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(5, new NucleotideSequenceBuilder(   "TTA").build())
											.add(5, new NucleotideSequenceBuilder(   "GGA").build())											
											//problem read
											.add(0, new NucleotideSequenceBuilder("NNACG").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG","ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA"), actual.getSlice(1));
		
	}
	
	@Test
	public void readStartsBeforeCdsButIntersectsItTooRegressionCheckDontHaveNegativeStartOffset(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("NNNNNNNNNNNNACGTTANNNN"), 3, Range.of(12,17))
											.add(12, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(15, new NucleotideSequenceBuilder(   "TTA").build())
											.add(15, new NucleotideSequenceBuilder(   "GGA").build())											
											//problem read
											.add(0, new NucleotideSequenceBuilder("NNNNNNNNNNNNACGTTANNNNNNNNNNNNNNNNNNN").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG","ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA", "TTA"), actual.getSlice(1));
		
	}
	
	@Test
	public void readStartsBeforeCdsButStartOffsetIsMoreThanRefSplicedLength(){
		VariableWidthNucleotideSliceMap actual = new VariableWidthNucleotideSliceMap.Builder(seq("NNNNNNNNNNNNNNNNNNNNNNACGTTA"), 3, Range.of(22,27))
											.add(22, new NucleotideSequenceBuilder("ACGTTA").build())
											.add(25, new NucleotideSequenceBuilder(   "TTA").build())
											.add(25, new NucleotideSequenceBuilder(   "GGA").build())											
											//problem read
											.add(10, new NucleotideSequenceBuilder("NNNNNNNNNNNNACGTTANNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN").build())
											.build();
		
		assertEquals(6, actual.getConsensusLength());
		assertEquals(2, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("ACG"),"ACG","ACG"), actual.getSlice(0));
		assertEquals(createSlice(seq("TTA"),"TTA", "TTA", "GGA", "TTA"), actual.getSlice(1));
		
	}
	
}
