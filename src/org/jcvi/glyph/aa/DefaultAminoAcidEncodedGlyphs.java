package org.jcvi.glyph.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;

public class DefaultAminoAcidEncodedGlyphs implements EncodedGlyphs<AminoAcid> {

	private final EncodedGlyphs<AminoAcid> encodedAminoAcids;
	
	public DefaultAminoAcidEncodedGlyphs(Collection<AminoAcid> glyphs) {
		this.encodedAminoAcids = new DefaultEncodedGlyphs<AminoAcid>(DefaultAminoAcidGlyphCodec.getInstance(),glyphs);
	}
	
	@Override
	public List<AminoAcid> decode() {
		return encodedAminoAcids.decode();
	}

	@Override
	public List<AminoAcid> decode(Range range) {
        if (range == null){
            return decode();
        }
        List<AminoAcid> result = new ArrayList<AminoAcid>();
        for (long index : range){
            result.add(get((int)index));
        }
        return result;
	}

	@Override
	public AminoAcid get(int index) {
		return encodedAminoAcids.get(index);
	}

	@Override
	public long getLength() {
		return encodedAminoAcids.getLength();
	}

}

