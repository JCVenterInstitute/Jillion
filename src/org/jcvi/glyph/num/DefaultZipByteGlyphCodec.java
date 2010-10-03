package org.jcvi.glyph.num;

import java.util.List;

public class DefaultZipByteGlyphCodec extends AbstractZipByteGlyphCodec<ByteGlyph> {

	@Override
	protected List<ByteGlyph> getGlyphsFor(byte[] decodedBytes) {
		
		return DefaultByteGlyphFactory.getInstance().getGlyphsFor(decodedBytes);
	}

}
