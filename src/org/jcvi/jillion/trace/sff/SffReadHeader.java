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
/**
 * {@code SffReadHeader} contains
 * the information about the a specific
 * flowgram record from an sff encoded file.
 * @author dkatzel
 *
 *
 */
public interface SffReadHeader {
    /**
     * The number of bases called for this read.
     * @return a positive number.
     */
    int getNumberOfBases();
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
     * The name of this read.
     * @return a non-null String.
     */
    String getId();

}
