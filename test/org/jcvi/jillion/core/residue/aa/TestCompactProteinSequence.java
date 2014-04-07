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
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestCompactProteinSequence extends AbstractTestProteinSequence{

	@Override
	protected ProteinSequence encode(AminoAcid[] aminoAcids) {
		return new CompactProteinSequence(aminoAcids);
	}

	@Test
	public void gappedSequence(){
		ProteinSequence seq = encode(AminoAcidUtil.parse("I-LKM-FDEX").toArray(new AminoAcid[0]));
		assertEquals("I-LKM-FDEX", AminoAcidUtil.asString(seq));
		assertEquals(2, seq.getNumberOfGaps());
		assertEquals(8, seq.getUngappedLength());
		assertEquals(1, seq.getUngappedOffsetFor(2));
	}

}
