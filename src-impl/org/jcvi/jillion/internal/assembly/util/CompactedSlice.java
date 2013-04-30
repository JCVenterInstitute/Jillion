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
package org.jcvi.jillion.internal.assembly.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;

/**
 * @author dkatzel
 *
 *
 */
public final class CompactedSlice implements Slice{

	private static final Nucleotide[] VALUES = Nucleotide.values();
	
    private final short[] elements;
    private final String[] ids;
    
    public static final CompactedSlice EMPTY = new Builder().build();
    
    /**
     * @param elements
     * @param ids
     */
    private CompactedSlice(short[] elements, List<String> ids) {
        this.elements = elements;
        this.ids = ids.toArray(new String[ids.size()]);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<SliceElement> iterator() {
    	
        return new Iterator<SliceElement>(){
        	int i=0;
            @Override
            public boolean hasNext() {
                return i<ids.length;
            }

            @Override
            public SliceElement next() {
            	if(!hasNext()){
            		throw new NoSuchElementException();
            	}
            	//i++ returns old value of i
                return getElement(i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
                
            }
            
        };
    }
    
    
    @Override
	public Map<Nucleotide, Integer> getNucleotideCounts() {
		int[] counts = new int[VALUES.length];
		for(int i=0; i < elements.length; i++){
			int ordinal =(elements[i] >>>8) &0xF;
			counts[ordinal]++;
		}
		Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(int i=0; i < counts.length; i++){
			int count = counts[i];
			map.put(VALUES[i], count);
		}
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Arrays.hashCode(ids);
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
    
    private CompactedSliceElement getElement(int i){
    	String id = ids[i];
    	short value =elements[i];
        byte dirAndNuc =(byte)((value >>>8) &0xFF);
        byte qual = (byte)(value & 0xFF);
        return new CompactedSliceElement(id, qual, dirAndNuc);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getCoverageDepth() {
        return ids.length;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsElement(String elementId) {
    	return indexOf(elementId) >=0;
    }

    private int indexOf(String id){
    	if(id==null){
    		for(int i=0; i< ids.length; i++){
        		if(ids[i]==null){
        			return i;
        		}
        	}
    	}else{
    		for(int i=0; i< ids.length; i++){
        		if(id.equals(ids[i])){
        			return i;
        		}
        	}
    	}
    	return -1;
    }
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(SliceElement element : this){
            builder.append(element).append( " | ");
        }
        
        
        return builder.toString();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public SliceElement getSliceElement(String elementId) {
        int index= indexOf(elementId);
        if(index<0){
            throw new IllegalArgumentException(elementId + " not in slice");
        }
        return getElement(index);
    }
    
    public static final class Builder implements org.jcvi.jillion.core.util.Builder<CompactedSlice>{

        GrowableShortArray bytes = new GrowableShortArray(1024);
        List<String> ids = new ArrayList<String>();
        public Builder addSliceElement(SliceElement element){            
            return addSliceElement(element.getId(),element.getBase(), element.getQuality(), element.getDirection());
        }
        public Builder addSliceElements(Iterable<? extends SliceElement> elements){
            for(SliceElement e : elements){
                addSliceElement(e);
            }
            return this;
        }
        public Builder addSliceElement(String id, Nucleotide base, PhredQuality quality, Direction dir){
        	CompactedSliceElement compacted = new CompactedSliceElement(id, base, quality, dir);
        	int value = compacted.getEncodedDirAndNucleotide() <<8;
        	value |= (compacted.getEncodedQuality() &0xFF);
            bytes.append((short)value);
            
            ids.add(id);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public CompactedSlice build() {
            return new CompactedSlice(bytes.toArray(), ids);
        }
        
    }

}
