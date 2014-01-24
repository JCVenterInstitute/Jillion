package org.jcvi.jillion.sam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
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
	/**
	 * First read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits77(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(77);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.UNMAPPED,
				SamRecordFlags.NEXT_SEGMENT_IN_TEMPLATE_UNMAPPED,
				SamRecordFlags.FIRST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
	}
	/**
	 * Last read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits141(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(141);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.UNMAPPED,
				SamRecordFlags.NEXT_SEGMENT_IN_TEMPLATE_UNMAPPED,
				SamRecordFlags.LAST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void bits81(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(81);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.REVERSE_COMPLEMENTED,
				SamRecordFlags.FIRST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void bits161(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(161);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.NEXT_SEGMENT_IN_TEMPLATE_REVERSE_COMPLEMENTED,
				SamRecordFlags.LAST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
		
	}
	
	
	@Test
	public void bits97(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(97);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.NEXT_SEGMENT_IN_TEMPLATE_REVERSE_COMPLEMENTED,
				SamRecordFlags.FIRST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void bits145(){
		EnumSet<SamRecordFlags> actual = SamRecordFlags.parseFlags(145);
		EnumSet<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MULT_SEGMENTS,
				SamRecordFlags.REVERSE_COMPLEMENTED,
				SamRecordFlags.LAST_SEGMENT_IN_TEMPLATE
			);
	
		assertEquals(expected, actual);
		
	}
}
