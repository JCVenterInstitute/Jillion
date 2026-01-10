package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;

import lombok.Data;
import lombok.NonNull;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

public interface IndelDetector<R extends Residue<R>> {

	enum IndelType{
		INSERTION,
		DELETION;

		public boolean isOpposite(IndelType type) {
			if(type !=null && type !=this) {
				return true;
			}
			return false;
		}
	}
	@Data
	public static class Indel implements Comparable<Indel>, Rangeable{
		@NonNull
		private final IndelType type;
		@NonNull
		private final Range location;
		@Override
		public int compareTo(Indel o) {
			return Range.Comparators.ARRIVAL.compare(location, o.location);
		}
		@Override
		public Range asRange() {
			return location;
		} 
	}

	<S1 extends ResidueSequence<R, S1,B1>, B1 extends ResidueSequenceBuilder<R, S1, B1>, S2 extends ResidueSequence<R, S2,B2>, B2 extends ResidueSequenceBuilder<R, S2, B2>> List<Indel> findIndels(S1 subject, S2 query);
}
