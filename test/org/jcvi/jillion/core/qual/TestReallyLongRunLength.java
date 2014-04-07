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
package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
/**
 * Ravi noticed if a quality sequence
 * that is longer than {@link Short#MAX_VALUE}
 * will crash the run length encoder
 * since it will cause a negative count.
 * 
 * I have created a bug on sourceforge's Jillion
 * bug tracker.
 * 
 * 
 * @author dkatzel
 * @see <a href= "https://sourceforge.net/tracker/?func=detail&atid=1278566&aid=3603580&group_id=303297">
 * Jillion Bug Report</a>
 */
public class TestReallyLongRunLength {

	@Test
	public void testLongerThanShortMax(){
		byte[] quals = new byte[Short.MAX_VALUE +1];
		byte value = (byte)60;
		Arrays.fill(quals, value);
		
		QualitySequence seq = new QualitySequenceBuilder(quals).build();
		assertEquals(quals.length,seq.getLength());
		Iterator<PhredQuality> iter = seq.iterator();
		assertTrue(iter.hasNext());
		while(iter.hasNext()){
			assertEquals(value, iter.next().getQualityScore());
		}
	}
}
