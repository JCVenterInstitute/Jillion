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

package org.jcvi.assembly.slice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

/**
 * @author dkatzel
 *
 *
 */
public class CompactedSlice implements Slice{

    private final byte[] elements;
    private final List<String> ids;
    
    
    /**
     * @param elements
     * @param ids
     */
    private CompactedSlice(byte[] elements, List<String> ids) {
        this.elements = elements;
        this.ids = ids;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<SliceElement> iterator() {
        return new Iterator<SliceElement>(){
            Iterator<String> idIter = ids.iterator();
            @Override
            public boolean hasNext() {
                return idIter.hasNext();
            }

            @Override
            public SliceElement next() {
                String id= idIter.next();
                return getSliceElement(id);
            }

            @Override
            public void remove() {
                idIter.remove();
                
            }
            
        };
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getCoverageDepth() {
        return ids.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsElement(String elementId) {
        return ids.contains(elementId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public SliceElement getSliceElement(String elementId) {
        int index= ids.indexOf(elementId);
        if(index<0){
            throw new IllegalArgumentException(elementId + " not in slice");
        }
        ByteBuffer buf = ByteBuffer.wrap(elements);
        buf.position(index *CompactedSliceElementCodec.SIZE_OF_ENCODED_DATA);
        byte[] tmp = new byte[CompactedSliceElementCodec.SIZE_OF_ENCODED_DATA];
        buf.get(tmp);
        return new CompactedSliceElement(ids.get(index),tmp);
    }
    
    public static class Builder implements org.jcvi.Builder<CompactedSlice>{

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        List<String> ids = new ArrayList<String>();
        public Builder addSliceElement(SliceElement element){            
            return addSliceElement(element.getId(),element.getBase(), element.getQuality(), element.getSequenceDirection());
        }
        public Builder addSliceElements(Iterable<? extends SliceElement> elements){
            for(SliceElement e : elements){
                addSliceElement(e);
            }
            return this;
        }
        public Builder addSliceElement(String id, NucleotideGlyph base, PhredQuality quality, SequenceDirection dir){
            try {
                bytes.write(CompactedSliceElementCodec.INSTANCE.compact(base, quality, dir));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
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
