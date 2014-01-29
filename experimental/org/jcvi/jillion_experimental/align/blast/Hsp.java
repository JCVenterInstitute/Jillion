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
package org.jcvi.jillion_experimental.align.blast;

import java.math.BigDecimal;
import java.util.Comparator;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;

/**
 * {@code Hsp} is an object representation of a 
 * "High-scoring Segment Pair" (HSP).  HSPs
 * are pairs of sequences whose alignments with one another
 * meet certain scoring and statistical criteria.
 * @author dkatzel
 *
 *
 */
public interface Hsp<R extends Residue, S extends ResidueSequence<R>> extends SequenceAlignment<R,S> {
	/**
	 * Get the Id of the Query sequence in this Hsp.
	 * @return a String; will never be null.
	 */
	String getQueryId();
	/**
	 * Get the Id of the Subject sequence in this Hsp.
	 * @return a String; will never be null.
	 */
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
     * The bitscore is a normalized raw score.
     * 
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
    
    public enum Comparators implements Comparator<Hsp<?,?>>{
    	/**
    	 * Sort by Bit score from
    	 * lowest (the worst) to highest (the best).
    	 */
        BIT_SCORE_WORST_TO_BEST{
            @Override
            public int compare(Hsp<?,?> o1, Hsp<?,?> o2) {
            	return o1.getBitScore().compareTo(o2.getBitScore());
            }
        },
        /**
    	 * Sort by Bit score from
    	 * the highest (the best)
    	 * to the lowest (the worst)
    	 */
        BIT_SCORE_BEST_TO_WORST{
            @Override
            public int compare(Hsp<?,?> o1, Hsp<?,?> o2) {
            	return o2.getBitScore().compareTo(o1.getBitScore());
            }
        },
        /**
    	 * Sort by e-value score from
    	 * lowest (the best) to highest (the worst).
    	 */
        
        E_VALUE_BEST_TO_WORST{
            @Override
            public int compare(Hsp<?,?> o1, Hsp<?,?> o2) {
            	return o1.getEvalue().compareTo(o2.getEvalue());
            }
        },
        /**
    	 * Sort by e-value score from
    	 * the highest (the worst)
    	 * to the lowest (the best)
    	 */
        E_VALUE_WORST_TO_BEST{
            @Override
            public int compare(Hsp<?,?> o1, Hsp<?,?> o2) {
            	return o2.getEvalue().compareTo(o1.getEvalue());
            }
        },
        
        ;

        
        
        
    }
}
