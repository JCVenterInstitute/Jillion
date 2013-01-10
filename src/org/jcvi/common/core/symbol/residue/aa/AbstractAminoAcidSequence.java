package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.internal.symbol.EncodedSequence;
import org.jcvi.jillion.core.internal.symbol.GlyphCodec;
import org.jcvi.jillion.core.internal.symbol.residue.AbstractResidueSequence;

public abstract class AbstractAminoAcidSequence extends AbstractResidueSequence<AminoAcid> implements AminoAcidSequence {

	private final Sequence<AminoAcid> encodedAminoAcids;
	
	public AbstractAminoAcidSequence(Collection<AminoAcid> glyphs, GlyphCodec<AminoAcid> codec) {
		this.encodedAminoAcids = new EncodedSequence<AminoAcid>(codec,glyphs);
	}
	
	

	@Override
	public AminoAcid get(long index) {
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
    
    /**
     * {@inheritDoc}
     */
     @Override
     public Iterator<AminoAcid> iterator(Range range) {
         return encodedAminoAcids.iterator(range);
     }
	@Override
	public List<Integer> getGapOffsets() {
		Iterator<AminoAcid> iter = iterator();
		int i=0;
		List<Integer> gapOffsets = new ArrayList<Integer>();
		while(iter.hasNext()){
			if(iter.next() ==AminoAcid.Gap){
				gapOffsets.add(Integer.valueOf(i));
			}
			i++;
		}		
		return gapOffsets;
	}
	@Override
	public int getNumberOfGaps() {
		Iterator<AminoAcid> iter = iterator();
		int count=0;
		
		while(iter.hasNext()){
			if(iter.next() ==AminoAcid.Gap){
				count++;
			}			
		}		
		return count;		
	}
	@Override
	public boolean isGap(int gappedOffset) {
		return encodedAminoAcids.get(gappedOffset) == AminoAcid.Gap;		
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
