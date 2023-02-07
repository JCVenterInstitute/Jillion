package org.jcvi.jillion.sam;

import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.sam.header.SamHeader;

public interface SamRecordFilter {
	void begin();
	boolean filter(SamRecord record);
	void end();
	
	default Predicate<SamRecord> asPredicate(){
		return this::filter;
	}
	
	public static SamRecordFilter wrap(Predicate<SamRecord> filter) {
		Objects.requireNonNull(filter);
		return new WrappedPredicateFilter(filter);
	}
	void ungappedReferenceDataStore(NucleotideFastaDataStore ungappedReferenceDataStore);
	
	default SamRecordFilter and(SamRecordFilter other) {
		return new MultiSamRecordFilter(this, MultiSamRecordFilter.Operation.AND, other);
	}
	default SamRecordFilter or(SamRecordFilter other) {
		return new MultiSamRecordFilter(this, MultiSamRecordFilter.Operation.OR, other);
	}
	default SamRecordFilter xor(SamRecordFilter other) {
		return new MultiSamRecordFilter(this, MultiSamRecordFilter.Operation.XOR, other);
	}
}