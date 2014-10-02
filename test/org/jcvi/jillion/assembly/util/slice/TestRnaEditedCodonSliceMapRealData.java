package org.jcvi.jillion.assembly.util.slice;

import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.createSlice;
import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.seq;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jcvi.jillion.assembly.util.slice.CodonSliceMapBuilder.RnaEdit;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.junit.Before;
import org.junit.Test;

public class TestRnaEditedCodonSliceMapRealData {

	NucleotideSequence uneditedConsensus = seq("NNN--A-A-A-A--------A------G---G--G"+"CACAG--GAGAG-AAGTT");
	//											    -A-----------------------------"
	RnaEdit rnaEdit;
	
	@Before
	public void setup(){
		rnaEdit = new RnaEdit(Range.of(4,10),
				seq("AAAAGGG"),
				seq("AAAAGGGGGGG"));
	}
	
	@Test
	public void readStartsBeforeEditRegion(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(
				uneditedConsensus,
				Range.ofLength(uneditedConsensus.getUngappedLength()),
				rnaEdit
				)

		.add(0, seq("NNN--A-----------------------------" + "CACAG--GAGAG-AAGTT"))
		
		.build();
		
		assertEquals(uneditedConsensus.getLength() +4, actual.getConsensusLength());
		assertEquals(10, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NNN"), "NNN"), actual.getSlice(0));
		assertEquals(createSlice(seq("--A-A-A"), "--A----"), actual.getSlice(1));
		assertEquals(createSlice(seq("-A--------A------G"), "------------------"), actual.getSlice(2));
		assertEquals(createSlice(seq("---G--Gg"), "--------"), actual.getSlice(3));
		assertEquals(createSlice(seq("ggg"), "---"), actual.getSlice(4));
		assertEquals(createSlice(seq("CAC"), "CAC"), actual.getSlice(5));
		assertEquals(createSlice(seq("AG--G"), "AG--G"), actual.getSlice(6));
		assertEquals(createSlice(seq("AGA"), "AGA"), actual.getSlice(7));
		assertEquals(createSlice(seq("G-AA"), "G-AA"), actual.getSlice(8));
		assertEquals(createSlice(seq("GTT"), "GTT"), actual.getSlice(9));
	}
	@Test
	public void readStartsAtBeginingOfGappedEditRegion(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(
				uneditedConsensus,
				Range.ofLength(uneditedConsensus.getUngappedLength()),
				rnaEdit
				)

		.add(3, seq("--A-----------------------------" + "CACAG--GAGAG-AAGTT"))
		
		.build();
		
		assertEquals(uneditedConsensus.getLength() +4, actual.getConsensusLength());
		assertEquals(10, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NNN")), actual.getSlice(0));
		assertEquals(createSlice(seq("--A-A-A"), "--A----"), actual.getSlice(1));
		assertEquals(createSlice(seq("-A--------A------G"), "------------------"), actual.getSlice(2));
		assertEquals(createSlice(seq("---G--Gg"), "--------"), actual.getSlice(3));
		assertEquals(createSlice(seq("ggg"), "---"), actual.getSlice(4));
		assertEquals(createSlice(seq("CAC"), "CAC"), actual.getSlice(5));
		assertEquals(createSlice(seq("AG--G"), "AG--G"), actual.getSlice(6));
		assertEquals(createSlice(seq("AGA"), "AGA"), actual.getSlice(7));
		assertEquals(createSlice(seq("G-AA"), "G-AA"), actual.getSlice(8));
		assertEquals(createSlice(seq("GTT"), "GTT"), actual.getSlice(9));
	}
	
	@Test
	public void readStarts1bpIntoGappedEditRegion(){
		VariableWidthNucleotideSliceMap actual = new CodonSliceMapBuilder(
				uneditedConsensus,
				Range.ofLength(uneditedConsensus.getUngappedLength()),
				rnaEdit
				)

		.add(4, seq("-A-----------------------------" + "CACAG--GAGAG-AAGTT"))
		
		.build();
		
		assertEquals(uneditedConsensus.getLength() +4, actual.getConsensusLength());
		assertEquals(10, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NNN")), actual.getSlice(0));
		assertEquals(createSlice(seq("--A-A-A")), actual.getSlice(1));
		assertEquals(createSlice(seq("-A--------A------G"), "------------------"), actual.getSlice(2));
		assertEquals(createSlice(seq("---G--Gg"), "--------"), actual.getSlice(3));
		assertEquals(createSlice(seq("ggg"), "---"), actual.getSlice(4));
		assertEquals(createSlice(seq("CAC"), "CAC"), actual.getSlice(5));
		assertEquals(createSlice(seq("AG--G"), "AG--G"), actual.getSlice(6));
		assertEquals(createSlice(seq("AGA"), "AGA"), actual.getSlice(7));
		assertEquals(createSlice(seq("G-AA"), "G-AA"), actual.getSlice(8));
		assertEquals(createSlice(seq("GTT"), "GTT"), actual.getSlice(9));
	}
	@Test
	public void readStartsIntoGappedEditRegion(){
		CodonSliceMapBuilder builder = new CodonSliceMapBuilder(
				uneditedConsensus,
				Range.ofLength(uneditedConsensus.getUngappedLength()),
				rnaEdit
				);
		String readSeq = "-A-----------------------------"+ "CACAG--GAGAG-AAGTT";
		for(int i=0; i<7; i++){
			builder.add(4+i, seq(readSeq.substring(i)));
		}
		 VariableWidthNucleotideSliceMap actual= builder.build();
		
		assertEquals(uneditedConsensus.getLength() +4, actual.getConsensusLength());
		assertEquals(10, actual.getNumberOfSlices());
		
		assertEquals(createSlice(seq("NNN")), actual.getSlice(0));
		assertEquals(createSlice(seq("--A-A-A")), actual.getSlice(1));
		assertEquals(createSlice(seq("-A--------A------G"), times("------------------", 7)), actual.getSlice(2));
		assertEquals(createSlice(seq("---G--Gg"), times("--------",7)), actual.getSlice(3));
		assertEquals(createSlice(seq("ggg"), times("---",7)), actual.getSlice(4));
		assertEquals(createSlice(seq("CAC"), times("CAC",7)), actual.getSlice(5));
		assertEquals(createSlice(seq("AG--G"), times("AG--G",7)), actual.getSlice(6));
		assertEquals(createSlice(seq("AGA"), times("AGA",7)), actual.getSlice(7));
		assertEquals(createSlice(seq("G-AA"), times("G-AA",7)), actual.getSlice(8));
		assertEquals(createSlice(seq("GTT"), times("GTT",7)), actual.getSlice(9));
	}
	
	private static String[] times(String seq, int x){
		String[] array = new String[x];
		Arrays.fill(array, seq);
		return array;
	}
	
	
}
