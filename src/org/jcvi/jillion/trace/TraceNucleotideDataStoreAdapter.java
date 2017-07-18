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
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
/**
 * {@code TraceNucleotideDataStoreAdapter} adapts a {@link TraceDataStore} into
 * a {@link NucleotideSequenceDataStore} by delegating all the get() calls
 * to the wrapped datastore and then returned only the {@link NucleotideSequence}
 *  from the desired trace.
 * @author dkatzel
 */
public final class TraceNucleotideDataStoreAdapter <T extends Trace> {
	
	private TraceNucleotideDataStoreAdapter(){
		//can not instantiate
	}
	/**
	 * Create a new {@link NucleotideSequenceDataStore} instance
	 * by adapting the given DataStore of traces.
	 * @param delegate the {@link DataStore} to adapt.
	 * @return a new {@link NucleotideSequenceDataStore} instance; never null.
	 * @throws NullPointerException if delegate is null.
	 */
	public static <T extends Trace> NucleotideSequenceDataStore adapt(DataStore<T> delegate){
        return DataStore.adapt(NucleotideSequenceDataStore.class, delegate, from ->from.getNucleotideSequence());
    }
}
