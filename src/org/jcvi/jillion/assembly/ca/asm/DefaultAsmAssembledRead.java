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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.List;

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
final class DefaultAsmAssembledRead implements AsmAssembledRead{
    private final boolean isSurrogate;
    private final AssembledRead placedRead;
    
    public static AsmAssembledReadBuilder createBuilder(NucleotideSequence reference, 
    		String readId,
    		String validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength, boolean isSurrogate){
        return new Builder(reference, readId, validBases, 
                offset, dir, clearRange, ungappedFullLength,isSurrogate);
    }
    
    private DefaultAsmAssembledRead(AssembledRead placedRead, boolean isSurrogate) {
        this.placedRead = placedRead;
        this.isSurrogate = isSurrogate;
    }

    @Override
	public ReadInfo getReadInfo() {
		return placedRead.getReadInfo();
	}


	/**
    * {@inheritDoc}
    */
    @Override
    public Direction getDirection() {
        return placedRead.getDirection();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toGappedValidRangeOffset(long referenceOffset) {
        return placedRead.toGappedValidRangeOffset(referenceOffset);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toReferenceOffset(long gappedValidRangeOffset) {
        return placedRead.toReferenceOffset(gappedValidRangeOffset);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return placedRead.getId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return placedRead.getNucleotideSequence();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedLength() {
        return placedRead.getGappedLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedStartOffset() {
        return placedRead.getGappedStartOffset();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getGappedEndOffset() {
        return placedRead.getGappedEndOffset();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return placedRead.asRange();
    }
    @Override
	public Range getGappedContigRange() {
		return placedRead.getGappedContigRange();
	}
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isRepeatSurrogate() {
        return isSurrogate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isSurrogate ? 1231 : 1237);
        result = prime * result
                + ((placedRead == null) ? 0 : placedRead.hashCode());
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
        if (!(obj instanceof DefaultAsmAssembledRead)) {
            return false;
        }
        DefaultAsmAssembledRead other = (DefaultAsmAssembledRead) obj;
        if (isSurrogate != other.isSurrogate) {
            return false;
        }
        if (placedRead == null) {
            if (other.placedRead != null) {
                return false;
            }
        } else if (!placedRead.equals(other.placedRead)) {
            return false;
        }
        return true;
    }

    private static class Builder implements AsmAssembledReadBuilder{
        private boolean isSurrogate=false;        
        private final AssembledReadBuilder<AssembledRead> delegateBuilder;
        
        
        public Builder(NucleotideSequence reference, String readId,String validBases,
                            int offset, Direction dir, Range clearRange,
                            int ungappedFullLength,boolean isSurrogate){
            this.delegateBuilder = DefaultAssembledRead.createBuilder(
                    reference, readId, validBases, offset,
                    dir, clearRange, ungappedFullLength);
            this.isSurrogate = isSurrogate;
        }
        
        private Builder(Builder copy){
       	 this.delegateBuilder = copy.delegateBuilder.copy();
       	 this.isSurrogate = copy.isSurrogate;
       }
       
        @Override
		public AsmAssembledReadBuilder trim(Range trimRange) {
			delegateBuilder.trim(trimRange);
			return this;
		}
       @Override
		public AsmAssembledReadBuilder copy() {
			return new Builder(this);
		}
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reference(NucleotideSequence reference, int newOffset){
            delegateBuilder.reference(reference, newOffset);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin(){
            return delegateBuilder.getBegin();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getId(){
            return delegateBuilder.getId();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setStartOffset(int newOffset){
            delegateBuilder.setStartOffset(newOffset);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shift(int numberOfBases){
            delegateBuilder.shift(numberOfBases);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range getClearRange() {
            return delegateBuilder.getClearRange();
        }



		/**
        * {@inheritDoc}
        */
        @Override
        public Direction getDirection() {
            return delegateBuilder.getDirection();
        }

        

        /**
        * {@inheritDoc}
        */
        @Override
        public int getUngappedFullLength() {
            return delegateBuilder.getUngappedFullLength();
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAsmAssembledRead build(){
            return new DefaultAsmAssembledRead(delegateBuilder.build(),isSurrogate);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, NucleotideSequence newBasecalls){
            delegateBuilder.reAbacus(gappedValidRangeToChange, newBasecalls);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized long getLength(){
            return delegateBuilder.getLength();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd(){
            return delegateBuilder.getEnd();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange(){
            return delegateBuilder.asRange();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequenceBuilder getNucleotideSequenceBuilder() {
            return delegateBuilder.getNucleotideSequenceBuilder();
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequence getCurrentNucleotideSequence(){
            return delegateBuilder.getCurrentNucleotideSequence();
        }


        
        @Override
		public AssembledReadBuilder<AsmAssembledRead> append(Nucleotide base) {
			delegateBuilder.append(base);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> append(
				Iterable<Nucleotide> sequence) {
			delegateBuilder.append(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> append(String sequence) {
			delegateBuilder.append(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> insert(int offset,
				String sequence) {
			delegateBuilder.insert(offset, sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> replace(int offset,
				Nucleotide replacement) {
			delegateBuilder.replace(offset, replacement);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> delete(Range range) {
			delegateBuilder.delete(range);
			return this;
		}


		@Override
		public int getNumGaps() {
			return delegateBuilder.getNumGaps();
		}


		@Override
		public int getNumNs() {
			return delegateBuilder.getNumNs();
		}


		@Override
		public int getNumAmbiguities() {
			return delegateBuilder.getNumAmbiguities();
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> prepend(String sequence) {
			delegateBuilder.prepend(sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> insert(int offset,
				Iterable<Nucleotide> sequence) {
			delegateBuilder.insert(offset, sequence);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> insert(int offset,
				Nucleotide base) {
			delegateBuilder.insert(offset, base);
			return this;
		}


		@Override
		public AssembledReadBuilder<AsmAssembledRead> prepend(
				Iterable<Nucleotide> sequence) {
			delegateBuilder.prepend(sequence);
			return this;
		}


		
        
        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime
                    * result
                    + ((delegateBuilder == null) ? 0 : delegateBuilder
                            .hashCode());
            result = prime * result
                    + (isSurrogate ? 1:0);
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
            if (delegateBuilder == null) {
                if (other.delegateBuilder != null) {
                    return false;
                }
            } else if (!delegateBuilder.equals(other.delegateBuilder)) {
                return false;
            }
            if(isSurrogate!=other.isSurrogate){
                return false;
            }
            return true;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isRepeatSurrogate() {
            return isSurrogate;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public void setRepeatSurrogate(boolean isRepeatSurrogate) {
            this.isSurrogate = isRepeatSurrogate;
            
        }
    }

    protected static final class IllegalReAbacus extends IllegalArgumentException{

        private static final long serialVersionUID = -8272559886165301526L;

        public IllegalReAbacus(List<Nucleotide> oldUngappedBasecalls, List<Nucleotide> newUngappedBasecalls){
            super(String.format("reAbacusing must retain same ungapped basecalls! '%s' vs '%s'", 
                    new NucleotideSequenceBuilder(oldUngappedBasecalls).toString(),
                    new NucleotideSequenceBuilder(newUngappedBasecalls).toString()
                    ));
        }
    }
}
