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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.datastore.DataStore;
/**
 * {@code ContigDataStore} is a marker
 * interface for a {@link DataStore}
 * implementation which stores
 * {@link Contig}s of the same type.
 * @author dkatzel
 *
 *
 */
public interface ContigDataStore<PR extends AssembledRead,C extends Contig<PR>> extends DataStore<C> {
    
}
