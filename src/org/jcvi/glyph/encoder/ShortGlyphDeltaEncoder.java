package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
import java.util.List;

import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;

public class ShortGlyphDeltaEncoder extends AbstractDeltaEncoderCodec<ShortGlyph, Short>{

	public ShortGlyphDeltaEncoder(DeltaEncoder deltaEncoder) {
		super(deltaEncoder, ShortValueSizeStrategy.getInstance());
	}

	public ShortGlyphDeltaEncoder(){
		this(Level1DeltaEncoder.getInstance());
	}
	

	@Override
	protected List<ShortGlyph> convertToGlyphs(ByteBuffer decodedData) {
		return ShortGlyphFactory.getInstance().getGlyphsFor(decodedData.asShortBuffer().array());
	}

	

}
