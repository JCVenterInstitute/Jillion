package org.jcvi.glyph.phredQuality;

import java.util.List;

import org.jcvi.glyph.num.AbstractZipByteGlyphCodec;
/**
 * {@code ZipPhredQualityCodec} is an {@link AbstractZipByteGlyphCodec}
 * for ZIP encoding {@link PhredQuality} glyphs.
 * @author dkatzel
 *
 */
public class ZipPhredQualityCodec extends AbstractZipByteGlyphCodec<PhredQuality>{

	@Override
	protected List<PhredQuality> getGlyphsFor(byte[] decodedBytes) {
		return PhredQuality.valueOf(decodedBytes);
	}

}
