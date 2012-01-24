package org.jcvi.common.core.symbol.residue.aa;

import java.util.Collection;

/**
 * {@code DefaultAminoAcidSequence} is the default implementation
 * of the {@link AminoAcidSequence} interface.
 *
 * @author naxelrod
 * @author dkatzel
 */
public class DefaultAminoAcidSequence extends AbstractAminoAcidSequence{

	public DefaultAminoAcidSequence(Collection<AminoAcid> glyphs) {
		super(glyphs, DefaultAminoAcidGlyphCodec.getInstance());
	}

	public DefaultAminoAcidSequence(String aminoAcids){
		this(AminoAcids.parse(aminoAcids));
	}
	public DefaultAminoAcidSequence(char[] aminoAcids){
		this(AminoAcids.parse(new String(aminoAcids)));
	}
}

