/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

import org.jcvi.assembly.cas.alignment.CasAlignmentType;


public class DefaultCasScoringScheme implements CasScoringScheme{

    private final CasAlignmentScore alignmentScore;
    private final CasScoreType scoreType;
    private final CasAlignmentType alignmentType;
    
    /**
     * @param scoreType
     * @param alignmentScore
     * @param alignmentType
     */
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
