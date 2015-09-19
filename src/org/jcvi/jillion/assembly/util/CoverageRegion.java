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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;


import java.util.stream.Stream;

import org.jcvi.jillion.core.Rangeable;
/**
 * A {@link CoverageRegion} is a contiguous 
 * portion of a {@link CoverageMap} which 
 * has exactly the same elements (and therefore
 * the same depth of coverage). If any elements
 * stop providing coverage or if new elements start
 * providing coverage, then there will be multiple
 * {@link CoverageRegion}s.
 * @author dkatzel
 *
 * @param <T> the type of {@link Rangeable} elements that make up
 * this coverage region.
 */
public interface CoverageRegion<T extends Rangeable> extends Rangeable, Iterable<T> {
    /**
     * Get the coverage depth of this coverage region.
     * which is the number of elements that make up 
     * this region.
     * @return an integer {@code >= 0}.
     */
    int getCoverageDepth();
    /**
     * Get the length of this coverage region.
     * Should return the same value as the length 
     * from {@code asRange().getLength()}.
     * @return the length; will always be >=0.
     */
    long getLength();
    
    
    Stream<T> streamElements();
}
