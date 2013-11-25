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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.BlosumMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.junit.Test;
public class TestBlosum62 extends AbstractBlosumTest{

	public TestBlosum62() {
		super(BlosumMatrices.blosum62());
	}

	@Test
	public void spotCheck(){
		
		AminoAcidSubstitutionMatrix blosum62 = getMatrix();
		assertEquals(4F,
				blosum62.getValue(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(7F,
				blosum62.getValue(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-2F,
				blosum62.getValue(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(0F,
				blosum62.getValue(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
		assertEquals(1F,
				blosum62.getValue(AminoAcid.STOP, AminoAcid.STOP),
				0F);
		assertEquals(-4F,
				blosum62.getValue(AminoAcid.STOP, AminoAcid.Alanine),
				0F);
	}
	
	
}
