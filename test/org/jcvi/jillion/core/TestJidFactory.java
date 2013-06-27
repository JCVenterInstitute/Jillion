/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestJidFactory {

	private final String str;
	private final String differentStr;
	private final Jid id;
	
	 @Parameters
	    public static Collection<?> data(){
		 char[] largeArray = new char[100];
		 Arrays.fill(largeArray, 'a');
		 
		 char[] diffLargeArray = new char[100];
		 System.arraycopy(largeArray, 0, diffLargeArray, 0, diffLargeArray.length);
		 diffLargeArray[50] = 'b';
		 
		 String uft16char = "\u1D11E";
		 String diffUft16char = "\u1D11F";
	        return Arrays.asList(new Object[][]{
	        		new String[]{"an Id < 13", "diff Str"},
	        		new String[]{"this is an Id < 21", "another str Id < 21"},
	        		new String[]{"this is yet another Id < 29", "this is yet another Id <=28"},
	        		new String[]{"this is yet another another Id < 37", "this is yet another another Id <=36"},
	        		new String[]{"this is yet another another another Id < 43", "this is yet another another Id <=42"},
	        		new String[]{"this is yet another another another another Id < 51", "this is yet another another Id <=50"},
	        		new String[]{"", "empty"},
	        		new String[]{"has a null "+'\0' + "char ", "has a diff null "+'\0' + "char "},
	        		new String[]{new String(largeArray), new String(diffLargeArray)},
	        		new String[]{"has UTF-16 char "+ uft16char,"has UTF-16 char "+ diffUft16char }
	        });
	 }
	 
	 public TestJidFactory(String str, String diffStr){
		 this.str = str;
		 this.differentStr = diffStr;
		 this.id = JidFactory.create(str);
	 }
	 
	 
	@Test
	public void equalsToString(){		
		assertEquals("to String", str, id.toString());		
	}
	@Test
	public void equalsSameRef(){		
		TestUtil.assertEqualAndHashcodeSame(id, id);		
	}
	@Test
	public void equalsSameValue(){		
		TestUtil.assertEqualAndHashcodeSame(id, JidFactory.create(str));		
	}
	@Test
	public void notEqualToNull(){
		assertFalse(id.equals(null));
	}
	
	@Test
	public void notEqualDifferentClass(){
		assertFalse(id.equals(Integer.valueOf(0)));
	}
	
	@Test
	public void notEqualDifferentValueOfSameLength(){
		String diffString = "X"+ (str.length() >1 ?str.substring(1):"");
		TestUtil.assertNotEqualAndHashcodeDifferent(id, JidFactory.create(diffString));
	}
	@Test
	public void notEqualDifferentValueWithDifferentLength(){
		TestUtil.assertNotEqualAndHashcodeDifferent(id, JidFactory.create(differentStr));
	}
	@Test
	public void equalsDifferentJidClassWithSameValue(){
		Jid other = new JidFactory.StringJid(str);
		TestUtil.assertEqualAndHashcodeSame(id, other);	
	}
	
	@Test
	public void comparedToItselfShouldReturn0(){
		assertEquals(0, id.compareTo(id));
	}
	@Test
	public void comparedToNonEqualShouldReturnNon0(){
		assertFalse(id.compareTo(JidFactory.create(differentStr)) ==0);
	}
}
