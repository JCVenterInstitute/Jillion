/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
