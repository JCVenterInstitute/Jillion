/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.align;

import org.jcvi.jillion.align.NucleotideSequenceAlignment;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class NucleotideSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder, NucleotideSequenceAlignment>{

    public NucleotideSequenceAlignmentBuilder() {
        super();
    }

    public NucleotideSequenceAlignmentBuilder(boolean builtFromTraceback) {
        super(builtFromTraceback);
    }

    public NucleotideSequenceAlignmentBuilder(boolean builtFromTraceback,
            Integer subjectShiftAmount) {
        super(builtFromTraceback, subjectShiftAmount, null);
    }

    @Override
    protected NucleotideSequenceBuilder createSequenceBuilder() {
        return new NucleotideSequenceBuilder();
    }

    @Override
    protected Nucleotide parse(char base) {
        return Nucleotide.parse(base);
    }

    @Override
    public NucleotideSequenceAlignmentBuilder addMatch(Nucleotide match) {
        super.addMatch(match);
        return this;
    }

    @Override
    public NucleotideSequenceAlignmentBuilder addMatches(
            Iterable<Nucleotide> matches) {
        super.addMatches(matches);
        return this;
    }

    @Override
    public NucleotideSequenceAlignmentBuilder addMismatch(Nucleotide query,
            Nucleotide subject) {
        super.addMismatch(query, subject);
        return this;
    }

    @Override
    public NucleotideSequenceAlignmentBuilder addGap(Nucleotide query,
            Nucleotide subject) {
        super.addGap(query, subject);
        return this;
    }

    private final class NucleotideSequenceAlignmentImpl
            extends AbstractSequenceAlignmentImpl
            implements NucleotideSequenceAlignment {

        public NucleotideSequenceAlignmentImpl(double percentIdentity,
                int alignmentLength, int numMismatches, int numGap,
                NucleotideSequence queryAlignment,
                NucleotideSequence subjectAlignment, Range queryRange,
                Range subjectRange) {
            super(percentIdentity, alignmentLength, numMismatches, numGap,
                    queryAlignment, subjectAlignment, queryRange, subjectRange);
        }

    }

    @Override
    protected NucleotideSequenceAlignment createAlignment(
            double percentIdentity, int alignmentLength, int numMismatches,
            int numGap, NucleotideSequence queryAlignment,
            NucleotideSequence subjectAlignment, Range queryRange,
            Range subjectRange) {
        return new NucleotideSequenceAlignmentImpl(percentIdentity,
                alignmentLength, numMismatches, numGap, queryAlignment,
                subjectAlignment, queryRange, subjectRange);
    }

    @Override
    protected Iterable<Nucleotide> parse(String sequence) {
        return new NucleotideSequenceBuilder(sequence).build();
    }
}
