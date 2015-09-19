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
/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public final class DefaultSlice implements Slice{
	public static final DefaultSlice EMPTY = new Builder().build();
	
    private final Map<String,SliceElement> elements;
    private final Nucleotide consensus;
    
    private DefaultSlice(Map<String,SliceElement> elements, Nucleotide consensus){
        this.elements = elements;
        this.consensus = consensus;
        
    }
    
    
    @Override
	public Nucleotide getConsensusCall() {
		return consensus;
	}


	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + elements.hashCode();
        Nucleotide consensusCall = getConsensusCall();
		if(consensusCall !=null){
			result = prime * result + consensusCall.hashCode();
		}
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
        if (!(obj instanceof Slice)){
            return false;
        }
        Slice other = (Slice) obj;
        if(other.getCoverageDepth() != elements.size()){
        	return false;
        }
        Nucleotide consensusCall = getConsensusCall();
		Nucleotide otherConsensusCall = other.getConsensusCall();
		if(consensusCall ==null){
        	if(otherConsensusCall!=null){
        		return false;
        	}
        }else{
        	if(!consensusCall.equals(otherConsensusCall)){
        		return false;
        	}
        }
        for(SliceElement e: other){
        	SliceElement ourElement =elements.get(e.getId());
        	if(!e.equals(ourElement)){
        		return false;
        	}
        }
        return true;
        
            
    }
    @Override
    public String toString() {
        return elements.toString();
    }
    @Override
    public int getCoverageDepth() {
        return elements.size();
    }
    @Override
    public Iterator<SliceElement> iterator() {
        return elements.values().iterator();
    }
    @Override
    public boolean containsElement(String elementId) {
        return getSliceElement(elementId)!=null;
    }
    @Override
    public SliceElement getSliceElement(String elementId) {
        return elements.get(elementId);
    }
    
    
    
    
    @Override
	public Map<Nucleotide, Integer> getNucleotideCounts() {
    	int[] counts = new int[Nucleotide.VALUES.size()];
    	for(SliceElement element : elements.values()){
    		counts[element.getBase().ordinal()]++;
    	}
    	Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(int i=0; i < counts.length; i++){
			int count = counts[i];
			map.put(Nucleotide.VALUES.get(i), count);
		}
		return map;
	}




	public static class Builder implements org.jcvi.jillion.core.util.Builder<DefaultSlice>{
        private final Map<String,SliceElement> elements = new LinkedHashMap<String, SliceElement>();
        private Nucleotide consensus = null;
        public Builder add(String id, Nucleotide base, PhredQuality quality, Direction dir){
            return add(new DefaultSliceElement(id, base, quality, dir));
        }
        public Builder addAll(Iterable<? extends SliceElement> elements){
            for(SliceElement element : elements){
                this.add(element);
            }
            return this;
        }
        public Builder add(SliceElement element){
            elements.put(element.getId(), element);
            return this;
        }
        public Builder setConsensus(Nucleotide consensus){
        	this.consensus = consensus;
        	return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultSlice build() {
            return new DefaultSlice(elements, consensus);
        }
        
        
    }
}
