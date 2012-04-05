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

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class DefaultFastqFileDataStore extends AbstractFastqFileDataStore{

   private DefaultFastqDataStore dataStore;
   private final DefaultFastqDataStore.Builder builder;
    /**
     * @param qualityCodec
     */
    public DefaultFastqFileDataStore(FastqQualityCodec qualityCodec, int numberOfRecords) {
        super(qualityCodec);
        builder = new DefaultFastqDataStore.Builder(numberOfRecords);
    }
    public DefaultFastqFileDataStore(FastqQualityCodec qualityCodec) {
        super(qualityCodec);        
        builder = new DefaultFastqDataStore.Builder();
    }
    public DefaultFastqFileDataStore(File fastQFile,FastqQualityCodec qualityCodec) throws IOException {
        super(qualityCodec);
        builder = new DefaultFastqDataStore.Builder();
        FastqFileParser.parse(fastQFile, this);
    }
    public DefaultFastqFileDataStore(File fastQFile,FastqQualityCodec qualityCodec, int numberOfRecords) throws IOException {
        super(qualityCodec);
        builder = new DefaultFastqDataStore.Builder(numberOfRecords);
        FastqFileParser.parse(fastQFile, this);
    }
    @Override
    protected FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord( String id, 
            NucleotideSequence nucleotides,
            QualitySequence qualities,
            String optionalComment) {
        builder.put(new DefaultFastqRecord(id, nucleotides, qualities, optionalComment));
        return FastXFileVisitor.EndOfBodyReturnCode.KEEP_PARSING;
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
    public FastqRecord get(String id) throws DataStoreException {
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
    public synchronized void close() throws IOException {
        dataStore.close();        
    }

    @Override
    public CloseableIterator<FastqRecord> iterator() {
        return dataStore.iterator();
    }
}
