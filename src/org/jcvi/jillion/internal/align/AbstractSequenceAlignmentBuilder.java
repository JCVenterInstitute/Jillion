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
package org.jcvi.jillion.internal.align;

import java.util.Iterator;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
/**
 * {@code AbstractSequenceAlignmentBuilder} is 
 * an abstract implementation of {@link SequenceAlignmentBuilder}
 * that handles all the dirty work of building a valid
 * {@link SequenceAlignment}.  Subclasses only have
 * to implement a few simple helper methods
 * to return the correct types.
 * @author dkatzel
 *
 * @param <R> the {@link Residue} type used.
 * @param <S> the {@link ResidueSequence} type used.
 * @param <A> the {@link SequenceAlignment} used.
 * @param <B> the {@link ResidueSequenceBuilder} used.
 */
public abstract class AbstractSequenceAlignmentBuilder
		<R extends Residue, S extends ResidueSequence<R, S,B>, B extends ResidueSequenceBuilder<R, S>, A extends SequenceAlignment<R, S>> implements SequenceAlignmentBuilder<R, S,B,A>{

	private final B querySequenceBuilder, subjectSequenceBuilder;
	private int numMatches=0, numMisMatches=0;
	private int alignmentLength=0;
	private int numGaps=0;
	private Integer queryStart, subjectStart;
	private final boolean builtFromTraceback;
	
	private Integer subjectShiftAmount;
	/**
	 * Constructs a new instance of a {@link SequenceAlignmentBuilder}.
	 * @param builtFromTraceback this alignment
	 * will be built from data collected from some kind
	 * of traceback algorithm.  This will change the behavior of
	 * how the query and subject alignment ranges are computed.
	 * @see #setAlignmentOffsets(int, int)
	 * 
	 */
	public AbstractSequenceAlignmentBuilder(boolean builtFromTraceback){
		this(builtFromTraceback, null);
	}
	/**
         * Constructs a new instance of a {@link SequenceAlignmentBuilder}.
         * @param builtFromTraceback this alignment
         * will be built from data collected from some kind
         * of traceback algorithm.  This will change the behavior of
         * how the query and subject alignment ranges are computed.
         * @see #setAlignmentOffsets(int, int)
         * 
         */
        public AbstractSequenceAlignmentBuilder(boolean builtFromTraceback, Integer subjectShiftAmount){
                querySequenceBuilder = createSequenceBuilder();
                subjectSequenceBuilder = createSequenceBuilder();
                this.builtFromTraceback = builtFromTraceback;
                this.subjectShiftAmount = null;
                this.subjectShiftAmount = subjectShiftAmount;
        }
	public AbstractSequenceAlignmentBuilder(){
		this(false);		 
	}
	/**
	 * Create a new instance of a 
	 * {@link ResidueSequenceBuilder} of the correct
	 * type.
	 * @return a new {@link ResidueSequenceBuilder};
	 * can not be null.
	 */
	protected abstract B createSequenceBuilder();
	
	protected abstract A createAlignment(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				S queryAlignment, S subjectAlignment,
				Range queryRange, Range subjectRange);
	/**
	 * Parse the given string into the 
	 * correct type of {@link Residue}.
	 * @param sequence the sequence to parse; will never
	 * be null.
	 * @return a new {@link Iterable}; can not be null.
	 */
	protected abstract Iterable<R> parse(String sequence);
	/**
	 * Parse the given char into the 
	 * correct type of {@link Residue}.
	 * @param residue the residue to parse.
	 * @return a new {@link Iterable}; can not be null.
	 */
	protected abstract R parse(char residue);
	
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public SequenceAlignmentBuilder<R,S,B,A> setAlignmentOffsets(
			int queryOffset, int subjectOffset) {
		queryStart = queryOffset;
		subjectStart = subjectOffset;
		return this;
	}

	@Override
	public SequenceAlignmentBuilder<R, S,B,A> addMatches(String matchedSequence) {
		return addMatches(parse(matchedSequence));
	}
	
	
	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addMismatches(String query,
			String subject) {
		if(query.length() != subject.length()){
			throw new IllegalArgumentException(
					String.format("query and subject have different number of residues: %d vs %d", query.length(), subject.length()));
			
		}
		Iterator<R> queryBases =parse(query).iterator();
		Iterator<R> subjectBases =parse(subject).iterator();
		
		while(queryBases.hasNext()){
			R nextQuery = queryBases.next();
			R nextSubject = subjectBases.next();
			addMismatch(nextQuery, nextSubject);
		}
		
		return this;
	}
	@Override
	public A build() {
		final double percentIdentity;
		if(alignmentLength==0){
			percentIdentity =0D;
		}else{
			percentIdentity = ((double)numMatches)/alignmentLength;
		}
		final Range queryRange, subjectRange;
		if(queryStart ==null){
			queryStart=0;
		}
		if(subjectStart ==null){
			subjectStart=0;
		}
		long shiftedSubjectStart= subjectStart;
		if(subjectShiftAmount !=null){
		    shiftedSubjectStart += subjectShiftAmount;
		}
		if(builtFromTraceback){
			queryRange = Range.of(queryStart-querySequenceBuilder.getUngappedLength()+1, queryStart);
			subjectRange = Range.of(shiftedSubjectStart-subjectSequenceBuilder.getUngappedLength()+1, shiftedSubjectStart);
			//we built these sequence backwards
			//since they were built from a traceback
			//so reverse (but not complement) the sequences
			//to make them in the correct order.
			querySequenceBuilder.reverse();
			subjectSequenceBuilder.reverse();
		}else{
			queryRange = new Range.Builder(querySequenceBuilder.getUngappedLength()).shift(queryStart).build();
			subjectRange = new Range.Builder(subjectSequenceBuilder.getUngappedLength()).shift(shiftedSubjectStart).build();
		
		}
		return createAlignment(percentIdentity, alignmentLength, numMisMatches, numGaps, 
				querySequenceBuilder.build(), subjectSequenceBuilder.build(),
				queryRange, subjectRange);
	}

	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addMatch(R match) {
		numMatches++;		
		return appendToBuilders(match,match);
	}
	
	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addMatches(Iterable<R> matches) {
		for(R match : matches){
			addMatch(match);
		}
		return this;
	}

	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addMismatch(R query, R subject) {
		numMisMatches++;
		appendToBuilders(query, subject);
		return this;
	}

	private SequenceAlignmentBuilder<R,S,B,A> appendToBuilders(R query, R subject) {
		querySequenceBuilder.append(query);
		subjectSequenceBuilder.append(subject);
		alignmentLength++;
		return this;
	}

	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addGap(char query, char subject){
		return addGap(parse(query),
				parse(subject));
	}
			
	@Override
	public SequenceAlignmentBuilder<R,S,B,A> addGap(R query, R subject) {
		numGaps++;
		return appendToBuilders(query,subject);
	}

	
	protected abstract class AbstractSequenceAlignmentImpl implements SequenceAlignment<R, S>{

		private final double percentIdentity;
		private final int alignmentLength, numMismatches, numGap;
		private final S queryAlignment, subjectAlignment;
		private final DirectedRange queryRange, subjectRange;
		
		
		public AbstractSequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				S queryAlignment, S subjectAlignment,
				Range queryRange, Range subjectRange) {
			this.percentIdentity = percentIdentity;
			this.alignmentLength = alignmentLength;
			this.numMismatches = numMismatches;
			this.numGap = numGap;
			this.queryAlignment = queryAlignment;
			this.subjectAlignment = subjectAlignment;
			this.queryRange = DirectedRange.create(queryRange);
			this.subjectRange = DirectedRange.create(subjectRange);
		}

		@Override
		public DirectedRange getQueryRange() {
			return queryRange;
		}

		@Override
		public DirectedRange getSubjectRange() {
			return subjectRange;
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
					+ ((queryAlignment == null) ? 0 : queryAlignment.hashCode());
			result = prime * result
					+ ((queryRange == null) ? 0 : queryRange.hashCode());
			result = prime
					* result
					+ ((subjectAlignment == null) ? 0 : subjectAlignment
							.hashCode());
			result = prime * result
					+ ((subjectRange == null) ? 0 : subjectRange.hashCode());
			return result;
		}

		@Override
		public String toString() {
			return "AbstractSequenceAlignmentImpl [percentIdentity="
					+ percentIdentity + ", alignmentLength=" + alignmentLength
					+ ", numMismatches=" + numMismatches + ", numGap=" + numGap
					+ ", queryAlignment=" + queryAlignment
					+ ", subjectAlignment=" + subjectAlignment
					+ ", queryRange=" + queryRange + ", subjectRange="
					+ subjectRange + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (!(obj instanceof AbstractSequenceAlignmentBuilder.AbstractSequenceAlignmentImpl)){
				return false;
			}
			@SuppressWarnings("unchecked")
			AbstractSequenceAlignmentImpl other = (AbstractSequenceAlignmentImpl) obj;

			if (alignmentLength != other.alignmentLength){
				return false;
			}
			if (numGap != other.numGap){
				return false;
			}
			if (numMismatches != other.numMismatches){
				return false;
			}
			if (Double.doubleToLongBits(percentIdentity) != Double
					.doubleToLongBits(other.percentIdentity)){
				return false;
			}
			if (queryAlignment == null) {
				if (other.queryAlignment != null){
					return false;
				}
			} else if (!queryAlignment.equals(other.queryAlignment)){
				return false;
			}
			if (queryRange == null) {
				if (other.queryRange != null){
					return false;
				}
			} else if (!queryRange.equals(other.queryRange)){
				return false;
			}
			if (subjectAlignment == null) {
				if (other.subjectAlignment != null){
					return false;
				}
			} else if (!subjectAlignment.equals(other.subjectAlignment)){
				return false;
			}
			if (subjectRange == null) {
				if (other.subjectRange != null){
					return false;
				}
			} else if (!subjectRange.equals(other.subjectRange)){
				return false;
			}
			return true;
		}

		

		
		
	}
}
