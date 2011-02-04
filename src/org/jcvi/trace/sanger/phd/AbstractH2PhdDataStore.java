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

package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.CloseableIterator;

/**
 * {@code AbstractH2PhdDataStore} is a DataStore of EncodedGlyphs
 * stored in an {@link AbstractH2EncodedGlyphDataStore} containing
 * data from a Phd file.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractH2PhdDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements PhdFileVisitor, DataStore<E>{
    private final AbstractH2EncodedGlyphDataStore<G, E> h2Datastore;
    private StringBuilder currentBuilder;
    private String currentId;
    public <T extends AbstractH2EncodedGlyphDataStore<G, E> >AbstractH2PhdDataStore(File phdFile, T h2Datastore) throws FileNotFoundException{
        this.h2Datastore = h2Datastore;
        PhdParser.parsePhd(phdFile, this);
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

    @Override
    public void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        currentBuilder.append(visitSingleBaseCall(base,quality,tracePosition));
        
    }
    /**
     * Visit a single basecall and return the data to be encoded in the H2 datastore
     * as a string. the results from all the basecalls from each Phd record will be
     * concatenated together so be sure to include any whitespace necessary to
     * parse the entire catenated String correctly.
     * @param base
     * @param quality
     * @param tracePosition
     * @return
     */
    protected abstract String visitSingleBaseCall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition);

    @Override
    public void visitBeginDna() {
        // no -op
        
    }

    @Override
    public void visitBeginSequence(String id) {
        currentId = id;
        currentBuilder = new StringBuilder();
        
    }

    @Override
    public void visitBeginTag(String tagName) {
        
    }

    @Override
    public void visitComment(Properties comments) {
        
    }

    @Override
    public void visitEndDna() {
        
    }

    @Override
    public void visitEndSequence() {
        try {
            h2Datastore.insertRecord(currentId, currentBuilder.toString());
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not insert qualities into datastore",e);
        }
        
    }

    @Override
    public void visitEndTag() {
        
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
}
