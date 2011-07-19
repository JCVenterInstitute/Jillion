package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;

/**
 * {@code DefaultAminoAcidEncodedGlyphs} is the default implementation
 * of the {@link AminoAcidSequence} interface.
 *
 * @author naxelrod
 */

public class DefaultAminoAcidEncodedGlyphs implements AminoAcidSequence {

	private final Sequence<AminoAcid> encodedAminoAcids;
	
	public DefaultAminoAcidEncodedGlyphs(Collection<AminoAcid> glyphs) {
		this.encodedAminoAcids = new EncodedSequence<AminoAcid>(DefaultAminoAcidGlyphCodec.getInstance(),glyphs);
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

