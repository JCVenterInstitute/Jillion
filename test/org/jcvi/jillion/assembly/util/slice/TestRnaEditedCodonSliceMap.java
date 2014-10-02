package org.jcvi.jillion.assembly.util.slice;

import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.createSlice;
import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.seq;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.util.slice.CodonSliceMapBuilder.RnaEdit;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestRnaEditedCodonSliceMap {

	@Test
	public void allReadsFullLengthNoGaps(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAAGGGTTT"))
													.build();
		
		assertEquals(15, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT"), actual.getSlice(4));
		
	}
	
	
	
	@Test
	public void downStreamReadShouldBeShifted(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAAGGGTTT"))
													.add(8, seq(        "TTT"))
													.build();
		
		assertEquals(15, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT", "TTT"), actual.getSlice(4));
		
	}
	
	@Test
	public void upstreamReadShouldNotBeAffected(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("N"))
													.build();
		
		assertEquals(15, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT"), actual.getSlice(4));
		
	}
	
	@Test
	public void downstreamGapsNotAffected(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTT-T"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTT-T"))
													.add(0, seq("NAAAAGGGTT-T"))
													.add(8, seq(        "TT-T"))
													.build();
		
		assertEquals(16, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TT-T"), "TT-T","TT-T", "TT-T"), actual.getSlice(4));
		
	}
	
	@Test
	public void partialReadEndsInsideEditAreaShouldReplaceRegionWithGaps(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAA"))
													.build();
		
		assertEquals(15, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT"), actual.getSlice(4));
		
	} 
	
	@Test
	public void partialReadStartsInsideEditAreaShouldReplaceRegionWithGaps(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NAAAAGGGTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NAAAAGGGTTT"))
													.add(0, seq("NAAAAGGGTTT"))
													.add(6, seq(      "GGTTT"))
													.build();
		
		assertEquals(15, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		//should make 	NAA|AAG|GGg|ggg|TTT
		//				---------------
		//				NAA|AAG|GGg|ggg|TTT
		//				NAA|AAG|GGg|ggg|TTT"))
		//				        GG-|---|TTT"))
		
		assertEquals(createSlice(seq("NAA"), "NAA","NAA"), actual.getSlice(0));
		assertEquals(createSlice(seq("AAG"), "AAG","AAG"), actual.getSlice(1));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG","GG-"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG","---"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT","TTT"), actual.getSlice(4));
		
	} 
	
	
	@Test
	public void editRegionContainsGaps(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(seq("NA-A-A--------A------G---G--GTTT"), Range.ofLength(11),
															new RnaEdit(Range.of(1,7),
																	seq("AAAAGGG"),
																	seq("AAAAGGGGGGG")))
		
													.add(0, seq("NA-A-A--------A------G---G--GTTT"))
													.add(0, seq("NA-A-A--------A------G---G--GTTT"))
													.build();
		
		assertEquals(36, actual.getConsensusLength());
		assertEquals(5, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NA-A"), "NA-A","NA-A"), actual.getSlice(0));
		
		assertEquals(createSlice(seq("-A--------A------G"), "-A--------A------G","-A--------A------G"), actual.getSlice(1));
		assertEquals(createSlice(seq("---G--GG"), "---G--GG","---G--GG"), actual.getSlice(2));
		assertEquals(createSlice(seq("GGG"), "GGG","GGG"), actual.getSlice(3));
		assertEquals(createSlice(seq("TTT"), "TTT","TTT"), actual.getSlice(4));
		
	}
	
	
	
	
}
