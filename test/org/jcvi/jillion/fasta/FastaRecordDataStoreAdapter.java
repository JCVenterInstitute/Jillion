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
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code FastaRecordDataStoreAdapter} adapts a {@link DataStore} of {@link FastaRecord}s
 * into a {@link DataStore} of the value returned by {@link FastaRecord#getSequence()}.
 * @author dkatzel
 *
 *
 */
public final class FastaRecordDataStoreAdapter{

	private FastaRecordDataStoreAdapter(){
		//can not instantiate
	}
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <T> the values of the fastaRecord.
     * @param <F> a FastaRecord.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <S,T extends Sequence<S>,F extends FastaRecord<S,T>, D extends DataStore<T>> D adapt(Class<D> datastoreToMimic, DataStore<F> datastoreOfFastaRecords){
    	return DataStoreUtil.adapt(datastoreToMimic, datastoreOfFastaRecords,
    			FastaRecord::getSequence      	   
           );
    }
    
}
