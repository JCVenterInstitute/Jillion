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

import org.jcvi.jillion.align.ProteinSequenceAlignment;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

public class ProteinSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<AminoAcid, ProteinSequence, ProteinSequenceBuilder, ProteinSequenceAlignment>{

    public ProteinSequenceAlignmentBuilder() {
        super();
    }

    public ProteinSequenceAlignmentBuilder(boolean builtFromTraceback) {
        super(builtFromTraceback);
    }

    public ProteinSequenceAlignmentBuilder(boolean builtFromTraceback,
            Integer subjectShiftAmount) {
        super(builtFromTraceback, subjectShiftAmount);
    }

    @Override
    protected ProteinSequenceBuilder createSequenceBuilder() {
        return new ProteinSequenceBuilder();
    }

    @Override
    protected ProteinSequenceAlignment createAlignment(double percentIdentity,
            int alignmentLength, int numMismatches, int numGap,
            ProteinSequence queryAlignment, ProteinSequence subjectAlignment,
            Range queryRange, Range subjectRange) {
        return new AminoAcidSequenceAlignmentImpl(percentIdentity,
                alignmentLength, numMismatches, numGap, queryAlignment,
                subjectAlignment, queryRange, subjectRange);
    }

    @Override
    protected Iterable<AminoAcid> parse(String sequence) {
        return new ProteinSequenceBuilder(sequence);
    }

    @Override
    public ProteinSequenceAlignmentBuilder addMatches(String matchedSequence) {
        super.addMatches(matchedSequence);
        return this;
    }

    @Override
    public ProteinSequenceAlignmentBuilder addMatch(AminoAcid match) {
        super.addMatch(match);
        return this;
    }

    @Override
    public ProteinSequenceAlignmentBuilder addMatches(
            Iterable<AminoAcid> matches) {
        super.addMatches(matches);
        return this;
    }

    @Override
    public ProteinSequenceAlignmentBuilder addMismatch(AminoAcid query,
            AminoAcid subject) {
        super.addMismatch(query, subject);
        return this;
    }

    @Override
    public ProteinSequenceAlignmentBuilder addGap(AminoAcid query,
            AminoAcid subject) {
        super.addGap(query, subject);
        return this;
    }

    @Override
    public ProteinSequenceAlignmentBuilder addGap(char query, char subject) {
        super.addGap(query, subject);
        return this;
    }

    @Override
    protected AminoAcid parse(char aa) {
        return AminoAcid.parse(aa);
    }

    private final class AminoAcidSequenceAlignmentImpl extends
            AbstractSequenceAlignmentImpl implements ProteinSequenceAlignment {

        public AminoAcidSequenceAlignmentImpl(double percentIdentity,
                int alignmentLength, int numMismatches, int numGap,
                ProteinSequence queryAlignment,
                ProteinSequence subjectAlignment, Range queryRange,
                Range subjectRange) {
            super(percentIdentity, alignmentLength, numMismatches, numGap,
                    queryAlignment, subjectAlignment, queryRange, subjectRange);
        }

    }

}
