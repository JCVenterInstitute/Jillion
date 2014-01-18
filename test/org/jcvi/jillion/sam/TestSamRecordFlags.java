package org.jcvi.jillion.sam;

import java.util.EnumSet;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestSamRecordFlags {

	@Test
	public void bitsOfZeroMeansEmpty(){
		assertTrue(SamRecordFlags.parseFlags(0).isEmpty());
	}
	@Test
	public void bits163(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(163);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.HAS_MULT_SEGMENTS,
					SamRecordFlags.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlags.LAST_SEGMENT_IN_TEMPLATE,
					SamRecordFlags.NEXT_SEGMENT_IN_TEMPLATE_REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void bits2064(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(2064);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.SUPPLEMENTARY_ALIGNMENT,
					SamRecordFlags.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void bits83(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(83);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.HAS_MULT_SEGMENTS,
					SamRecordFlags.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlags.FIRST_SEGMENT_IN_TEMPLATE,
					SamRecordFlags.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
	}
}
