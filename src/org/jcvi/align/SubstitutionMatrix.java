/**
 * SubstitutionMatrix.java Created: Nov 18, 2009 - 10:45:21 AM (jsitz) Copyright
 * 2009 J. Craig Venter Institute
 */
package org.jcvi.align;


/**
 * A <code>SubstitutionMatrix</code> is a matrix of possible sequence values used to provide
 * score values for the dynamic programming step in a Smith-Waterman alignment.
 *
 * @author jsitz@jcvi.org
 */
public interface SubstitutionMatrix<T> extends AlignmentMatrix<T>
{
    
}