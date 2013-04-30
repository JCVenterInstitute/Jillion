/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.assembly.util;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public final class DefaultSlice implements Slice{
	public static final DefaultSlice EMPTY = new Builder().build();
	private static final Nucleotide[] VALUES = Nucleotide.values();
	
    private final Map<String,SliceElement> elements;
    
    private DefaultSlice(Map<String,SliceElement> elements){
        this.elements = elements;
        
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + elements.hashCode();
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
        Iterator<SliceElement> iter = iterator();
        Iterator<SliceElement> otherIter = other.iterator();
        while(iter.hasNext()){
			if(!otherIter.hasNext()){
				return false;
			}
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		if(otherIter.hasNext()){
			return false;
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
    	int[] counts = new int[VALUES.length];
    	for(SliceElement element : elements.values()){
    		counts[element.getBase().ordinal()]++;
    	}
    	Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(int i=0; i < counts.length; i++){
			int count = counts[i];
			map.put(VALUES[i], count);
		}
		return map;
	}




	public static class Builder implements org.jcvi.jillion.core.util.Builder<DefaultSlice>{
        private final Map<String,SliceElement> elements = new LinkedHashMap<String, SliceElement>();
        
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
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultSlice build() {
            return new DefaultSlice(elements);
        }
        
        
    }
}
