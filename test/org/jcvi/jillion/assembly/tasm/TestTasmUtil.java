package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

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
