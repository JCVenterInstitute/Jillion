package org.jcvi.jillion.sam;

import java.util.Objects;
import java.util.function.Supplier;

import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;

public class MultiSamRecordFilter implements SamRecordFilter{

	enum Operation{
		AND{
			@Override
			boolean operate(boolean a, Supplier<Boolean> b) {
				return a && b.get();
			}
		},
		OR{

			@Override
			boolean operate(boolean a, Supplier<Boolean> b) {
				return a || b.get();
			}
			
		},
		XOR{

			@Override
			boolean operate(boolean a, Supplier<Boolean> b) {
				return a ^ b.get();
			}
			
		}
		;
		
		abstract boolean operate(boolean a, Supplier<Boolean> b);
		
	}
	
	private final Operation op;
	private final SamRecordFilter a, b;
	
	
	public MultiSamRecordFilter(SamRecordFilter a, Operation op, SamRecordFilter b) {
		this.a = Objects.requireNonNull(a);
		this.op = Objects.requireNonNull(op);
		this.b = Objects.requireNonNull(b);
	}

	@Override
	public void begin() {
		a.begin();
		b.begin();
		
	}

	@Override
	public boolean filter(SamRecord record) {
		
		return op.operate(a.filter(record), ()-> b.filter(record));
	}

	@Override
	public void end() {
		a.end();
		b.end();
		
	}

	@Override
	public void ungappedReferenceDataStore(NucleotideFastaDataStore ungappedReferenceDataStore) {
		a.ungappedReferenceDataStore(ungappedReferenceDataStore);
		b.ungappedReferenceDataStore(ungappedReferenceDataStore);
		
	}
	
}
