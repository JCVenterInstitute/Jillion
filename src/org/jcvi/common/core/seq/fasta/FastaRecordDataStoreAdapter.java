/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;
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
    public static <S extends Symbol,T extends Sequence<S>,F extends FastaRecord<S,T>, D extends DataStore<T>> D adapt(Class<D> datastoreToMimic, DataStore<F> datastoreOfFastaRecords){
       return DataStoreUtil.adapt(datastoreToMimic, datastoreOfFastaRecords, new DataStoreUtil.AdapterCallback<F, T>() {

		@Override
		public T get(F from) {
			return from.getSequence();
		}
    	   
       });
    }
    
}
