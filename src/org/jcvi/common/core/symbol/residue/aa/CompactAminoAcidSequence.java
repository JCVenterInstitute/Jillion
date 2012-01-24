package org.jcvi.common.core.symbol.residue.aa;

import java.util.Collection;
/**
 * {@code CompactAminoAcidSequence} is able to 
 * an {@link AminoAcidSequence} as in a byte array where each {@link AminoAcid}
 * only takes up 5 bits. This is a 37.5% memory reduction compared to 
 * encoding the data as one byte each or 68% memory reduction compared
 * to encoding each AminoAcid as one char each.
 * @author dkatzel
 *
 */
public class CompactAminoAcidSequence extends AbstractAminoAcidSequence {

	public CompactAminoAcidSequence(Collection<AminoAcid> glyphs) {
		super(glyphs, CompactAminoAcidSequenceCodec.INSTANCE);
	}

	public CompactAminoAcidSequence(String aminoAcids){
		this(AminoAcids.parse(aminoAcids));
	}
}
