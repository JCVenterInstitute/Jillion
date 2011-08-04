package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.List;


public class ShortGlyphDeltaEncoder extends AbstractDeltaEncoderCodec<ShortSymbol>{

	public ShortGlyphDeltaEncoder(DeltaEncoder deltaEncoder) {
		super(deltaEncoder, ValueSizeStrategy.SHORT);
	}

	public ShortGlyphDeltaEncoder(){
		this(DeltaEncoder.LEVEL_1);
	}
	

	@Override
	protected List<ShortSymbol> convertToGlyphs(ByteBuffer decodedData) {
		ShortBuffer buf = ShortBuffer.allocate(decodedData.remaining()/2);
		while(decodedData.hasRemaining()){
			buf.put(decodedData.getShort());
		}
		return ShortGlyphFactory.getInstance().getGlyphsFor(buf.array());
	}

	

}
