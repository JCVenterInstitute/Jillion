/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.util.slice;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public final class CompactedSlice implements IdedSlice{

    private final byte[] elements;
    private final String[] ids;
    
    public static final CompactedSlice EMPTY = new Builder().build();
    
    /**
     * @param elements
     * @param ids
     */
    private CompactedSlice(byte[] elements, List<String> ids) {
        this.elements = elements;
        this.ids = ids.toArray(new String[ids.size()]);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<IdedSliceElement> iterator() {
    	
        return new Iterator<IdedSliceElement>(){
        	int i=0;
            @Override
            public boolean hasNext() {
                return i<ids.length;
            }

            @Override
            public IdedSliceElement next() {
            	if(!hasNext()){
            		throw new NoSuchElementException();
            	}
            	String id = ids[i];
            	int offset = i*2;
                byte dirAndNuc =elements[offset];
                byte qual = elements[offset+1];
                i++;
                return new CompactedSliceElement(id, qual, dirAndNuc);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
                
            }
            
        };
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
    public IdedSliceElement getSliceElement(String elementId) {
        int index= indexOf(elementId);
        if(index<0){
            throw new IllegalArgumentException(elementId + " not in slice");
        }
        int offset = index*2;
        byte dirAndNuc =elements[offset];
        byte qual = elements[offset+1];
        return new CompactedSliceElement(elementId, qual, dirAndNuc);
    }
    
    public static final class Builder implements org.jcvi.jillion.core.util.Builder<CompactedSlice>{

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        List<String> ids = new ArrayList<String>();
        public Builder addSliceElement(IdedSliceElement element){            
            return addSliceElement(element.getId(),element.getBase(), element.getQuality(), element.getSequenceDirection());
        }
        public Builder addSliceElements(Iterable<? extends IdedSliceElement> elements){
            for(IdedSliceElement e : elements){
                addSliceElement(e);
            }
            return this;
        }
        public Builder addSliceElement(String id, Nucleotide base, PhredQuality quality, Direction dir){
        	CompactedSliceElement compacted = new CompactedSliceElement(id, base, quality, dir);
            bytes.write(compacted.getEncodedDirAndNucleotide());
            bytes.write(compacted.getEncodedQuality());
            
            ids.add(id);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public CompactedSlice build() {
            return new CompactedSlice(bytes.toByteArray(), ids);
        }
        
    }

}
