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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.IOException;


import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public abstract class AbstractFastQFileDataStore<T extends FastQRecord> extends AbstractFastQFileVisitor implements FastQDataStore<T>{

    
    
    private boolean isClosed =false;
    private boolean initialized=false;

    public AbstractFastQFileDataStore(FastQQualityCodec qualityCodec){
        super(qualityCodec);
    }
    protected void checkNotYetInitialized(){
        if(initialized){
            throw new IllegalStateException("already initialized, can not visit more records");
        }
    }    
    public synchronized boolean isClosed() {
        return isClosed;
    }

    protected synchronized void throwExceptionIfClosed() throws DataStoreException{
        if(isClosed()){
            throw new DataStoreException("datastore is closed");
        }
    }
  

    @Override
    public void visitEndOfFile() {
        checkNotYetInitialized();
        super.visitEndOfFile();     
    }

    @Override
    public void visitLine(String line) {
        checkNotYetInitialized();
        super.visitLine(line);
    }
    
    @Override
    public void visitEncodedQualities(String encodedQualities) {
        checkNotYetInitialized();
        super.visitEncodedQualities(encodedQualities);
    }

    @Override
    public void visitFile() {       
        checkNotYetInitialized();
        super.visitFile();
    }
    @Override
    public CloseableIterator<T> iterator() {
        try {
            throwExceptionIfClosed();
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not create iterator", e);
        }
        return new DataStoreIterator<T>(this);
    }

    @Override
    public synchronized void close() throws IOException {
        isClosed =true;        
    }

    

    @Override
    public FastXFileVisitor.DeflineReturnCode visitDefline(String id, String optionalComment) {
        checkNotYetInitialized();
        return super.visitDefline(id, optionalComment);
    }
    
    @Override
    public void visitNucleotides(NucleotideSequence nucleotides) {
        checkNotYetInitialized();
       super.visitNucleotides(nucleotides);
        
    }

   
   

    

    



}
