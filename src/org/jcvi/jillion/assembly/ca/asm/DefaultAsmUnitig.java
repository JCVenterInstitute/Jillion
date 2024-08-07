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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.Collection;

import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
final class DefaultAsmUnitig implements AsmUnitig{

    private final Contig<AsmAssembledRead> delegate;

    public static AsmUnitigBuilder createBuilder(String id, NucleotideSequence consensus){
    	return new DefaultAsmUnitigBuilder(id,consensus);
    }
    
    public DefaultAsmUnitig(Contig<AsmAssembledRead> delegate) {
        this.delegate = delegate;
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
    public long getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<AsmAssembledRead> getReadIterator() {
        return delegate.getReadIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getConsensusSequence() {
        return delegate.getConsensusSequence();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AsmAssembledRead getRead(String id) {
        return delegate.getRead(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsRead(String placedReadId) {
        return delegate.containsRead(placedReadId);
    }
    

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AsmUnitig)) {
			return false;
		}
		AsmUnitig other = (AsmUnitig) obj;		
		if (!getId().equals(other.getId())) {
			return false;
		}
		if (!getConsensusSequence().equals(other.getConsensusSequence())) {
			return false;
		}
		if (getNumberOfReads()!=other.getNumberOfReads()) {
			return false;
		}
		try(StreamingIterator<AsmAssembledRead> readIter = getReadIterator()){
			
			while(readIter.hasNext()){
				AsmAssembledRead read = readIter.next();
				String readId = read.getId();
				if(!other.containsRead(readId)){
					return false;
				}
				if(!read.equals(other.getRead(readId))){
					return false;
				}
			}
		}		
		return true;
	}
	
	
	private static final class DefaultAsmUnitigBuilder implements AsmUnitigBuilder{

		private final AsmContigBuilder delegate;
		
		public DefaultAsmUnitigBuilder(String id, NucleotideSequence consensus){
			delegate = DefaultAsmContig.createBuilder(id, consensus);
		}
		@Override
		public AsmUnitigBuilder setContigId(
				String contigId) {
			delegate.setContigId(contigId);
			return this;
		}

		@Override
		public String getContigId() {
			return delegate.getContigId();
		}

		@Override
		public int numberOfReads() {
			return delegate.numberOfReads();
		}

		@Override
		public AsmUnitigBuilder addRead(
				AsmAssembledRead placedRead) {
			delegate.addRead(placedRead);
			return this;
		}

		@Override
		public AsmUnitigBuilder addAllReads(
				Iterable<AsmAssembledRead> reads) {
			delegate.addAllReads(reads);
			return this;
		}

		@Override
		public Collection<? extends AssembledReadBuilder<AsmAssembledRead>> getAllAssembledReadBuilders() {
			return delegate.getAllAssembledReadBuilders();
		}

		@Override
		public AssembledReadBuilder<AsmAssembledRead> getAssembledReadBuilder(
				String readId) {
			return delegate.getAssembledReadBuilder(readId);
		}

		@Override
		public AsmUnitigBuilder removeRead(
				String readId) {
			delegate.removeRead(readId);
			return this;
		}

		@Override
		public NucleotideSequenceBuilder getConsensusBuilder() {
			return delegate.getConsensusBuilder();
		}

		@Override
		public AsmUnitigBuilder recallConsensus(
				ConsensusCaller consensusCaller,
				QualitySequenceDataStore qualityDataStore,
				GapQualityValueStrategy qualityValueStrategy) {
			delegate.recallConsensus(consensusCaller,qualityDataStore,qualityValueStrategy);
			return this;
		}

		@Override
		public AsmUnitigBuilder recallConsensus(
				ConsensusCaller consensusCaller) {
			delegate.recallConsensus(consensusCaller);
			return this;
		}

		@Override
		public AsmUnitigBuilder recallConsensusNow() {
			delegate.recallConsensusNow();
			return this;
		}

		@Override
		public AsmUnitig build() {
			return new DefaultAsmUnitig(delegate.build());
		}

		@Override
		public AsmUnitigBuilder addRead(String readId, String validBases,
				int offset, Direction dir, Range clearRange,
				int ungappedFullLength, boolean isSurrogate) {
			delegate.addRead(readId, validBases,offset,dir,clearRange,ungappedFullLength,isSurrogate);
			return this;
		}
		
		
		
	}
}
