package org.jcvi.common.core.seq.fastx.fasta.nt;

import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

/**
 * A {@code NucleotideFastaDataStoreBuilderVisitor} is a 
 * {@link NucleotideSequenceFastaDataStoreBuilder} that also implements
 * {@link FastaFileVisitor}.  This allows {@link NucleotideSequenceFastaDataStore}s
 * to be built by either manually adding records via
 * {@link #addFastaRecord(DefaultNucleotideSequenceFastaRecord)}
 * or by passing an instance of this class to {@link FastaFileParser}'s parse methods
 * to add all the fasta records from  a fasta file.  Some implementations
 * may be able to add fasta records from multiple fasta files.
 * @author dkatzel
 *
 */
public interface NucleotideFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, DefaultNucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>{

}
