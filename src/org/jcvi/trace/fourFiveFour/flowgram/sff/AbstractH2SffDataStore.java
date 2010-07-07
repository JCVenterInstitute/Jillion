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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
/**
 * {@code AbstractH2SffDataStore} is an abstract implementation
 * of a {@link DataStore} of {@link EncodedGlyphs} from an {@link SffDataStore}.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractH2SffDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>, SffFileVisitor{

    private final AbstractH2EncodedGlyphDataStore<G, E> datastore;
    private final boolean trim;
    private SFFReadHeader currentReadHeader;
    private final DataStoreFilter filter;
    /**
     * Creates an {@link AbstractH2SffDataStore}.
     * @param sffFile The sff file to parse and extract information from.
     * @param datastore the {@link AbstractH2EncodedGlyphDataStore} where
     * the parsed data will be stored.
     * @param trim should only the trimmed data be stored.
     * @throws SFFDecoderException if there is a problem parsing the sff
     * file.
     * @throws FileNotFoundException if the sff file does not exist.
     * @throws NullPointerException if datastore is null.
     */
    public AbstractH2SffDataStore(File sffFile,AbstractH2EncodedGlyphDataStore<G, E> datastore, DataStoreFilter filter,boolean trim) throws SFFDecoderException, FileNotFoundException {
        if(datastore==null){
            throw new NullPointerException("AbstractH2EncodedGlyphDataStore can not be null");
        }
        if(filter ==null){
            throw new NullPointerException("DataStoreFilter can not be null");
        }
        this.datastore = datastore;
        this.trim = trim;
        this.filter = filter;
        SffParser.parseSFF(sffFile, this);
    }
    /**
     * Convience constructor, same as {@link #AbstractH2SffDataStore(File, AbstractH2EncodedGlyphDataStore, DataStoreFilter, boolean)
     * new AbstractH2EncodedGlyphDataStore(sffFile,datastore,EmptyDataStoreFilter.INSTANCE,false)}.
     * @param sffFile The sff file to parse and extract information from.
     * @param datastore the {@link AbstractH2EncodedGlyphDataStore} where
     * the parsed data will be stored.
     * @throws SFFDecoderException if there is a problem parsing the sff
     * file.
     * @throws FileNotFoundException if the sff file does not exist.
     * @throws NullPointerException if datastore is null.
     * @see #AbstractH2SffDataStore(File, AbstractH2EncodedGlyphDataStore, DataStoreFilter,boolean)
     */
    public AbstractH2SffDataStore(File sffFile,AbstractH2EncodedGlyphDataStore<G, E> datastore,boolean trim) throws SFFDecoderException, FileNotFoundException {
        this(sffFile, datastore,EmptyDataStoreFilter.INSTANCE, trim);
    }
    /**
     * Convience constructor, same as {@link #AbstractH2SffDataStore(File, AbstractH2EncodedGlyphDataStore, boolean)
     * new AbstractH2EncodedGlyphDataStore(sffFile,datastore,false)}.
     * @param sffFile The sff file to parse and extract information from.
     * @param datastore the {@link AbstractH2EncodedGlyphDataStore} where
     * the parsed data will be stored.
     * @throws SFFDecoderException if there is a problem parsing the sff
     * file.
     * @throws FileNotFoundException if the sff file does not exist.
     * @throws NullPointerException if datastore is null.
     * @see #AbstractH2SffDataStore(File, AbstractH2EncodedGlyphDataStore, boolean)
     */
    public AbstractH2SffDataStore(File sffFile,AbstractH2EncodedGlyphDataStore<G, E> datastore) throws SFFDecoderException, FileNotFoundException {
        this(sffFile, datastore, false);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public E get(String id) throws DataStoreException {
        return datastore.get(id);
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }
    @Override
    public Iterator<E> iterator() {
        return datastore.iterator();
    }
    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        return true;
    }
    protected abstract String getDataRecord(SFFReadHeader readHeader,SFFReadData readData, boolean shouldTrim);
    
    /**
     * inserts the given {@link SFFReadData} into the wrapped
     * {@link AbstractH2EncodedGlyphDataStore}.
     * @see AbstractH2EncodedGlyphDataStore#insertRecord(String, String)
     */
    @Override
    public boolean visitReadData(SFFReadData readData) {
        
        try {
            datastore.insertRecord(currentReadHeader.getName(), getDataRecord(currentReadHeader,readData,trim));
        } catch (DataStoreException e) {
            throw new IllegalStateException("Could not insert read into datastore",e);
        }
        return true;
    }
    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        currentReadHeader = readHeader;
        return filter.accept(readHeader.getName());
    }
    @Override
    public void visitEndOfFile() {
        
    }
    @Override
    public void visitFile() {
        
    }
    
}
