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
package org.jcvi.jillion.core.residue.nt;

import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;

public interface NucleotideSequenceDataStore extends DataStore<NucleotideSequence>{
	/**
	 * Creates a new {@link NucleotideSequenceDataStore} of the given Map of 
	 * name-sequence pairs.  
	 * @param map the map of names and their sequences to convert into a {@link NucleotideSequenceDataStore};
	 *  can not be null. The given map is copied so any future changes to the input map are NOT 
	 *  reflected in the datastore.
	 * @return a new NucleotideSequenceDataStore
	 * @since 6.0
	 */
	public static NucleotideSequenceDataStore of(Map<String, NucleotideSequence> map) {
		return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, map);
	}
}
