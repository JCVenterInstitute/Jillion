package org.jcvi.jillion.sam;

import java.util.Objects;
import java.util.function.Predicate;

public interface SamRecordFilter {
	void begin();
	boolean filter(SamRecord record);
	void end();
	Predicate<SamRecord> asPredicate();
	
	public static SamRecordFilter wrap(Predicate<SamRecord> filter) {
		Objects.requireNonNull(filter);
		return new WrappedPredicateFilter(filter);
	}
}