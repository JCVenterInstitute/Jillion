package org.jcvi.jillion.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.core.Range;

@FunctionalInterface
public interface UnAdjustedCoordinateMapper{
	long unadjust(long newCoordinate);
	
	default Range unadjust(Range range) {
		return Range.of(unadjust(range.getBegin()), unadjust(range.getEnd()));
	}
	
	public default UnAdjustedCoordinateMapper andThen(UnAdjustedCoordinateMapper other) {
		return c-> {
			long tmp= unadjust(c);
			return other.unadjust(tmp);
		};
	}
	static UnAdjustedCoordinateMapper combine(List<UnAdjustedCoordinateMapper> mappers) {
		return c->{
			long current = c;
			for(UnAdjustedCoordinateMapper m : mappers) {
				current = m.unadjust(current);
			}
			return current;
		};
	}
	
	static UnAdjustedCoordinateMapper noOp() {
		return c-> c;
	}
	
	public static class Builder{
		private List<UnAdjustedCoordinateMapper> mappers = new ArrayList<>();
		private volatile boolean built=false;
		public Builder add(UnAdjustedCoordinateMapper mapper) {
			if(built) {
				throw new IllegalStateException("can not add to already built");
			}
			mappers.add(Objects.requireNonNull(mapper));
			return this;
		}
		
		public UnAdjustedCoordinateMapper build() {
			built=true;
			Collections.reverse(mappers);
			return UnAdjustedCoordinateMapper.combine(mappers);
		}

		public Builder copy() {
			Builder copy = new Builder();
			copy.mappers.addAll(this.mappers);
			return copy;
		}
	}
}