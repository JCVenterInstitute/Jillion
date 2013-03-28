/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
