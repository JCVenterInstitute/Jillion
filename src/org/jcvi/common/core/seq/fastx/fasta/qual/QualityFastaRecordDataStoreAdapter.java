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
package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public final class QualityFastaRecordDataStoreAdapter extends FastaRecordDataStoreAdapter<PhredQuality,QualitySequence,QualitySequenceFastaRecord> implements QualityDataStore{
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code QualityDataStore} which wraps the given datastore. 
     */
    public static  QualityDataStore adapt(DataStore<QualitySequenceFastaRecord> datastoreOfFastaRecords){
        return new QualityFastaRecordDataStoreAdapter(datastoreOfFastaRecords);
    }
    private QualityFastaRecordDataStoreAdapter(
            DataStore<QualitySequenceFastaRecord> datastoreOfFastaRecords) {
        super(datastoreOfFastaRecords);
    }

}
