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
/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;



final class DefaultCasScoringScheme implements CasScoringScheme{

    private final CasAlignmentScore alignmentScore;
    private final CasScoreType scoreType;
    private final CasAlignmentType alignmentType;
    
    public DefaultCasScoringScheme(CasScoreType scoreType,
            CasAlignmentScore alignmentScore, CasAlignmentType alignmentType) {
        this.scoreType = scoreType;
        this.alignmentScore = alignmentScore;
        this.alignmentType = alignmentType;
    }

    @Override
    public CasAlignmentScore getAlignmentScore() {
        return alignmentScore;
    }

    @Override
    public CasAlignmentType getAlignmentType() {
        return alignmentType;
    }

    @Override
    public CasScoreType getScoreType() {
        return scoreType;
    }

    @Override
    public String toString() {
        return "DefaultCasScoringScheme [scoreType=" + scoreType
                + ", alignmentScore=" + alignmentScore + ", alignmentType="
                + alignmentType + "]";
    }

}
