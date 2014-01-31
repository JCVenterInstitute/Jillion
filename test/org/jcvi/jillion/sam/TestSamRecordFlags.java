package org.jcvi.jillion.sam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;
public class TestSamRecordFlags {

	@Test(expected = IllegalArgumentException.class)
	public void negativeFlagsShouldThrowException(){
		SamRecordFlags.parseFlags(-1);
	}
	@Test(expected = NullPointerException.class)
	public void nullAsBitsShouldThrowNPE(){
		SamRecordFlags.asBits(null);
	}
	@Test
	public void bitsOfZeroMeansEmpty(){
		assertTrue(SamRecordFlags.parseFlags(0).isEmpty());
		assertEquals(0, SamRecordFlags.asBits(Collections.<SamRecordFlags>emptySet()));
	}
	@Test
	public void bits163(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(163);
		Set<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.HAS_MATE_PAIR,
					SamRecordFlags.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlags.SECOND_MATE_OF_PAIR,
					SamRecordFlags.MATE_REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(163, SamRecordFlags.asBits(expected));
	}
	
	@Test
	public void bits2064(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(2064);
		Set<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.SUPPLEMENTARY_ALIGNMENT,
					SamRecordFlags.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(2064, SamRecordFlags.asBits(expected));
	}
	
	@Test
	public void bits83(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(83);
		Set<SamRecordFlags> expected = EnumSet.of(
				
					SamRecordFlags.HAS_MATE_PAIR,
					SamRecordFlags.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlags.FIRST_MATE_OF_PAIR,
					SamRecordFlags.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(83, SamRecordFlags.asBits(expected));
	}
	/**
	 * First read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits77(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(77);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.READ_UNMAPPED,
				SamRecordFlags.MATE_UNMAPPED,
				SamRecordFlags.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(77, SamRecordFlags.asBits(expected));
	}
	/**
	 * Last read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits141(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(141);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.READ_UNMAPPED,
				SamRecordFlags.MATE_UNMAPPED,
				SamRecordFlags.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(141, SamRecordFlags.asBits(expected));
	}
	
	@Test
	public void bits81(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(81);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.REVERSE_COMPLEMENTED,
				SamRecordFlags.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(81, SamRecordFlags.asBits(expected));
	}
	
	@Test
	public void bits161(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(161);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.MATE_REVERSE_COMPLEMENTED,
				SamRecordFlags.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(161, SamRecordFlags.asBits(expected));
	}
	
	
	@Test
	public void bits97(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(97);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.MATE_REVERSE_COMPLEMENTED,
				SamRecordFlags.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(97, SamRecordFlags.asBits(expected));
	}
	
	@Test
	public void bits145(){
		Set<SamRecordFlags> actual = SamRecordFlags.parseFlags(145);
		Set<SamRecordFlags> expected = EnumSet.of(
				
				SamRecordFlags.HAS_MATE_PAIR,
				SamRecordFlags.REVERSE_COMPLEMENTED,
				SamRecordFlags.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(145, SamRecordFlags.asBits(expected));
	}
}
