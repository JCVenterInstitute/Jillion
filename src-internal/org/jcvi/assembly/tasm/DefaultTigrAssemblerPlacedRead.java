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

package org.jcvi.assembly.tasm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.PlacedReadBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultTigrAssemblerPlacedRead implements TigrAssemblerPlacedRead{

    private final Map<TigrAssemblerReadAttribute,String> attributes;
    private final PlacedRead delegate;
    
    
    public static TigrAssemblerPlacedReadBuilder createBuilder(NucleotideSequence reference, 
            String readId,String validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength){
        return new Builder(reference, readId, validBases, offset, dir, clearRange, ungappedFullLength);
    }
    /**
     * @param read
     * @param start
     * @param sequenceDirection
     */
    private DefaultTigrAssemblerPlacedRead(
            PlacedRead read) {
        this(read, new EnumMap<TigrAssemblerReadAttribute,String>(TigrAssemblerReadAttribute.class));
    }
    /**
     * @param read
     * @param start
     * @param sequenceDirection
     */
    private DefaultTigrAssemblerPlacedRead(
            PlacedRead read, Map<TigrAssemblerReadAttribute, String> attributes) {
        this.delegate = read;
        Map<TigrAssemblerReadAttribute, String> map = new EnumMap<TigrAssemblerReadAttribute, String>(attributes);
        this.attributes = Collections.unmodifiableMap(map);
    }
    @Override
    public Map<TigrAssemblerReadAttribute, String> getAttributes() {
        return attributes;
    }
	@Override
	public String getAttributeValue(TigrAssemblerReadAttribute attribute) {
		if(!hasAttribute(attribute)){
			throw new NoSuchElementException("read does not contain attribute "+attribute);
		}
		return attributes.get(attribute);
	}
	@Override
	public boolean hasAttribute(TigrAssemblerReadAttribute attribute) {
		return attributes.containsKey(attribute);
	}
    /**
    * {@inheritDoc}
    */
    @Override
    public Map<Integer, Nucleotide> getSnps() {
        return delegate.getSnps();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range getValidRange() {

        return delegate.getValidRange();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Direction getDirection() {
        return delegate.getDirection();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long toGappedValidRangeOffset(long referenceOffset) {
        return delegate.toGappedValidRangeOffset(referenceOffset);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long toReferenceOffset(long gappedValidRangeOffset) {
        return delegate.toReferenceOffset(gappedValidRangeOffset);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedFullLength() {
        return delegate.getUngappedFullLength();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return delegate.getId();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return delegate.getNucleotideSequence();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        return delegate.getLength();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getStart() {
        return delegate.getStart();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getEnd() {
        return delegate.getEnd();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return delegate.asRange();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        return delegate.compareTo(o);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result
                + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultTigrAssemblerPlacedRead)) {
            return false;
        }
        DefaultTigrAssemblerPlacedRead other = (DefaultTigrAssemblerPlacedRead) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (delegate == null) {
            if (other.delegate != null) {
                return false;
            }
        } else if (!delegate.equals(other.delegate)) {
            return false;
        }
        return true;
    }
	
    private static class Builder implements TigrAssemblerPlacedReadBuilder{

        private final Map<TigrAssemblerReadAttribute, String> map =new EnumMap<TigrAssemblerReadAttribute,String>(TigrAssemblerReadAttribute.class);
        private final PlacedReadBuilder<PlacedRead> delegate;
        
        
        public Builder(NucleotideSequence reference, 
                String readId,String validBases,
                int offset, Direction dir, Range clearRange,
                int ungappedFullLength) {
            this.delegate = DefaultPlacedRead.createBuilder(reference, readId, validBases, offset, dir, clearRange, ungappedFullLength);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TigrAssemblerPlacedReadBuilder addAllAttributes(
                Map<TigrAssemblerReadAttribute, String> map) {
            for(Entry<TigrAssemblerReadAttribute, String> entry : map.entrySet()){
                addAttribute(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> reference(
                NucleotideSequence reference, int newOffset) {
            delegate.reference(reference, newOffset);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getStart() {
            return delegate.getStart();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getId() {
            return delegate.getId();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> setStartOffset(
                int newOffset) {
            delegate.setStartOffset(newOffset);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> shiftRight(
                int numberOfBases) {
            delegate.shiftRight(numberOfBases);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> shiftLeft(
                int numberOfBases) {
            delegate.shiftLeft(numberOfBases);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Range getClearRange() {
            return delegate.getClearRange();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Direction getDirection() {
            return delegate.getDirection();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int getUngappedFullLength() {
            return delegate.getUngappedFullLength();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TigrAssemblerPlacedRead build() {
            return new DefaultTigrAssemblerPlacedRead(delegate.build(), map);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> reAbacus(
                Range gappedValidRangeToChange, String newBasecalls) {
            delegate.reAbacus(gappedValidRangeToChange, newBasecalls);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<TigrAssemblerPlacedRead> reAbacus(
                Range gappedValidRangeToChange, List<Nucleotide> newBasecalls) {
            delegate.reAbacus(gappedValidRangeToChange, newBasecalls);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getLength() {
            return delegate.getLength();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd() {
            return delegate.getEnd();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange() {
            return delegate.asRange();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getBasesBuilder() {
            return delegate.getBasesBuilder();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequence getCurrentNucleotideSequence() {
            return delegate.getCurrentNucleotideSequence();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int compareTo(PlacedReadBuilder<TigrAssemblerPlacedRead> o) {
            int rangeCompare = asRange().compareTo(o.asRange());
            if(rangeCompare !=0){
                return rangeCompare;
            }
            return getId().compareTo(o.getId());
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TigrAssemblerPlacedReadBuilder addAttribute(
                TigrAssemblerReadAttribute key, String value) {
            map.put(key, value);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TigrAssemblerPlacedReadBuilder removeAttribute(
                TigrAssemblerReadAttribute key) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

}
