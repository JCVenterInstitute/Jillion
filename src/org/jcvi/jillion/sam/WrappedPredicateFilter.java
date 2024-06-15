package org.jcvi.jillion.sam;

import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.residue.nt.INucleotideSequence;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.sam.header.SamHeader;

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

	@Override
	public void ungappedReferenceDataStore(DataStore<? extends INucleotideSequence<?,?>> ungappedReferenceDataStore) {
		//no-op
		
	}
	
}