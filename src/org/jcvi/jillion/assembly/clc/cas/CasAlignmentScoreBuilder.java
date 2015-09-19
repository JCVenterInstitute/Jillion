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
