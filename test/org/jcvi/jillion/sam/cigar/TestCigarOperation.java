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
package org.jcvi.jillion.sam.cigar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestCigarOperation {
	/**
	 * BAM file encoding using ints
	 * to refer to Cigar operations.
	 * Jillion stores the enum values in that 
	 * order so we can just use ordinal lookups.
	 */
	@Test
	public void assertOrdinals(){
		CigarOperation[] expectedOrder = new CigarOperation[]{
				CigarOperation.ALIGNMENT_MATCH,
				CigarOperation.INSERTION,
				CigarOperation.DELETION,
				CigarOperation.SKIPPED,
				CigarOperation.SOFT_CLIP,
				CigarOperation.HARD_CLIP,
				CigarOperation.PADDING,
				CigarOperation.SEQUENCE_MATCH,
				CigarOperation.SEQUENCE_MISMATCH
		};
		assertArrayEquals(expectedOrder, CigarOperation.values());
	}
	
	@Test
	public void parseValid(){
		for(CigarOperation op : CigarOperation.values()){
			char opCode =op.getOpCode();
			assertEquals(op, CigarOperation.parseOp(opCode));
			assertEquals(op, CigarOperation.parseOp(""+opCode));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void parseInValidShouldThrowIllegalArgumentException(){
		CigarOperation.parseOp("*");
	}
	@Test(expected = NullPointerException.class)
	public void parseNullShouldThrowNPE(){
		CigarOperation.parseOp(null);
	}
}
