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

package org.jcvi.common.core.assembly.tasm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.AssembledReadBuilder;
import org.jcvi.common.core.assembly.DefaultAssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

/**
 * @author dkatzel
 *
 *
 */
final class DefaultTasmAssembledRead implements TasmAssembledRead{

    private final Map<TasmReadAttribute,String> attributes;
    private final AssembledRead delegate;
    
    
    public static TasmAssembledReadBuilder createBuilder(NucleotideSequence reference, 
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
    private DefaultTasmAssembledRead(
            AssembledRead read) {
        this(read, new EnumMap<TasmReadAttribute,String>(TasmReadAttribute.class));
    }
    /**
     * @param read
     * @param start
     * @param sequenceDirection
     */
    private DefaultTasmAssembledRead(
            AssembledRead read, Map<TasmReadAttribute, String> attributes) {
        this.delegate = read;
        Map<TasmReadAttribute, String> map = new EnumMap<TasmReadAttribute, String>(attributes);
        this.attributes = Collections.unmodifiableMap(map);
    }
    @Override
    public Map<TasmReadAttribute, String> getAttributes() {
        return attributes;
    }
	@Override
	public String getAttributeValue(TasmReadAttribute attribute) {
		if(!hasAttribute(attribute)){
			throw new NoSuchElementException("read does not contain attribute "+attribute);
		}
		return attributes.get(attribute);
	}
	@Override
	public boolean hasAttribute(TasmReadAttribute attribute) {
		return attributes.containsKey(attribute);
	}
	
	
    @Override
	public ReadInfo getReadInfo() {
		return delegate.getReadInfo();
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
    public String getId() {
        return delegate.getId();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return delegate.getNucleotideSequence();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedLength() {
        return delegate.getGappedLength();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedStartOffset() {
        return delegate.getGappedStartOffset();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedEndOffset() {
        return delegate.getGappedEndOffset();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return delegate.asRange();
    }
    
    
    @Override
	public Range getGappedContigRange() {
		return delegate.getGappedContigRange();
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
        if (!(obj instanceof DefaultTasmAssembledRead)) {
            return false;
        }
        DefaultTasmAssembledRead other = (DefaultTasmAssembledRead) obj;
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
	
    private static class Builder implements TasmAssembledReadBuilder{

        private final Map<TasmReadAttribute, String> map =new EnumMap<TasmReadAttribute,String>(TasmReadAttribute.class);
        private final AssembledReadBuilder<AssembledRead> delegate;
        
        
        public Builder(NucleotideSequence reference, 
                String readId,String validBases,
                int offset, Direction dir, Range clearRange,
                int ungappedFullLength) {
            this.delegate = DefaultAssembledRead.createBuilder(reference, readId, validBases, offset, dir, clearRange, ungappedFullLength);
        }
        private Builder(Builder copy){
          	 this.delegate = copy.delegate.copy();
          	 this.map.putAll(copy.map);
          }
          
        @Override
		public TasmAssembledReadBuilder trim(Range trimRange) {
			delegate.trim(trimRange);
			return this;
		}
          @Override
   		public TasmAssembledReadBuilder copy() {
   			return new Builder(this);
   		}
        /**
        * {@inheritDoc}
        */
        @Override
        public TasmAssembledReadBuilder addAllAttributes(
                Map<TasmReadAttribute, String> map) {
            for(Entry<TasmReadAttribute, String> entry : map.entrySet()){
                addAttribute(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<TasmAssembledRead> reference(
                NucleotideSequence reference, int newOffset) {
            delegate.reference(reference, newOffset);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin() {
            return delegate.getBegin();
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
        public AssembledReadBuilder<TasmAssembledRead> setStartOffset(
                int newOffset) {
            delegate.setStartOffset(newOffset);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<TasmAssembledRead> shift(
                int numberOfBases) {
            delegate.shift(numberOfBases);
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
        public TasmAssembledRead build() {
            return new DefaultTasmAssembledRead(delegate.build(), map);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<TasmAssembledRead> reAbacus(
                Range gappedValidRangeToChange, NucleotideSequence newBasecalls) {
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
        public NucleotideSequenceBuilder getNucleotideSequenceBuilder() {
            return delegate.getNucleotideSequenceBuilder();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequence getCurrentNucleotideSequence() {
            return delegate.getCurrentNucleotideSequence();
        }

        
        @Override
		public AssembledReadBuilder<TasmAssembledRead> append(Nucleotide base) {
			delegate.append(base);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> append(
				Iterable<Nucleotide> sequence) {
			delegate.append(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> append(String sequence) {
			delegate.append(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> insert(int offset,
				String sequence) {
			delegate.insert(offset, sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> replace(int offset,
				Nucleotide replacement) {
			delegate.replace(offset, replacement);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> delete(Range range) {
			delegate.delete(range);
			return this;
		}


		@Override
		public int getNumGaps() {
			return delegate.getNumGaps();
		}


		@Override
		public int getNumNs() {
			return delegate.getNumNs();
		}


		@Override
		public int getNumAmbiguities() {
			return delegate.getNumAmbiguities();
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> prepend(String sequence) {
			delegate.prepend(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> insert(int offset,
				Iterable<Nucleotide> sequence) {
			delegate.insert(offset, sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> insert(int offset,
				Nucleotide base) {
			delegate.insert(offset, base);
			return this;
		}


		@Override
		public AssembledReadBuilder<TasmAssembledRead> prepend(
				Iterable<Nucleotide> sequence) {
			delegate.prepend(sequence);
			return this;
		}


        
        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((delegate == null) ? 0 : delegate.hashCode());
            result = prime * result + ((map == null) ? 0 : map.hashCode());
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
            if (!(obj instanceof Builder)) {
                return false;
            }
            Builder other = (Builder) obj;
            if (delegate == null) {
                if (other.delegate != null) {
                    return false;
                }
            } else if (!delegate.equals(other.delegate)) {
                return false;
            }
            if (map == null) {
                if (other.map != null) {
                    return false;
                }
            } else if (!map.equals(other.map)) {
                return false;
            }
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TasmAssembledReadBuilder addAttribute(
                TasmReadAttribute key, String value) {
            map.put(key, value);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TasmAssembledReadBuilder removeAttribute(
                TasmReadAttribute key) {
            map.remove(key);
            return this;
        }
        
    }

}
