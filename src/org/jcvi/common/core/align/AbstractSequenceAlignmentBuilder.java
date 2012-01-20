package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.ResidueSequenceBuilder;

public abstract class AbstractSequenceAlignmentBuilder
		<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>, B extends ResidueSequenceBuilder<R, S>> implements SequenceAlignmentBuilder<R, S,A>{

	private final B querySequenceBuilder, subjectSequenceBuilder;
	private int numMatches=0, numMisMatches=0;
	private int alignmentLength=0;
	private int numGaps=0;
	public AbstractSequenceAlignmentBuilder(){
		querySequenceBuilder = createSequenceBuilder();
		subjectSequenceBuilder = createSequenceBuilder();
	}
	
	protected abstract B createSequenceBuilder();
	protected abstract A createAlignment(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				S queryAlignment, S subjectAlignment);
	
	protected abstract Iterable<R> parse(String sequence);
	
	@Override
	public SequenceAlignmentBuilder<R, S,A> addMatches(String matchedSequence) {
		return addMatches(parse(matchedSequence));
	}
	@Override
	public A build() {
		final double percentIdentity;
		if(alignmentLength==0){
			percentIdentity =0D;
		}else{
			percentIdentity = ((double)numMatches)/alignmentLength;
		}
		return createAlignment(percentIdentity, alignmentLength, numMisMatches, numGaps, 
				querySequenceBuilder.build(), subjectSequenceBuilder.build());
	}

	@Override
	public SequenceAlignmentBuilder<R, S,A> addMatch(R match) {
		numMatches++;
		return appendToBuilders(match,match);
	}
	
	@Override
	public SequenceAlignmentBuilder<R, S,A> addMatches(Iterable<R> matches) {
		for(R match : matches){
			addMatch(match);
		}
		return this;
	}

	@Override
	public SequenceAlignmentBuilder<R, S,A> addMismatch(R query, R subject) {
		numMisMatches++;
		appendToBuilders(query, subject);
		return this;
	}

	private SequenceAlignmentBuilder<R, S,A> appendToBuilders(R query, R subject) {
		querySequenceBuilder.append(query);
		subjectSequenceBuilder.append(subject);
		alignmentLength++;
		return this;
	}

	@Override
	public SequenceAlignmentBuilder<R, S,A> addGap(R query, R subject) {
		numGaps++;
		return appendToBuilders(query,subject);
	}

	@Override
	public SequenceAlignmentBuilder<R, S,A> reverse() {
		querySequenceBuilder.reverse();
		subjectSequenceBuilder.reverse();
		return this;
	}

	
	protected abstract class SequenceAlignmentImpl implements SequenceAlignment<R, S>{

		private final double percentIdentity;
		private final int alignmentLength, numMismatches, numGap;
		private final S queryAlignment, subjectAlignment;
		
		
		
		public SequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				S queryAlignment, S subjectAlignment) {
			this.percentIdentity = percentIdentity;
			this.alignmentLength = alignmentLength;
			this.numMismatches = numMismatches;
			this.numGap = numGap;
			this.queryAlignment = queryAlignment;
			this.subjectAlignment = subjectAlignment;
		}

		@Override
		public double getPercentIdentity() {
			return percentIdentity;
		}

		@Override
		public int getAlignmentLength() {
			return alignmentLength;
		}

		@Override
		public int getNumberOfMismatches() {
			return numMismatches;
		}

		@Override
		public int getNumberOfGapOpenings() {
			return numGap;
		}

		@Override
		public S getGappedQueryAlignment() {
			return queryAlignment;
		}

		@Override
		public S getGappedSubjectAlignment() {
			return subjectAlignment;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + alignmentLength;
			result = prime * result + numGap;
			result = prime * result + numMismatches;
			long temp;
			temp = Double.doubleToLongBits(percentIdentity);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime
					* result
					+ queryAlignment.hashCode();
			result = prime
					* result
					+ subjectAlignment.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SequenceAlignmentImpl other = (SequenceAlignmentImpl) obj;
			if (alignmentLength != other.alignmentLength)
				return false;
			if (numGap != other.numGap)
				return false;
			if (numMismatches != other.numMismatches)
				return false;
			if (Double.doubleToLongBits(percentIdentity) != Double
					.doubleToLongBits(other.percentIdentity))
				return false;
			if (queryAlignment == null) {
				if (other.queryAlignment != null)
					return false;
			} else if (!queryAlignment.equals(other.queryAlignment))
				return false;
			if (subjectAlignment == null) {
				if (other.subjectAlignment != null)
					return false;
			} else if (!subjectAlignment.equals(other.subjectAlignment))
				return false;
			return true;
		}


		@Override
		public String toString() {
			return "SequenceAlignmentImpl [percentIdentity=" + percentIdentity
					+ ", alignmentLength=" + alignmentLength
					+ ", numMismatches=" + numMismatches + ", numGap=" + numGap
					+ ", queryAlignment=" + queryAlignment
					+ ", subjectAlignment=" + subjectAlignment + "]";
		}
		
		
	}
}
