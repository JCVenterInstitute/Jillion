package org.jcvi.common.core.symbol;

import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.ShortGlyphDeltaEncoder;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.junit.Test;

public class TestShortGlyphDeltaEncoderWhenDecoding {

	private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
	private static final ShortGlyphDeltaEncoder SUT = new ShortGlyphDeltaEncoder();
	
	@Test
	public void emptyList(){
		assertEncodedAndDecode(Collections.<ShortSymbol>emptyList());
	}
	@Test
	public void oneElement(){
		List<ShortSymbol> list = FACTORY.getGlyphsFor(new short[]{5});
		assertEncodedAndDecode(list);
	}
	
	@Test
	public void twoElements(){
		List<ShortSymbol> list = FACTORY.getGlyphsFor(
		new short[]{5,10});
		assertEncodedAndDecode(list);
	}
	
	@Test
	public void threeElements(){
		List<ShortSymbol> list = FACTORY.getGlyphsFor(
		new short[]{5,10,45});
		
		assertEncodedAndDecode(list);
	}
	private static void assertEncodedAndDecode(List<ShortSymbol> list) {
		byte[] encoded =SUT.encode(list);
		assertEquals(list, SUT.decode(encoded));		
	}
	
	@Test
	public void negativeDelta(){
		List<ShortSymbol> list = FACTORY.getGlyphsFor(
		new short[]{5,10,45,30});
		assertEncodedAndDecode(list);
	}
}
