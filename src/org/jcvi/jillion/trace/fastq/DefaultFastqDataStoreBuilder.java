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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code DefaultFastqDataStoreBuilder} is a {@link Builder}
 * implementation that creates a new
 * {@link FastqDataStore} instance implemented 
 * with a {@link Map}
 * of {@link FastqRecord}s.
 * @author dkatzel
 *
 */
final class DefaultFastqDataStoreBuilder implements Builder<FastqDataStore>{
        private final Map<String, FastqRecord> map;
        
        public DefaultFastqDataStoreBuilder(){
            map = new LinkedHashMap<String, FastqRecord>();
        }
        public DefaultFastqDataStoreBuilder(int numberOfRecords){
            map = new LinkedHashMap<String, FastqRecord>(numberOfRecords);
        }
        public DefaultFastqDataStoreBuilder put(FastqRecord fastQRecord){
            map.put(fastQRecord.getId(), fastQRecord);
            return this;
        }
        public DefaultFastqDataStoreBuilder remove(FastqRecord fastQRecord){
            map.remove(fastQRecord.getId());
            return this;
        }
        public DefaultFastqDataStoreBuilder putAll(Collection<FastqRecord> fastQRecords){
            for(FastqRecord fastQRecord : fastQRecords){
                put(fastQRecord);
            }           
            return this;
        }
        
        public DefaultFastqDataStoreBuilder removeAll(Collection<FastqRecord> fastQRecords){
            for(FastqRecord fastQRecord : fastQRecords){
                remove(fastQRecord);
            }           
            return this;
        }
        @Override
        public FastqDataStore build() {
            return DataStoreUtil.adapt(FastqDataStore.class, map);
        }
}
