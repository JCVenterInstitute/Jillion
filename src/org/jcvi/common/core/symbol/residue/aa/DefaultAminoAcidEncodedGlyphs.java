package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.AbstractResidueSequence;

/**
 * {@code DefaultAminoAcidEncodedGlyphs} is the default implementation
 * of the {@link AminoAcidSequence} interface.
 *
 * @author naxelrod
 * @author dkatzel
 */

public class DefaultAminoAcidEncodedGlyphs extends AbstractResidueSequence<AminoAcid> implements AminoAcidSequence {

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
	public List<AminoAcid> asList() {
		return encodedAminoAcids.asList();
	}

	@Override
	public List<AminoAcid> asList(Range range) {
        if (range == null){
            return asList();
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
    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<AminoAcid> iterator() {
        return encodedAminoAcids.iterator();
    }
	@Override
	public List<Integer> getGapOffsets() {
		List<AminoAcid> aas = asList();
		List<Integer> gapOffsets = new ArrayList<Integer>();
		for(int i=0; i< aas.size(); i++){
			if(aas.get(i) == AminoAcid.Gap){
				gapOffsets.add(Integer.valueOf(i));
			}
		}
		return gapOffsets;
	}
	@Override
	public int getNumberOfGaps() {
		List<AminoAcid> aas = asList();
		int count=0;
		for(int i=0; i< aas.size(); i++){
			if(aas.get(i) == AminoAcid.Gap){
			count++;
			}
		}
		return count;
	}
	@Override
	public boolean isGap(int gappedOffset) {
		List<AminoAcid> aas = asList();
		return aas.get(gappedOffset) ==AminoAcid.Gap;
	}

}

