/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.align;

import java.util.List;

import org.jcvi.common.core.io.TextFileVisitor;

/**
 * {@code AlnVisitor} is a visitor
 * interface for visiting clustal .aln
 * multiple sequence alignment formatted
 * files.
 * @author dkatzel
 *
 *
 */
public interface AlnVisitor extends TextFileVisitor{
    /**
     * {@code ConservationInfo} contains information
     * about how each column (slice) in a group block
     * match.
     * @author dkatzel
     */
    public enum ConservationInfo{
        /**
         * The residues in the column are identical
         * in all sequences in the alignment.
         */
        IDENTICAL,
        /**
         * A conserved substitution has been
         * observed in this column.
         */
        CONSERVED_SUBSITUTION,
        /**
         * A semi-conserved substitution has been
         * observed in this column.
         */
        SEMI_CONSERVED_SUBSITUTION,
        /**
         * There is no conservation
         * in this column.  This could
         * mean that there are gaps
         * in the alignment at this column.
         */
        NOT_CONSERVED
        ;
    }
    void visitBeginGroup();
    
    void visitEndGroup();
    
    void visitAlignedSegment(String id, String gappedAlignment);
    
    void visitConservationInfo(List<ConservationInfo> conservationInfos);
}
