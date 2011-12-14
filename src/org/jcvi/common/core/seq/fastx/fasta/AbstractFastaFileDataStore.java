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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
/**
 * {@code AbstractFastaFileDataStore} is a {@link DataStore} implementation
 * of FastaRecords parsed from a Fasta file.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaFileDataStore<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>> implements FastaVisitor, DataStore<F>{

    private boolean closed =false;
    
    protected synchronized void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    @Override
    public void close() throws IOException {
        closed=true;
        
    }

    @Override
    public boolean isClosed() throws DataStoreException {
        return closed;
    }

    @Override
    public void visitLine(String line) {
        
    }

    @Override
    public void visitEndOfFile() {
        
    }

    @Override
    public void visitFile() {
        
    }

    @Override
    public boolean visitBodyLine(String bodyLine) {
        return true;
    }

    @Override
    public boolean visitDefline(String defline) {
        return true;
    }
    
    protected abstract class AbstractFastaFileDataStoreBuilderVisitor<D extends DataStore<F>> implements FastaDataStoreBuilder<S,T,F,D>, FastaVisitor{
    	private final Map<String, F> map = new LinkedHashMap<String, F>();
		@Override
		public D build() {
			return createDataStore(map);
		}
		/**
		 * Create a {@link DataStore} of the correct type using the given
		 * map as input.
		 * @param map the input mappings of ids to fastaRecords;
		 * will never be null but could be empty.
		 * @return a new DataStore instance; can not be null.
		 */
		protected abstract D createDataStore(Map<String, F> map);
		/**
		 * no-op.
		 */
		@Override
		public void visitLine(String line) {
			//no-op
		}
		/**
		 * no-op.
		 */
		@Override
		public void visitFile() {
			//no-op
			
		}
		/**
		 * no-op.
		 */
		@Override
		public void visitEndOfFile() {
			//no-op			
		}
		/**
		 * no-op.
		 * @return true.
		 */
		@Override
		public boolean visitDefline(String defline) {
			return true;
		}
		/**
		 * no-op.
		 * @return true.
		 */
		@Override
		public boolean visitBodyLine(String bodyLine) {
			return true;
		}
		
		@Override
		public boolean visitRecord(String id, String comment, String entireBody) {
			addFastaRecord(createFastaRecord(id, comment, entireBody));
			return true;
		}
		
		protected abstract F createFastaRecord(String id, String comment, String entireBody);

		@Override
		public FastaDataStoreBuilder<S, T, F, D> addFastaRecord(F fastaRecord) {
			if(fastaRecord ==null){
				throw new NullPointerException("fasta record can not be null");
			}
			map.put(fastaRecord.getId(), fastaRecord);
			return this;
		}
    	
    }
}
