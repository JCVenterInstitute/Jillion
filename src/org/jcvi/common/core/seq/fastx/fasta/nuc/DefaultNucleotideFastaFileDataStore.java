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
package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code DefaultNucleotideFastaFileDataStore} is the default implementation
 * of {@link AbstractNucleotideFastaFileDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeNucleotideFastaFileDataStore
 *
 */
public class DefaultNucleotideFastaFileDataStore extends AbstractNucleotideFastaFileDataStore{
    private final Map<String, NucleotideSequenceFastaRecord> map = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
    private DataStore<NucleotideSequenceFastaRecord> datastore;
    /**
     * @param fastaRecordFactory
     */
    public DefaultNucleotideFastaFileDataStore(
            NucleotideFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
    }
    
    /**
     * Convenience constructor using the {@link DefaultNucleotideFastaRecordFactory}.
     * This call is the same as {@link #DefaultNucelotideFastaFileDataStore(QualityFastaRecordFactory)
     * new DefaultNucelotideFastaFileDataStore(DefaultNucleotideFastaRecordFactory.getInstance());}
     */
    public DefaultNucleotideFastaFileDataStore() {
        super();
    }

    public DefaultNucleotideFastaFileDataStore(File fastaFile,NucleotideFastaRecordFactory fastaRecordFactory) throws FileNotFoundException {
        super(fastaRecordFactory);
        parseFastaFile(fastaFile);
    }
    public DefaultNucleotideFastaFileDataStore(File fastaFile) throws FileNotFoundException {
        super();
        parseFastaFile(fastaFile);
    }
    private void parseFastaFile(File fastaFile) throws FileNotFoundException {
        InputStream in = new FileInputStream(fastaFile);
        try{
        FastaParser.parseFasta(in, this);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public boolean visitRecord(String id, String comment, String recordBody) {
        map.put(id  , this.getFastaRecordFactory().createFastaRecord(id, comment,recordBody));
        return true;
    }
    @Override
    public void close() throws IOException {
        super.close();
        map.clear();
        datastore.close();
    }
    
    
    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        datastore = new SimpleDataStore<NucleotideSequenceFastaRecord>(map);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public NucleotideSequenceFastaRecord get(String id)
            throws DataStoreException {
        return datastore.get(id);
    }
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    @Override
    public CloseableIterator<NucleotideSequenceFastaRecord> iterator() {
        return datastore.iterator();
    }
    

}
