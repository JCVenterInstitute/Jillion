/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
package org.jcvi.jillion.align.pairwise.blosom;

import org.jcvi.jillion.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion.align.pairwise.blosom.BlosomMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBlosom50 {

	@Test
	public void spotCheck(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		assertEquals(5F,
				blosom50.getScore(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(10F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-3F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(0F,
				blosom50.getScore(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
	}
}
