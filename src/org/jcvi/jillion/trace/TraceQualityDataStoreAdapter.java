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
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
/**
 * {@code TraceQualityDataStoreAdapter} adapts a {@link TraceDataStore} into
 * a {@link QualitySequenceDataStore} by delegating all the get() calls
 * to the wrapped datastore and then returned only the qualities from the desired trace.
 * @author dkatzel
 */
public final class TraceQualityDataStoreAdapter<T extends Trace>{
	
	private TraceQualityDataStoreAdapter(){
		//can not instantiate
	}
	/**
	 * Create a new {@link QualitySequenceDataStore} instance
	 * by adapting the given DataStore of traces.
	 * @param delegate the {@link DataStore} to adapt.
	 * @return a new {@link QualitySequenceDataStore} instance; never null.
	 * @throws NullPointerException if delegate is null.
	 */
    public static <T extends Trace> QualitySequenceDataStore adapt(DataStore<T> delegate){
    	return DataStore.adapt(QualitySequenceDataStore.class, delegate, from -> from.getQualitySequence());
    }
}
