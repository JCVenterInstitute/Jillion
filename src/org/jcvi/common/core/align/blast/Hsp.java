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

package org.jcvi.common.core.align.blast;

import java.math.BigDecimal;
import java.util.Comparator;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public interface Hsp extends SequenceAlignment<Nucleotide,NucleotideSequence> {
    String getQueryId();
    
    String getSubjectId();

    /**
     * Get the Expectation value of this hit.
     * The value provides a measure of statistical
     * significance.  An e-value is the probability
     * that this hit occurred by chance. The lower the e-value, the 
     * more similar the sequences and the more confidence that 
     * this hit is homologous to the query.
     * @return the e-value as a {@link BigDecimal}
     * never null.
     */
    BigDecimal getEvalue();
    /**
     * Get the Bit score for this hit.
     * @return
     */
    
    BigDecimal getBitScore();
    /**
     * Does this Hsp contain the actual
     * alignment information.  If {@code true}
     * then {@link #getGappedQueryAlignment()}
     * and {@link #getGappedSubjectAlignment()}
     * will return non-null objects.
     * @return {@code true} if this Hsp
     * does contain alignment information; {@code false}
     * otherwise.
     */
    boolean hasAlignments();
    
    public enum Comparators implements Comparator<Hsp>{
        BIT_SCORE{
            @Override
            public int compare(Hsp o1, Hsp o2) {
                int queryCmp = o1.getQueryId().compareTo(o2.getQueryId());
                if(queryCmp !=0){
                    return queryCmp;
                }
                int bitScoreCmp= o1.getBitScore().compareTo(o2.getBitScore());
                if(bitScoreCmp !=0){
                    return bitScoreCmp;
                }
                //bitScore should account for length so don't bother checking that
                return o1.getSubjectId().compareTo(o2.getSubjectId());
            }
        }
        ;

        
        
        
    }
}
