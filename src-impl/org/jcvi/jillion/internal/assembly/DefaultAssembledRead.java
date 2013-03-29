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
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.assembly;

import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;


public final class DefaultAssembledRead implements AssembledRead {

    private final long start;
    private final byte directionOrdinal;
    private final ReferenceMappedNucleotideSequence sequence;
    private final String id;
    private final ReadInfo readInfo;
    
    public static AssembledReadBuilder<AssembledRead> createBuilder(NucleotideSequence reference, 
            String readId,NucleotideSequence validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength){
        return new Builder(reference, readId, validBases, offset, dir, 
                clearRange, ungappedFullLength);
    }
    
    public static AssembledReadBuilder<AssembledRead> createBuilder(NucleotideSequence reference, 
            String readId,String validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength){
    	 return createBuilder(reference, readId, new NucleotideSequenceBuilder(validBases).build(), offset, dir, 
                 clearRange, ungappedFullLength);
    }
    
    public DefaultAssembledRead(String id, ReferenceMappedNucleotideSequence sequence, long start, Direction sequenceDirection, int ungappedFullLength, Range validRange){
       this.id = id;
       this.sequence = sequence;
        this.start= start;
        this.directionOrdinal = (byte)sequenceDirection.ordinal();
        this.readInfo = new ReadInfo(validRange, ungappedFullLength);
    }
    
    
    @Override
	public ReadInfo getReadInfo() {
		return readInfo;
	}

	@Override
    public long getGappedLength() {
        return sequence.getLength();
    }

    @Override
    public long getGappedStartOffset() {
        return start;
    }
    
    
    @Override
	public String toString() {
		return "DefaultPlacedRead [start=" + start + ", directionOrdinal="
				+ directionOrdinal + ", id=" + id + "]";
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result + sequence.hashCode();
        result = prime * result + directionOrdinal;
        result = prime * result + (int) (start ^ (start >>> 32));
        return result;
    }
    /**
     * Two PlacedReads are equal if they have the same start value
     * and they have their reads are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj instanceof AssembledRead){           
        	AssembledRead other = (AssembledRead) obj;
        	if(getId()==null && other.getId() !=null){
        		return false;
        	}
        	if(!getId().equals(other.getId())){
        		return false;
        	}
        	if(getNucleotideSequence()==null && other.getNucleotideSequence() !=null){
        		return false;
        	}
        	if(other.getNucleotideSequence() ==null){
        		return false;
        	}
        	if(!getNucleotideSequence().equals(other.getNucleotideSequence())){
        		return false;
        	}
        	
            return start== other.getGappedStartOffset() 
            && getDirection() == other.getDirection();
        }
        return false;
        
    }
    
    @Override
    public Direction getDirection() {
        return Direction.values()[directionOrdinal];
    }
  
    @Override
    public long getGappedEndOffset() {
        return getGappedStartOffset()+getGappedLength()-1;
    }

    public Map<Integer, Nucleotide> getDifferenceMap(){
       return getNucleotideSequence().getDifferenceMap();
        
    }
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return sequence;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getGappedStartOffset();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getGappedStartOffset() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range " + validRangeIndex);
        }
        if(validRangeIndex > getGappedLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return getGappedContigRange();
    }
    
    @Override
	public Range getGappedContigRange() {
		return Range.of(getGappedStartOffset(), getGappedEndOffset());
	}

	private static class Builder implements AssembledReadBuilder<AssembledRead>{
        private final String readId;
        /**
         * Our original encoded sequence.  If we 
         * edit the basecalls, this will get set to null
         * and we use {@link #basesBuilder} instead.
         */
        private NucleotideSequence originalSequence;
        /**
         * Our edited sequence, only used if needed
         * since it takes up more memory.
         */
        private NucleotideSequenceBuilder basesBuilder=null;
        private int offset;
        private Range clearRange;
        private NucleotideSequence reference;
        private final Direction dir;
        private int ungappedFullLength;
        
        
        private Builder( Builder copy){
        	this.readId = copy.readId;
            this.dir =copy.dir;
            this.clearRange = copy.clearRange;
            this.offset = copy.offset;
            this.originalSequence = copy.originalSequence;
            this.basesBuilder =copy.basesBuilder==null? null: copy.basesBuilder.copy();
            this.reference = copy.reference;
            this.ungappedFullLength = copy.ungappedFullLength;
        }
        
        public Builder(NucleotideSequence reference, String readId, NucleotideSequence validBases,
                            int offset, Direction dir, Range clearRange,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.clearRange = clearRange;
            this.offset = offset;
            this.originalSequence = validBases;
            this.basesBuilder =null;
            if(offset + validBases.getLength() > reference.getLength()){
                throw new IllegalArgumentException(
                		String.format("read %s , last offset %d goes beyond the reference (length %d)",
                				readId, offset + validBases.getLength(), reference.getLength()));
            }
            if(offset <0){
                throw new IllegalArgumentException("read goes before the reference");
            }
            if(ungappedFullLength < clearRange.getEnd()){
            	throw new IllegalArgumentException("clear range extends beyond ungapped full length");
            }
            this.reference = reference;
            this.ungappedFullLength = ungappedFullLength;
        }
        
        @Override
		public AssembledReadBuilder<AssembledRead> copy() {
			return new Builder(this);
		}
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reference(NucleotideSequence reference, int newOffset){
            if(reference ==null){
                throw new NullPointerException("reference can not be null");
            }
            this.reference = reference;
            this.offset = newOffset;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin(){
            return offset;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getId(){
            return readId;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setStartOffset(int newOffset){
            this.offset = newOffset;
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shift(int numberOfBases){
            return setStartOffset(offset+numberOfBases);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range getClearRange() {
            return clearRange;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public Direction getDirection() {
            return dir;
        }

        

        /**
        * {@inheritDoc}
        */
        @Override
        public int getUngappedFullLength() {
            return ungappedFullLength;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized AssembledRead build(){
        	final NucleotideSequenceBuilder finalBuilder;
        
        	if(originalSequence !=null){
        		finalBuilder = new NucleotideSequenceBuilder(originalSequence);
        	}else{
        		finalBuilder = basesBuilder;
        	}
            ReferenceMappedNucleotideSequence updatedEncodedBasecalls = finalBuilder
            																.setReferenceHint(reference, offset)
            																.buildReferenceEncodedNucleotideSequence();
            return new DefaultAssembledRead(readId, 
            		updatedEncodedBasecalls, 
            		offset, dir, 
            		ungappedFullLength,
            		clearRange);
        }

        /**
        * {@inheritDoc}
        */
         @Override
        public Builder reAbacus(Range gappedValidRangeToChange, NucleotideSequence newBasecalls){
        	
        	 NucleotideSequence newUngappedBasecalls = new NucleotideSequenceBuilder(newBasecalls)
				.ungap()
				.build();
        	 
            NucleotideSequence oldUngappedBasecalls = getNucleotideSequenceBuilder()
            										.copy()
            										.trim(gappedValidRangeToChange)
            										.ungap()
            										.build();
           
            if(!oldUngappedBasecalls.equals(newUngappedBasecalls)){
                throw new IllegalReAbacus(oldUngappedBasecalls,newUngappedBasecalls);
            }
            basesBuilder.delete(gappedValidRangeToChange);
            basesBuilder.insert((int)gappedValidRangeToChange.getBegin(), newBasecalls);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized long getLength(){
            if(basesBuilder !=null){
                return basesBuilder.getLength();
            }
            return originalSequence.getLength();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd(){
            return offset + getLength()-1;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange(){
            return Range.of(offset,getEnd());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequenceBuilder getNucleotideSequenceBuilder() {
            if(basesBuilder==null){
                this.basesBuilder = new NucleotideSequenceBuilder(originalSequence);
                originalSequence=null;
            }
            return basesBuilder;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequence getCurrentNucleotideSequence(){
            if(originalSequence !=null){
                return originalSequence;
            }
            return basesBuilder.build();
        }
       

        @Override
		public Builder append(Nucleotide base) {
        	getNucleotideSequenceBuilder().append(base);
        	if(base !=Nucleotide.Gap){
        		expandValidRangeEnd(1);
        		ungappedFullLength++;
        	}
			return this;
		}


		@Override
		public Builder append(Iterable<Nucleotide> sequence) {
			NucleotideSequenceBuilder validRangeBuilder =getNucleotideSequenceBuilder();
			//have to get old and new length 
			//because we don't know how long iterable is
			//(there's no size method on iterable)
			long oldLength = validRangeBuilder.getUngappedLength();
			validRangeBuilder.append(sequence);
			long newLength = validRangeBuilder.getUngappedLength();
			long numberUngappedBasesAdded = newLength-oldLength;
			//expand valid range end by length of insert
			expandValidRangeEnd(numberUngappedBasesAdded);
			//update ungapped full length accordingly
			ungappedFullLength+=numberUngappedBasesAdded;
			return this;
		}


		@Override
		public Builder append(String sequence) {
			return append(new NucleotideSequenceBuilder(sequence));
		}


		@Override
		public Builder insert(int offset, String sequence) {
			return insert(offset, new NucleotideSequenceBuilder(sequence));
		}


		@Override
		public AssembledReadBuilder<AssembledRead> trim(Range trimRange) {
			NucleotideSequence untrimmed = getCurrentNucleotideSequence();
			int numLeft =untrimmed.getUngappedOffsetFor((int)trimRange.getBegin());
			int numRight =(int)(untrimmed.getUngappedLength()-1  -untrimmed.getUngappedOffsetFor((int)trimRange.getEnd()));
			
			
			//for now we are actually trimming the sequence
			//but future versions will only update
			//valid range when we start tracking
			//the full sequence...
			getNucleotideSequenceBuilder()
				.trim(trimRange);
			if(dir == Direction.FORWARD){
				this.contractValidRangeBegin(numLeft);
				this.contractValidRangeEnd(numRight);
			}else{
				this.contractValidRangeBegin(numRight);
				this.contractValidRangeEnd(numLeft);
			}
			//shift this sequence the number of bases up
			//from its old start
			this.shift((int)trimRange.getBegin());
			return this;
		}

		@Override
		public Builder replace(int offset, Nucleotide replacement) {
			
			NucleotideSequenceBuilder sequenceBuilder = getNucleotideSequenceBuilder();
			long oldLength = sequenceBuilder.getUngappedLength();
			sequenceBuilder.replace(offset, replacement);
			long newLength = sequenceBuilder.getUngappedLength();
			//have to modify valid range
			//if number of gaps has changed
			if(newLength < oldLength){
				//we replaced a nongap with a gap
				ungappedFullLength--;				
				contractValidRangeEnd(1);
			}else if(newLength > oldLength){
				ungappedFullLength++;
				expandValidRangeEnd(1);
			}
			return this;
		}


		@Override
		public Builder delete(Range range) {
			NucleotideSequenceBuilder sequenceBuilder = getNucleotideSequenceBuilder();
			long oldUngappedLength = sequenceBuilder.getUngappedLength();
			sequenceBuilder.delete(range);
			long newUngappedLength = sequenceBuilder.getUngappedLength();
			long numberOfUngappedBasesDeleted = oldUngappedLength -newUngappedLength;
			if(numberOfUngappedBasesDeleted>0){
				//we only ever contract the end of the valid
				//range since the good part always starts
				//at the same place									
				contractValidRangeEnd(numberOfUngappedBasesDeleted);
				//update ungapped full length accordingly
				ungappedFullLength-=numberOfUngappedBasesDeleted;
			}
			
			return this;
		}


		@Override
		public int getNumGaps() {
			return getNucleotideSequenceBuilder()
					.getNumGaps();
		}


		@Override
		public int getNumNs() {
			return getNucleotideSequenceBuilder()
					.getNumNs();
		}


		@Override
		public int getNumAmbiguities() {
			return getNucleotideSequenceBuilder()
						.getNumAmbiguities();
		}


		@Override
		public Builder prepend(String sequence) {
			return prepend(new NucleotideSequenceBuilder(sequence));
		}


		@Override
		public Builder insert(int offset,
				Iterable<Nucleotide> sequence) {
			NucleotideSequenceBuilder validRangeBuilder =getNucleotideSequenceBuilder();
			//have to get old and new length 
			//because we don't know how long iterable is
			//(there's no size method on iterable)
			long oldLength = validRangeBuilder.getUngappedLength();
			validRangeBuilder.insert(offset, sequence);
			long newLength = validRangeBuilder.getUngappedLength();
			long numberOfNonGapsAdded = newLength-oldLength;
			//expand valid range end by length of insert
			expandValidRangeEnd(numberOfNonGapsAdded);
			
			ungappedFullLength+=numberOfNonGapsAdded;
			return this;
		}


		@Override
		public Builder insert(int offset, Nucleotide base) {
			NucleotideSequenceBuilder validRangeBuilder =getNucleotideSequenceBuilder();
			validRangeBuilder.insert(offset, base);
			if(base !=Nucleotide.Gap){
				//expand valid range end by 1 to include added base
				expandValidRangeEnd(1);
				ungappedFullLength++;
			}
			return this;
		}


		@Override
		public Builder prepend(Iterable<Nucleotide> sequence) {
			return insert(0, sequence);
		}

		

		private Builder expandValidRangeEnd(long units) {
			Range updatedClearRange = new Range.Builder(clearRange)
											.expandEnd(units)
											.build();
			clearRange =updatedClearRange;
			return this;
		}

		private Builder contractValidRangeBegin(long units) {
			Range updatedClearRange = new Range.Builder(clearRange)
											.contractBegin(units)
											.build();
			clearRange =updatedClearRange;
			return this;
		}

		private Builder contractValidRangeEnd(long units) {
			Range updatedClearRange = new Range.Builder(clearRange)
											.contractEnd(units)
											.build();
			clearRange =updatedClearRange;
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
                    + ((readId == null) ? 0 : readId.hashCode());
            result = prime * result
                    + ((reference == null) ? 0 : reference.hashCode());
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
            if (readId == null) {
                if (other.readId != null) {
                    return false;
                }
            } else if (!readId.equals(other.readId)) {
                return false;
            }
            if (reference == null) {
                if (other.reference != null) {
                    return false;
                }
            } else if (!reference.equals(other.reference)) {
                return false;
            }
            return true;
        }


		
        
        
        
    }

    protected static final class IllegalReAbacus extends IllegalArgumentException{

        private static final long serialVersionUID = -8272559886165301526L;

        public IllegalReAbacus(NucleotideSequence  oldUngappedBasecalls, NucleotideSequence newUngappedBasecalls){
            super(String.format("reAbacusing must retain same ungapped basecalls! '%s' vs '%s'", 
                   oldUngappedBasecalls,
                    newUngappedBasecalls
                    ));
        }
    
    }
}
