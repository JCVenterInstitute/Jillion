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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.io.IOUtil;
/**
 * {@code DefaultQualityFastaFileDataStore} is the default implementation
 * of {@link AbstractQualityFastaFileDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeQualityFastaFileDataStore
 *
 */
public class DefaultQualityFastaFileDataStore extends AbstractQualityFastaFileDataStore{

    private final Map<String, QualityFastaRecord> map = new HashMap<String, QualityFastaRecord>();
    private DataStore<QualityFastaRecord> datastore;
    /**
     * @param fastaRecordFactory
     */
    public DefaultQualityFastaFileDataStore(
            QualityFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
    }
    /**
     * Convenience constructor using the {@link DefaultQualityFastaRecordFactory}.
     * This call is the same as {@link #DefaultQualityFastaFileDataStore(QualityFastaRecordFactory)
     * new DefaultQualityFastaFileDataStore(DefaultQualityFastaRecordFactory.getInstance());}
     */
    public DefaultQualityFastaFileDataStore() {
        super();
    }
    public DefaultQualityFastaFileDataStore(File fastaFile,QualityFastaRecordFactory fastaRecordFactory) throws FileNotFoundException {
        super(fastaRecordFactory);
        parseFastaFile(fastaFile);
    }
    public DefaultQualityFastaFileDataStore(File fastaFile) throws FileNotFoundException {
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
    public void visitRecord(String id, String comment, String recordBody) {
        map.put(id  , this.getFastaRecordFactory().createFastaRecord(id, comment,recordBody));
        
    }
    @Override
    public void close() throws IOException {
        map.clear();
        datastore.close();
    }
    
    
    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        datastore = new SimpleDataStore<QualityFastaRecord>(map);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public QualityFastaRecord get(String id)
            throws DataStoreException {
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
    public Iterator<QualityFastaRecord> iterator() {
        return datastore.iterator();
    }
    

}
