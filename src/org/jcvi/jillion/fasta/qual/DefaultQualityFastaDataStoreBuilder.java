/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.qual;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultQualityFastaDataStoreBuilder} is a {@link QualitySequenceFastaDataStoreBuilder}
 * that stores all {@link QualitySequenceFastaRecord} added to it via the {@link #addFastaRecord(QualitySequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public class DefaultQualityFastaDataStoreBuilder implements QualitySequenceFastaDataStoreBuilder{

	private final Map<String, QualitySequenceFastaRecord> map = new LinkedHashMap<String, QualitySequenceFastaRecord>();
	@Override
	public QualitySequenceFastaDataStore build() {
		return DataStoreUtil.adapt(QualitySequenceFastaDataStore.class,map);
	}

	@Override
	public QualitySequenceFastaDataStoreBuilder addFastaRecord(
			QualitySequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
}
