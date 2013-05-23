package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.junit.Test;
public class TestTextLineParser {

	@Test(expected = NullPointerException.class)
	public void nullInputStreamShouldThrowNPE() throws IOException{
		new TextLineParser(null);
	}
	
	
	@Test
	public void oneLineOnlyNoEOL() throws IOException{
		testOneLineOnlyNoEOL(0);
	}
	@Test
	public void oneLineOnlyNoEOLWithSetPosition() throws IOException{
		testOneLineOnlyNoEOL(1000);
	}
	
	private void testOneLineOnlyNoEOL(int position) throws IOException{
		String expectedLine = "this is only 1 line";
		InputStream in = toInputStream(expectedLine);
		TextLineParser sut = new TextLineParser(in,position);
		try{
			assertTrue(sut.hasNextLine());
			assertEquals(expectedLine, sut.nextLine());
			assertFalse(sut.hasNextLine());
			assertEquals(position +expectedLine.length(),sut.getPosition());
		}finally{
			sut.close();
		}
	}
	
	@Test
	public void multiLinesUnix() throws IOException{
		testMultipleLines(0, "\n");
	}
	@Test
	public void multiLinesSetPositionUnix() throws IOException{
		testMultipleLines(1000, "\n");
	}
	
	@Test
	public void multiLinesWindows() throws IOException{
		testMultipleLines(0, "\r\n");
	}
	@Test
	public void multiLinesSetPositionWindows() throws IOException{
		testMultipleLines(1000, "\r\n");
	}
	
	@Test
	public void multiLinesOS9() throws IOException{
		testMultipleLines(0, "\r");
	}
	@Test
	public void multiLinesSetPositionOS9() throws IOException{
		testMultipleLines(1000, "\r");
	}
	
	private void testMultipleLines(int position, String eol) throws IOException{
		List<String> lines = Arrays.asList(
				"this is first line",
				"this is 2nd line",
				"",
				"4th after blank line");
											
		InputStream in = toInputStream(new JoinedStringBuilder(lines)
											.glue(eol)
											.includeEmptyStrings(true)
											.build());
		TextLineParser sut = new TextLineParser(in,position);
		try{
			int currentPosition = position;
			assertEquals(position, sut.getPosition());
			for(String line : lines){
				assertTrue(sut.hasNextLine());
				String actualLine = sut.nextLine();
				final String expectedLine;
				if(sut.hasNextLine()){
					expectedLine = line+ eol;					
				}else{
					expectedLine = line;
				}				
				assertEquals(expectedLine, actualLine);
				assertEquals(currentPosition +expectedLine.length(),sut.getPosition());
				currentPosition+=expectedLine.length();
			}			
			assertFalse(sut.hasNextLine());
		}finally{
			sut.close();
		}
	}
	

	private InputStream toInputStream(String value){
		return  new ByteArrayInputStream(value.getBytes(IOUtil.UTF_8));
	}
}
