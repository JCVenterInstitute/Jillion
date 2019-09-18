/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.internal.core.EncodedSequence;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;

abstract class AbstractProteinSequence extends AbstractResidueSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder> implements ProteinSequence {

	//This class uses the Serialization Proxy Pattern
	//described in Effective Java 2nd Ed
	//to substitute a proxy class to be serialized.
		
	private static final long serialVersionUID = -8506764895478268009L;
	private final Sequence<AminoAcid> encodedAminoAcids;
	
	public AbstractProteinSequence(AminoAcid[] glyphs, AminoAcidCodec codec) {
		this.encodedAminoAcids = new EncodedSequence<AminoAcid>(codec,codec.encode(glyphs)){

			@Override
			public ProteinSequenceBuilder toBuilder() {
				return new ProteinSequenceBuilder(AbstractProteinSequence.this);
			}
			
		};
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
    
    @Override
    public ProteinSequence asSubtype() {
        return this;
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
		StringBuilder builder = new StringBuilder((int)getLength());
		for(AminoAcid aa : this){
			builder.append(aa.asChar());
		}
		return builder.toString();
	}
	
	
	
	
	@Override
	public ProteinSequenceBuilder toBuilder() {
		return new ProteinSequenceBuilder(this);
	}
	
	@Override
        public ProteinSequenceBuilder toBuilder(Range range) {
                return new ProteinSequenceBuilder(this, range);
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
		if (!(obj instanceof ProteinSequence)){
			return false;
		}
		ProteinSequence other = (ProteinSequence) obj;
		return toString().equals(other.toString());
	}
	
	 //serialization methods need to be protected
    //so the subclasses inherit them!
    
    protected Object writeReplace(){
		return new ProteinSequenceProxy(this);
	}
	
	
	/**
	 * Serialization Proxy Pattern object to handle
	 * serialization of ProteinSequence objects.  This allows us
	 * to change ProteinSequence fields and subclasses without
	 * breaking serialization.
	 * 
	 * @author dkatzel
	 *
	 */
	private static final class ProteinSequenceProxy implements Serializable{

		private static final long serialVersionUID = -8473861196950222580L;
		
		private final String seq;
		
		ProteinSequenceProxy(ProteinSequence s){
			seq = s.toString();
		}
		
		private Object readResolve(){
			return new ProteinSequenceBuilder(seq).build();
		}
	}

}
