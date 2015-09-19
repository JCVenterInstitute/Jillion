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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;


import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.Trace;
/**
 * A {@code Flowgram} is a next-generation trace
 * created using pyrosequencing.
 * @author dkatzel
 * @see <a href="http://en.wikipedia.org/wiki/Pyrosequencing">Pyrosequencing Wikipedia Article</a>
 *
 */
public interface SffFlowgram extends Trace {
	/**
	 * The name of this flowgram.
	 * @return the id as a String; will never be null.
	 */
    String getId();
    
    /**
     * The quality clip points that
     * specify the subset of the basecalls
     * that are good quality.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getQualityClip();
    /**
     * The adapter clip points that
     * specify the subset of the basecalls
     * that are not adapter sequence.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getAdapterClip();
    /**
     * Get the number of flows that this
     * flowgram has.
     * @return an int >=0.
     */
    int getNumberOfFlows();
    /**
     * Get the flow value for the given flow index.  
     * The flow value is a float which is the estimated
     * size of a homopolymer run for the given flow index.
     * 
     * @param index the index into the flowgram
     * values to get.  Must be >=0 and < the number of flows.
     * @return the flow value as a float.
     * @throws ArrayIndexOutOfBoundsException if 
     * the index is outside the number of flows
     * specified by {@link #getNumberOfFlows()}.
     */
    float getCalledFlowValue(int index);
    
    /**
     * Get the raw  flow indexes in the array returned
     * by {@link #getRawEncodedFlowValues() }
     * @return a new byte array; never null
     */
    byte[] getRawIndexes();
    /**
     * Get the raw flowgram values which contains
     * the homopolymer stretch estimates for each flow of the read.
     * This is equivalent to the {@link SffReadData#getFlowgramValues()}.
     * @return a new short array; never null
     */
    short[] getRawEncodedFlowValues();
    
    /**
     * Compares this {@link SffFlowgram} with the specified Object for equality.
     * This method considers two {@link SffFlowgram} objects equal 
     * only if they are have equal id, basecalls, flowvalues, qualities and clip points.
     */
    @Override
    boolean equals(Object obj);
    
    /**
     * Returns the hash code for this {@link SffFlowgram}.
     * Hash code based on hashcodes for id, basecalls, flowvalues, qualities and clip points.
     */
    @Override
    public int hashCode();
}
