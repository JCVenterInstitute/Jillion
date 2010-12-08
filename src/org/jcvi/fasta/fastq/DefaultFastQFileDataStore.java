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
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.CloseableIterator;

public class DefaultFastQFileDataStore extends AbstractFastQFileDataStore<FastQRecord>{

   private DefaultFastQDataStore<FastQRecord> dataStore;
   private final DefaultFastQDataStore.Builder<FastQRecord> builder = new DefaultFastQDataStore.Builder<FastQRecord>();
    /**
     * @param qualityCodec
     */
    public DefaultFastQFileDataStore(FastQQualityCodec qualityCodec) {
        super(qualityCodec);        
    }
    public DefaultFastQFileDataStore(File fastQFile,FastQQualityCodec qualityCodec) throws FileNotFoundException {
        super(qualityCodec);
        FastQFileParser.parse(fastQFile, this);
    }
    @Override
    protected void visitFastQRecord( String id, 
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities,
            String optionalComment) {
        builder.put(new DefaultFastQRecord(id, nucleotides, qualities, optionalComment));
    }

    
    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        dataStore = builder.build();
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public FastQRecord get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return dataStore.size();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();        
    }

    @Override
    public CloseableIterator<FastQRecord> iterator() {
        return dataStore.iterator();
    }
}
