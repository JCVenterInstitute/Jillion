package org.jcvi.glyph.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;

/**
 * {@code DefaultAminoAcidEncodedGlyphs} is the default implementation
 * of the {@link AminoAcidEncodedGlyphs} interface.
 *
 * @author naxelrod
 */

public class DefaultAminoAcidEncodedGlyphs implements AminoAcidEncodedGlyphs {

	private final EncodedGlyphs<AminoAcid> encodedAminoAcids;
	
	public DefaultAminoAcidEncodedGlyphs(Collection<AminoAcid> glyphs) {
		this.encodedAminoAcids = new DefaultEncodedGlyphs<AminoAcid>(DefaultAminoAcidGlyphCodec.getInstance(),glyphs);
	}
	public DefaultAminoAcidEncodedGlyphs(char[] aminoAcids) {
		this(new String(aminoAcids));
	}
	public DefaultAminoAcidEncodedGlyphs(String aminoAcids) {
		this(AminoAcid.getGlyphsFor(aminoAcids));
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
        List<AminoAcid> result = new ArrayList<AminoAcid>((int)range.size());
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

