package org.jcvi.glyph.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.glyph.GlyphCodec;

/**
 * <code>DefaultAminoAcidGlyphCodec</code> is the implementation
 * of {@link GlyphCodec} that can converts {@link AminoAcid}s
 * into a 1 byte representation. 
 * @author naxelrod
 *
 */
public final class DefaultAminoAcidGlyphCodec implements GlyphCodec<AminoAcid> {

	private static final Map<Byte, AminoAcid> BYTE_TO_GLYPH_MAP = new HashMap<Byte, AminoAcid>();
    private static final Map<AminoAcid, Byte> GLYPH_TO_BYTE_MAP = new EnumMap<AminoAcid, Byte>(AminoAcid.class);

	static{
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Isoleucine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Leucine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Lysine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Methionine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Phenylalanine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Threonine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Tryptophan.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Valine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Cysteine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Glutamine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Glycine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Proline.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Serine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Tyrosine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Arginine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Histidine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Alanine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Asparagine.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Aspartic_Acid.getAbbreviation().toString()), AminoAcid.Alanine);
		BYTE_TO_GLYPH_MAP.put(Byte.valueOf(AminoAcid.Glutamic_Acid.getAbbreviation().toString()), AminoAcid.Alanine);
        for(Entry<Byte, AminoAcid> entry : BYTE_TO_GLYPH_MAP.entrySet()){
            GLYPH_TO_BYTE_MAP.put(entry.getValue(), entry.getKey());           
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
