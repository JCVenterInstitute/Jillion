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

package org.jcvi.fastX.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.fastX.FastXFilter;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.Glyph;
import org.jcvi.util.CloseableIterator;

/**
 * {@code AbstractFastaH2DataStore} is an abstract implementation to the record
 * bodies (basecall or qual data) of FastaRecords inside an H2 data store.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaH2DataStore <G extends Glyph, E extends Sequence<G>> implements FastaVisitor, DataStore<E>{

    private final AbstractH2EncodedGlyphDataStore<G, E> h2Datastore;
    private final DataStoreFilter filter;
    public AbstractFastaH2DataStore(File fastaFile,AbstractH2EncodedGlyphDataStore<G, E> h2Datastore) throws FileNotFoundException{
        this(fastaFile, h2Datastore, EmptyDataStoreFilter.INSTANCE);
    }
    public AbstractFastaH2DataStore(File fastaFile,AbstractH2EncodedGlyphDataStore<G, E> h2Datastore, DataStoreFilter filter) throws FileNotFoundException{
        this.h2Datastore = h2Datastore;
        this.filter = filter;
        FastaParser.parseFasta(fastaFile, this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitBodyLine(String bodyLine) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitDefline(String defline) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitRecord(String id, String comment, String entireBody) {
        try{
            final boolean accept;
            if(filter instanceof FastXFilter){
                accept=((FastXFilter)filter).accept(id, comment);
            }else{
                accept = filter.accept(id);
            }
            if(accept){
                h2Datastore.insertRecord(id, entireBody);
            }
        }
        catch (DataStoreException e) {
            throw new IllegalStateException("could not insert record into datastore",e);
        }
        return true;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return h2Datastore.contains(id);
    }

    @Override
    public E get(String id) throws DataStoreException {
        return h2Datastore.get(id);
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return h2Datastore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return h2Datastore.size();
    }

    @Override
    public void close() throws IOException {
        h2Datastore.close();
        
    }
    

    @Override
    public boolean isClosed() throws DataStoreException {
        return h2Datastore.isClosed();
    }
    @Override
    public CloseableIterator<E> iterator() {
        return h2Datastore.iterator();
    }

}
