package org.jcvi.common.core.seq.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.seq.GlyphCodec;

/**
 * <code>DefaultAminoAcidGlyphCodec</code> is the implementation
 * of {@link GlyphCodec} that can converts {@link AminoAcid}s
 * into a 1 byte representation. 
 * @author naxelrod
 * @author dkatzel
 *
 */
public final class DefaultAminoAcidGlyphCodec implements GlyphCodec<AminoAcid> {

	private static final Map<Byte, AminoAcid> BYTE_TO_GLYPH_MAP = new HashMap<Byte, AminoAcid>();
    private static final Map<AminoAcid, Byte> GLYPH_TO_BYTE_MAP = new EnumMap<AminoAcid, Byte>(AminoAcid.class);

	static{
		for(AminoAcid aa : AminoAcid.values()){
        	BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)aa.ordinal()), aa);
        	GLYPH_TO_BYTE_MAP.put(aa,Byte.valueOf((byte)aa.ordinal()));
        }
		
    }

	// Singleton instance of our class
    private static DefaultAminoAcidGlyphCodec INSTANCE = new DefaultAminoAcidGlyphCodec();
    private DefaultAminoAcidGlyphCodec(){}
    public static DefaultAminoAcidGlyphCodec getInstance(){
        return INSTANCE;
    }
    
	@Override
	public List<AminoAcid> decode(byte[] encodedGlyphs) {
		List<AminoAcid> result = new ArrayList<AminoAcid>(encodedGlyphs.length);
		for (byte b : encodedGlyphs) {
			result.add(BYTE_TO_GLYPH_MAP.get(b));
		}
		return result;
	}

	@Override
	public AminoAcid decode(byte[] encodedGlyphs, int index) {
		return BYTE_TO_GLYPH_MAP.get(encodedGlyphs[index]);
	}

	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		return encodedGlyphs.length;
	}

	@Override
	public byte[] encode(Collection<AminoAcid> glyphs) {
		int len = glyphs.size();
		byte[] result = new byte[len];
		int i = 0;
		for (AminoAcid aa : glyphs) {
			result[i++] = GLYPH_TO_BYTE_MAP.get(aa);
		}
		return result;
	}

}
