package org.jcvi.common.core.seq.fasta.nt;

import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaRecord} is an implementation
 * of {@link FastaRecord} whose sequences are {@link NucleotideSequence}s.
 * @author dkatzel
 *
 */
public interface NucleotideSequenceFastaRecord extends FastaRecord<Nucleotide,NucleotideSequence>{

}
