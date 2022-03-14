package org.jcvi.jillion.core.residue.nt;

import java.util.Objects;

public interface PolymorphismComputationOption {
	public boolean include(NucleotideSequence ref, NucleotideSequence mapped);
	
	public boolean include(Nucleotide ref, Nucleotide mapped);
	
	public default PolymorphismComputationOption combine(PolymorphismComputationOption other) {
		Objects.requireNonNull(other);
		return new PolymorphismComputationOption() {

			@Override
			public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
				return this.include(ref, mapped) && other.include(ref, mapped);
			}

			@Override
			public boolean include(Nucleotide ref, Nucleotide mapped) {
				return this.include(ref, mapped) && other.include(ref, mapped);
			}
			
		};
	}
	public default PolymorphismComputationOption combine(PolymorphismComputationOption... others) {
		for(PolymorphismComputationOption o : others) {
			Objects.requireNonNull(o);
		}
		PolymorphismComputationOption orig = this;
		return new PolymorphismComputationOption() {

			@Override
			public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
				if( !orig.include(ref, mapped)) {
					return false;
				}
				for(PolymorphismComputationOption o : others) {
					if( !o.include(ref, mapped)) {
						return false;
					}
				}
				return true;            
			}

			@Override
			public boolean include(Nucleotide ref, Nucleotide mapped) {
				if( !orig.include(ref, mapped)) {
					return false;
				}
				for(PolymorphismComputationOption o : others) {
					if( !o.include(ref, mapped)) {
						return false;
					}
				}
				return true;
			}
			
		};
	}
}