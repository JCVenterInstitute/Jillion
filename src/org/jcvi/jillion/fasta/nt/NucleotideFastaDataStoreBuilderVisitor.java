package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;

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
interface NucleotideFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>{

}
