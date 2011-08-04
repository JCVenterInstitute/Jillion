package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.ShortGlyphDeltaEncoder;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestShortGlyphDeltaEncoderWhenEncoding {

	ShortGlyphDeltaEncoder sut = new ShortGlyphDeltaEncoder();
	
	@Test
	public void emptyList(){
		byte[] expected =new byte[]{};
		byte[] actual =sut.encode(new ArrayList<ShortSymbol>());
		assertArrayEquals(expected, actual);
	}
	@Test
	public void oneElement(){
		short[] input = new short[]{5};
		byte[] expected =ByteBuffer.allocate(2)
					.putShort((short)5)
					.array();
		
		byte[] actual =sut.encode(ShortGlyphFactory.getInstance().getGlyphsFor(input));
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void twoElements(){
		short[] input = new short[]{5,10};
		byte[] expected =ByteBuffer.allocate(4)
					.putShort((short)5)
					.putShort((short)5)
					.array();
		
		byte[] actual =sut.encode(ShortGlyphFactory.getInstance().getGlyphsFor(input));
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void threeElements(){
		short[] input = new short[]{5,10,45};
		byte[] expected =ByteBuffer.allocate(6)
					.putShort((short)5)
					.putShort((short)5)
					.putShort((short)35)
					.array();
		
		byte[] actual =sut.encode(ShortGlyphFactory.getInstance().getGlyphsFor(input));
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void negativeDelta(){
		short[] input = new short[]{5,10,7};
		byte[] expected =ByteBuffer.allocate(6)
					.putShort((short)5)
					.putShort((short)5)
					.putShort((short)-3)
					.array();
		
		byte[] actual =sut.encode(ShortGlyphFactory.getInstance().getGlyphsFor(input));
		assertArrayEquals(expected, actual);
	}
}
