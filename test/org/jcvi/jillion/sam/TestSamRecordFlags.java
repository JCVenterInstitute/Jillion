/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
		SamRecordFlag.parseFlags(-1);
	}
	@Test(expected = NullPointerException.class)
	public void nullAsBitsShouldThrowNPE(){
		SamRecordFlag.asBits(null);
	}
	@Test
	public void bitsOfZeroMeansEmpty(){
		assertTrue(SamRecordFlag.parseFlags(0).isEmpty());
		assertEquals(0, SamRecordFlag.asBits(Collections.<SamRecordFlag>emptySet()));
	}
	@Test
	public void bits163(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(163);
		Set<SamRecordFlag> expected = EnumSet.of(
				
					SamRecordFlag.HAS_MATE_PAIR,
					SamRecordFlag.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlag.SECOND_MATE_OF_PAIR,
					SamRecordFlag.MATE_REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(163, SamRecordFlag.asBits(expected));
	}
	
	@Test
	public void bits2064(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(2064);
		Set<SamRecordFlag> expected = EnumSet.of(
				
					SamRecordFlag.SUPPLEMENTARY_ALIGNMENT,
					SamRecordFlag.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(2064, SamRecordFlag.asBits(expected));
	}
	
	@Test
	public void bits83(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(83);
		Set<SamRecordFlag> expected = EnumSet.of(
				
					SamRecordFlag.HAS_MATE_PAIR,
					SamRecordFlag.EACH_SEGMENT_PROPERLY_ALIGNED,
					SamRecordFlag.FIRST_MATE_OF_PAIR,
					SamRecordFlag.REVERSE_COMPLEMENTED
				);
		
		assertEquals(expected, actual);
		assertEquals(83, SamRecordFlag.asBits(expected));
	}
	/**
	 * First read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits77(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(77);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.READ_UNMAPPED,
				SamRecordFlag.MATE_UNMAPPED,
				SamRecordFlag.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(77, SamRecordFlag.asBits(expected));
	}
	/**
	 * Last read in mate did not map
	 * (and its mate didn't either)
	 */
	@Test
	public void bits141(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(141);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.READ_UNMAPPED,
				SamRecordFlag.MATE_UNMAPPED,
				SamRecordFlag.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(141, SamRecordFlag.asBits(expected));
	}
	
	@Test
	public void bits81(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(81);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.REVERSE_COMPLEMENTED,
				SamRecordFlag.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(81, SamRecordFlag.asBits(expected));
	}
	
	@Test
	public void bits161(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(161);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.MATE_REVERSE_COMPLEMENTED,
				SamRecordFlag.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(161, SamRecordFlag.asBits(expected));
	}
	
	
	@Test
	public void bits97(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(97);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.MATE_REVERSE_COMPLEMENTED,
				SamRecordFlag.FIRST_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(97, SamRecordFlag.asBits(expected));
	}
	
	@Test
	public void bits145(){
		Set<SamRecordFlag> actual = SamRecordFlag.parseFlags(145);
		Set<SamRecordFlag> expected = EnumSet.of(
				
				SamRecordFlag.HAS_MATE_PAIR,
				SamRecordFlag.REVERSE_COMPLEMENTED,
				SamRecordFlag.SECOND_MATE_OF_PAIR
			);
	
		assertEquals(expected, actual);
		assertEquals(145, SamRecordFlag.asBits(expected));
	}
}
