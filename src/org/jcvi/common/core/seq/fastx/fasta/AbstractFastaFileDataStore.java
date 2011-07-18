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

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
/**
 * {@code AbstractFastaFileDataStore} is a {@link DataStore} implementation
 * of FastaRecords parsed from a Fasta file.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaFileDataStore<T extends FastaRecord> implements FastaVisitor, DataStore<T>{

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
    
    
}
