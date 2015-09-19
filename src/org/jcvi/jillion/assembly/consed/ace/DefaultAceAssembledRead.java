/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

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

final class DefaultAceAssembledRead implements AceAssembledRead {
    private final PhdInfo phdInfo;
    private final AssembledRead placedRead;
    
    
    public static AceAssembledReadBuilder createBuilder(String readId, NucleotideSequence validBases,int offset,
            Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength,
            AceContigBuilder callback){
        return new Builder(readId, validBases, offset, 
                dir, clearRange, phdInfo, ungappedFullLength,callback);
    }
    private DefaultAceAssembledRead(AssembledRead placedRead, PhdInfo phdInfo) {
        this.placedRead = placedRead;
        this.phdInfo =phdInfo;
    }

    @Override
	public ReadInfo getReadInfo() {
		return placedRead.getReadInfo();
	}
	@Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }


    @Override
	public String toString() {
		return "DefaultAcePlacedRead [placedRead=" + placedRead + ", phdInfo="
				+ phdInfo + "]";
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
    @Override
	public Range getGappedContigRange() {
		return placedRead.getGappedContigRange();
	}
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return placedRead.asRange();
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
    public long toGappedValidRangeOffset(long referenceIndex) {
        return placedRead.toGappedValidRangeOffset(referenceIndex);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        return placedRead.toReferenceOffset(validRangeIndex);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phdInfo == null) ? 0 : phdInfo.hashCode());
        result = prime * result
                + ((placedRead == null) ? 0 : placedRead.hashCode());
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        DefaultAceAssembledRead other = (DefaultAceAssembledRead) obj;
        if (phdInfo == null) {
            if (other.phdInfo != null){
                return false;
            }
        } else if (!phdInfo.equals(other.phdInfo)){
            return false;
        }
        if (placedRead == null) {
            if (other.placedRead != null){
                return false;
            }
        } else if (!placedRead.equals(other.placedRead)){
            return false;
        }        
        return true;
    }


    static final class Builder implements AceAssembledReadBuilder{
        private final PhdInfo phdInfo;        
        private final AssembledReadBuilder<AssembledRead> delegateBuilder;
        private AceContigBuilder contigBuilder;
        
        public Builder(String readId, NucleotideSequence validBases,int offset,
                            Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength,
                            AceContigBuilder callback){
            this.delegateBuilder = DefaultAssembledRead.createBuilder(
                    readId, validBases, offset, dir,
                    clearRange, ungappedFullLength);
            this.phdInfo = phdInfo;
            this.contigBuilder = callback;
        }
        
        private Builder(Builder copy){
        	 this.delegateBuilder = copy.delegateBuilder.copy();
        	 this.phdInfo = copy.phdInfo;
        }
        
        
        @Override
		public AceAssembledReadBuilder trim(Range trimRange) {
			delegateBuilder.trim(trimRange);
			fireContigSizeUpdate();
			return this;
		}

		@Override
		public AceAssembledReadBuilder copy() {
			return new Builder(this);
		}
		
		public void setParentContigBuilder(AceContigBuilder contigBuilder){
			this.contigBuilder = contigBuilder;
			
		}
		
		private void fireContigSizeUpdate(){
			if(contigBuilder !=null){
				contigBuilder.updatedReadRange(getId(), getBegin(), getEnd());
			}
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
            fireContigSizeUpdate();
            return this;
        }
      


		/**
        * {@inheritDoc}
        */
        @Override
        public Builder shift(int numberOfBases){
            delegateBuilder.shift(numberOfBases);
            fireContigSizeUpdate();
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
        public PhdInfo getPhdInfo() {
            return phdInfo;
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
        public DefaultAceAssembledRead build(NucleotideSequence consensus){
            return new DefaultAceAssembledRead(delegateBuilder.build(consensus),phdInfo);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, NucleotideSequence newBasecalls){
            delegateBuilder.reAbacus(gappedValidRangeToChange, newBasecalls);
            fireContigSizeUpdate();
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
        public synchronized NucleotideSequence getCurrentNucleotideSequence(){
            return delegateBuilder.getCurrentNucleotideSequence();
        }

		@Override
		public AceAssembledReadBuilder append(Nucleotide base) {
			delegateBuilder.append(base);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder append(
				Iterable<Nucleotide> sequence) {
			delegateBuilder.append(sequence);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder append(String sequence) {
			delegateBuilder.append(sequence);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder insert(int offset,
				String sequence) {
			delegateBuilder.insert(offset, sequence);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder replace(int offset,
				Nucleotide replacement) {
			delegateBuilder.replace(offset, replacement);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder delete(Range range) {
			delegateBuilder.delete(range);
			fireContigSizeUpdate();
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
		public AceAssembledReadBuilder prepend(String sequence) {
			delegateBuilder.prepend(sequence);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder insert(int offset,
				Iterable<Nucleotide> sequence) {
			delegateBuilder.insert(offset, sequence);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder insert(int offset,
				Nucleotide base) {
			delegateBuilder.insert(offset, base);
			fireContigSizeUpdate();
			return this;
		}


		@Override
		public AceAssembledReadBuilder prepend(
				Iterable<Nucleotide> sequence) {
			delegateBuilder.prepend(sequence);
			fireContigSizeUpdate();
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
                    + ((phdInfo == null) ? 0 : phdInfo.hashCode());
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
            if (phdInfo == null) {
                if (other.phdInfo != null) {
                    return false;
                }
            } else if (!phdInfo.equals(other.phdInfo)) {
                return false;
            }
            return true;
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
