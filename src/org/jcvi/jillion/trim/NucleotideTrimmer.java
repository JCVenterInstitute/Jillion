package org.jcvi.jillion.trim;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface NucleotideTrimmer {

	Range trim(NucleotideSequence seq);
}
