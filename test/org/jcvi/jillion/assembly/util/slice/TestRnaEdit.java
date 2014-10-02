package org.jcvi.jillion.assembly.util.slice;

import static org.jcvi.jillion.assembly.util.slice.VariableWidthSliceTestUtil.seq;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.util.slice.CodonSliceMapBuilder.RnaEdit;
import org.jcvi.jillion.core.Range;
import org.junit.Before;
import org.junit.Test;
public class TestRnaEdit {

	RnaEdit sut = new RnaEdit(Range.of(10,16),
			seq("AAAAGGG"),
			seq("AAAAGGGGGGG"));
	
	@Before
	public void setup(){
		sut.editReference(seq("NNNNNNNNNNAAAAGGGNNNNNNN"));
		
	}
	
	@Test
	public void startOffsetBeforeEditRegionUnaffected(){
		for(int i=0; i< 10; i++){
			assertEquals(i, sut.adjustStartOffset(i));
		}		
	}
	@Test
	public void startOffsetAfterEditRegionShouldBeShiftedByNumberOfBasesAdded(){
		for(int i=17; i< 20; i++){
			assertEquals(i+4, sut.adjustStartOffset(i));
		}		
	}
	
	@Test
	public void startOffsetInsideEditRegionShouldBeUnaffected(){
		for(int i=10; i< 17; i++){
			assertEquals(i, sut.adjustStartOffset(i));
		}
	}
}
