/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

import org.jcvi.assembly.cas.alignment.CasAlignmentType;


public interface CasScoringScheme {

    CasScoreType getScoreType();
    CasAlignmentType getAlignmentType();
    CasAlignmentScore getAlignmentScore();
}
