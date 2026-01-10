package org.jcvi.jillion.align;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

public final class DefaultIndelDetector<R extends Residue<R>> implements IndelDetector<R> {


	public static <R extends Residue<R>> IndelDetector<R> create(){
		return new DefaultIndelDetector<>();
	}

	@Override
	public <S1 extends ResidueSequence<R, S1,B1>, B1 extends ResidueSequenceBuilder<R, S1, B1>, S2 extends ResidueSequence<R, S2,B2>, B2 extends ResidueSequenceBuilder<R, S2, B2>> List<Indel>  findIndels(S1 subject, S2 query) {
		
		List<Range> subjectRanges = subject.getRangesOfGaps();
		List<Range> queryRanges = query.getRangesOfGaps();
		//these are the gaps they have in common which should only happen if this
		//is part of a multiple sequence alignment and there's different sequence that causes both of these to insert a gap
		List<Range> union = Ranges.union(subjectRanges, queryRanges);
		if(union.isEmpty()) {
			//simple case nothing overlaps
			List<Indel> list = new ArrayList<>(subjectRanges.size() + queryRanges.size());
			for(Range r : subjectRanges) {
				list.add(new Indel(IndelType.INSERTION, r));
			}
			for(Range r : queryRanges) {
				list.add(new Indel(IndelType.DELETION, r));
			}
			list.sort(null);
			return list;
		}
		// complex case
		List<Range> subjectRangesWithoutCommonGaps = Ranges.union(Ranges.createInclusiveRange(subjectRanges).complement(union), subjectRanges);
		List<Range> queryRangesWithoutCommonGaps = Ranges.union(Ranges.createInclusiveRange(queryRanges).complement(union), queryRanges);
																
		//TODO handle case where a common gap is inside an indel we don't want to lose that because it can cause a frame shift?
		
		List<Indel> list = new ArrayList<>(subjectRangesWithoutCommonGaps.size() + queryRangesWithoutCommonGaps.size());
		for(Range r : subjectRangesWithoutCommonGaps) {
			list.add(new Indel(IndelType.INSERTION, r));
		}
		for(Range r : queryRangesWithoutCommonGaps) {
			list.add(new Indel(IndelType.DELETION, r));
		}
		list.sort(null);
		return list;
	}

}
