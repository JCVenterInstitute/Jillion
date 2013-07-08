package org.jcvi.jillion.core.residue.aa;

import java.util.Collection;

import org.jcvi.jillion.internal.core.GlyphCodec;

public interface AminoAcidCodec extends GlyphCodec<AminoAcid>{

	byte[] encode(Collection<AminoAcid> glyphs);
}
