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
package org.jcvi.jillion.assembly.tigr.tasm;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.internal.assembly.DefaultAssembledRead;

/**
 * @author dkatzel
 *
 *
 */
final class DefaultTasmAssembledRead implements TasmAssembledRead{
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
        this.delegate = read;      
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

        private final AssembledReadBuilder<AssembledRead> delegate;
        
        
        public Builder(NucleotideSequence reference, 
                String readId,String validBases,
                int offset, Direction dir, Range clearRange,
                int ungappedFullLength) {
            this.delegate = DefaultAssembledRead.createBuilder(reference, readId, validBases, offset, dir, clearRange, ungappedFullLength);
        }
        private Builder(Builder copy){
          	 this.delegate = copy.delegate.copy();
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
            return new DefaultTasmAssembledRead(delegate.build());
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
           
            return true;
        }
        
    }

}
