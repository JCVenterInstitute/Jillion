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

import org.jcvi.jillion.core.util.Builder;

public class CasAlignmentScoreBuilder implements Builder<CasAlignmentScore>{
    private int firstInsertion, insertionExtension,
    firstDeletion, deletionExtension,
    match,
    transition, transversion,unknown;
    
    private Integer colorspaceError;
    
    public CasAlignmentScoreBuilder firstInsertion(int firstInsertion){
        this.firstInsertion = firstInsertion;
        return this;
    }
    
    public CasAlignmentScoreBuilder insertionExtension(int insertionExtension){
        this.insertionExtension = insertionExtension;
        return this;
    }
    
    public CasAlignmentScoreBuilder firstDeletion(int firstDeletion){
        this.firstDeletion = firstDeletion;
        return this;
    }
    public CasAlignmentScoreBuilder deletionExtension(int deletionExtension){
        this.deletionExtension = deletionExtension;
        return this;
    }
    public CasAlignmentScoreBuilder match(int match){
        this.match = match;
        return this;
    }
    public CasAlignmentScoreBuilder transition(int transition){
        this.transition = transition;
        return this;
    }
    public CasAlignmentScoreBuilder transversion(int transversion){
        this.transversion = transversion;
        return this;
    }
    public CasAlignmentScoreBuilder unknown(int unknown){
        this.unknown = unknown;
        return this;
    }
    public CasAlignmentScoreBuilder colorSpaceError(int colorspaceError){
        this.colorspaceError = colorspaceError;
        return this;
    }
    
    
    @Override
    public CasAlignmentScore build() {
        CasAlignmentScore score = new DefaultCasAlignmentScore(
                                    firstInsertion, insertionExtension, 
                                    firstDeletion, deletionExtension, 
                                    match, 
                                    transition, transversion, 
                                    unknown);
        if(colorspaceError !=null){
            return new DefaultCasColorSpaceAlignmentScore(score, colorspaceError);
        }
        return score;
    }

}
