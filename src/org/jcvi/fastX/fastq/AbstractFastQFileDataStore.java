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
package org.jcvi.fastX.fastq;

import java.io.IOException;


import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.CloseableIterator;

public abstract class AbstractFastQFileDataStore<T extends FastQRecord> extends AbstractFastQFileVisitor<T> implements FastQDataStore<T>{

    private String currentId, currentComment;
    private EncodedGlyphs<PhredQuality> qualities;
    private NucleotideEncodedGlyphs nucleotides;
    protected final FastQQualityCodec qualityCodec;
    private boolean isClosed =false;
    public AbstractFastQFileDataStore(FastQQualityCodec qualityCodec){
        this.qualityCodec = qualityCodec;
    }
   
    public synchronized boolean isClosed() {
        return isClosed;
    }

    protected synchronized void throwExceptionIfClosed() throws DataStoreException{
        if(isClosed()){
            throw new DataStoreException("datastore is closed");
        }
    }
    protected FastQQualityCodec getQualityCodec() {
        return qualityCodec;
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
    public void visitFile() {       
        checkNotYetInitialized();
    }

    protected abstract boolean visitFastQRecord(
            String id, 
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities,
            String optionalComment);
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        checkNotYetInitialized();
        currentId = id;
        currentComment = optionalComment;
        return true;
    }

    @Override
    public void visitEncodedQualities(String encodedQualities) {
        checkNotYetInitialized();
        qualities = qualityCodec.decode(encodedQualities);  
    }

    @Override
    public boolean visitEndBlock() {
        return visitFastQRecord(currentId, nucleotides, qualities, currentComment);
        
        
    }

    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        checkNotYetInitialized();
        this.nucleotides = nucleotides;
        
    }



}
