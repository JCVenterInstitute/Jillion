/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestAlnUtil {

	@Test
	public void validHeader(){
		assertTrue(AlnUtil.validHeader("CLUSTAL"));
		assertTrue(AlnUtil.validHeader("CLUSTAL W (1.82) multiple sequence alignment"));
		assertTrue(AlnUtil.validHeader("CLUSTALW (1.82) multiple sequence alignment"));
		
		assertTrue(AlnUtil.validHeader("CLUSTAL 2.0.11 multiple sequence alignment"));
		
	}
	
	@Test
	public void invalidHeader(){
		assertFalse(AlnUtil.validHeader("FOSB_MOUSE      MFQAFPGDYDSGSRCSSSPSAESQYLSSVDSFGSPPTAAASQECAGLGEMPGSFVPTVTA 60"));
		assertFalse(AlnUtil.validHeader(""));
	}
	@Test(expected = NullPointerException.class)
	public void nullHeaderShouldThrowNPE(){
		AlnUtil.validHeader(null);
	}
}
