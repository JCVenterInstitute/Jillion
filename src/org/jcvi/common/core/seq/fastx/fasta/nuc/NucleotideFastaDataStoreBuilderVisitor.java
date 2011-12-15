package org.jcvi.common.core.seq.fastx.fasta.nuc;

import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * A {@code NucleotideFastaDataStoreBuilderVisitor} is a 
 * {@link NucleotideFastaDataStoreBuilder} that also implements
 * {@link FastaVisitor}.  This allows {@link NucleotideFastaDataStore}s
 * to be built by either manually adding records via
 * {@link #addFastaRecord(NucleotideSequenceFastaRecord)}
 * or by passing an instance of this class to {@link FastaParser}'s parse methods
 * to add all the fasta records from  a fasta file.  Some implementations
 * may be able to add fasta records from multiple fasta files.
 * @author dkatzel
 *
 */
public interface NucleotideFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideFastaDataStore>{

}
