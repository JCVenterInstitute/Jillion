package org.jcvi.jillion.internal.fasta.nt;

import java.util.Map;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.fasta.AdaptedFastaDataStore;

public class AdaptedNucleotideFastaDataStore extends AdaptedFastaDataStore<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaDataStore{

	public AdaptedNucleotideFastaDataStore(Map<String, NucleotideFastaRecord> map) {
		super(map);
	}

	

}
