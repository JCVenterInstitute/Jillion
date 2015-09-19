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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.datastore.DataStore;
/**
 * {@code QualitySequenceDataStore} is a marker
 * interface for a {@link DataStore}
 * of {@link QualitySequence}s.
 * @author dkatzel
 *
 */
public interface QualitySequenceDataStore extends DataStore<QualitySequence> {

}
