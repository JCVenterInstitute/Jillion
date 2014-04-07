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
package org.jcvi.jillion.assembly.tigr.tasm;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.jcvi.jillion.assembly.tigr.tasm.TasmUtil;
import org.joda.time.DateTime;
import org.junit.Test;

public class TestTasmUtil {

	DateTime expected = new DateTime(2010, 3, 5, 13, 52, 31);
	
	private String editDateAsString = "03/05/10 01:52:31 PM";
	
	@Test
	public void parseEditDate() throws ParseException{
		Date actual = TasmUtil.parseEditDate(editDateAsString);
		
		assertEquals(expected.getMillis(), actual.getTime());
	}
	@Test
	public void parseEditDateWithExtraWhitespace() throws ParseException{
		Date actual = TasmUtil.parseEditDate(editDateAsString+" \t\n");
		
		assertEquals(expected.getMillis(), actual.getTime());
	}
	
	@Test(expected = NullPointerException.class)
	public void parseEditDateWithNullShouldThrowNPE() throws ParseException{
		TasmUtil.parseEditDate(null);
	}
	
	@Test
	public void formatEditDate(){
		String actual = TasmUtil.formatEditDate(expected.toDate());
		assertEquals(editDateAsString, actual);
	}
	
	@Test(expected = NullPointerException.class)
	public void formatEditDateWithNullShouldThrowNPE(){
		TasmUtil.formatEditDate(null);		
	}
}
