package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.GlyphCodec;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.AbstractResidueSequence;

public abstract class AbstractAminoAcidSequence extends AbstractResidueSequence<AminoAcid> implements AminoAcidSequence {

	private final Sequence<AminoAcid> encodedAminoAcids;
	
	public AbstractAminoAcidSequence(Collection<AminoAcid> glyphs, GlyphCodec<AminoAcid> codec) {
		this.encodedAminoAcids = new EncodedSequence<AminoAcid>(codec,glyphs);
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

	@Override
	public String toString(){
		return AminoAcids.asString(this);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ encodedAminoAcids.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		AminoAcidSequence other = (AminoAcidSequence) obj;
		return AminoAcids.asString(this).equals(AminoAcids.asString(other));
	}

}
