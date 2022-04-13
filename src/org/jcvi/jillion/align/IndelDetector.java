package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;

import lombok.Data;
import lombok.NonNull;

public interface IndelDetector<R extends ResidueSequence> {

	enum IndelType{
		INSERTION,
		DELETION
	}
	@Data
	public static class Indel implements Comparable<Indel>{
		@NonNull
		private final IndelType type;
		@NonNull
		private final Range location;
		@Override
		public int compareTo(Indel o) {
			return Range.Comparators.ARRIVAL.compare(location, o.location);
		} 
	}
	
	 List<Indel> findIndels(R subject, R query);
}
