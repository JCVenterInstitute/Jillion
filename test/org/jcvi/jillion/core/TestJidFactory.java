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
}
