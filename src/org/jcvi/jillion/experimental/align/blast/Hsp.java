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
package org.jcvi.jillion.experimental.align.blast;

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
	 * Get the defline of the Subject in this Hsp.
	 * @return a String; may be null if not specified.
	 */
    String getSubjectDefinition();
    
    Integer getQueryLength();
    
    Integer getSubjectLength();
    
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
    
    Float getHspScore();
    
   
    
    Integer getNumberOfIdentitcalMatches();
    
    Integer getNumberOfPositiveMatches();

    Integer getHitFrame();
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
