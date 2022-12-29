package org.jcvi.jillion.sam;

import java.util.function.Predicate;

final class WrappedPredicateFilter implements SamRecordFilter {
	private final Predicate<SamRecord> filter;

	WrappedPredicateFilter(Predicate<SamRecord> filter) {
		this.filter = filter;
	}

	@Override
	public void begin() {
		//no-op
	}

	@Override
	public boolean filter(SamRecord record) {
		return filter.test(record);
	}

	@Override
	public void end() {
		//no-op
		
	}

	@Override
	public Predicate<SamRecord> asPredicate() {
		return filter;
	}
	
}