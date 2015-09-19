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
package org.jcvi.jillion.core.datastore;

import java.util.function.Predicate;

/**
 * {@code DataStoreFilter} is a filter that can be applied
 * to a DataStore to only allow certain Datastore ids.
 * @author dkatzel
 *
 *
 */
public interface DataStoreFilter extends Predicate<String>{
    /**
     * Is the given id accepted by the filter.
     * @param id the id to check.
     * @return {@code true} if the id should be accepted
     * by the filter {@code false} otherwise.
     */
    boolean accept(String id);
    /**
     * By default, delegate {@link Predicate#test(Object)}
     * to {@link #accept(String)}.
     * {@inheritDoc}
     */
    default boolean test(String id){
    	return accept(id);
    }
}
