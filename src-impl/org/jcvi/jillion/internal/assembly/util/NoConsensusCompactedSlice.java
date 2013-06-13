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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public class NoConsensusCompactedSlice implements Slice{
	
    private final short[] elements;
    private final String[] ids;
    
    public static final NoConsensusCompactedSlice EMPTY = (NoConsensusCompactedSlice) new SliceBuilder().build();
    
    /**
     * @param elements
     * @param ids
     */
    public NoConsensusCompactedSlice(short[] elements, List<String> ids) {
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
		int[] counts = new int[Nucleotide.VALUES.size()];
		for(int i=0; i < elements.length; i++){
			int ordinal =(elements[i] >>>8) &0xF;
			counts[ordinal]++;
		}
		Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(int i=0; i < counts.length; i++){
			int count = counts[i];
			map.put(Nucleotide.VALUES.get(i), count);
		}
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Arrays.hashCode(ids);
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
        if(getCoverageDepth() !=other.getCoverageDepth()){
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
		Iterator<SliceElement> iter = other.iterator();
		while(iter.hasNext()){
			
			SliceElement element = iter.next();
			if(!element.equals(getSliceElement(element.getId()))){
				return false;
			}
			
		}
		 
       return true;
            
    }
    
    private CompactedSliceElement getElement(int i){
    	String id = ids[i];
        return CompactedSliceElement.create(id, elements[i]);
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
        StringBuilder builder = new StringBuilder("consensus = ")
        						.append(getConsensusCall())
        						.append(" ");
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
            return null;
        }
        return getElement(index);
    }

	@Override
	public Nucleotide getConsensusCall() {
		return null;
	}
    
   

}
