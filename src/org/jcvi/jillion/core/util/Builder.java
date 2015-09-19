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
 * Created on Jun 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;
/**
 * {@code Builder} is an interface for the 
 * Builder Pattern.
 * @param <T> the Type of object this Builder will build.
 * @author dkatzel
 *
 *
 */
public interface Builder<T> {
    /**
     * Create a new instance using the data collected
     * by this builder thus far.
     * @return a new instance of T.
     */
    T build();
}
