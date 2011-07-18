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
 * Created on Jan 27, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.pos.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.ShortGlyph;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;

public class DefaultPositionFastaFileDataStore extends AbstractPositionFastaFileDataStore{

private final Map<String, PositionFastaRecord<Sequence<ShortGlyph>>> map = new HashMap<String, PositionFastaRecord<Sequence<ShortGlyph>>>();
private DataStore<PositionFastaRecord<Sequence<ShortGlyph>>> datastore;
/**
 * @param fastaRecordFactory
 */
public DefaultPositionFastaFileDataStore(
        PositionFastaRecordFactory fastaRecordFactory) {
    super(fastaRecordFactory);
}

/**
 * Convenience constructor using the {@link DefaultPositionFastaFileDataStore}.
 * This call is the same as {@link #DefaultPositionFastaFileDataStore(QualityFastaRecordFactory)
 * new DefaultPositionFastaFileDataStore(DefaultPositionFastaRecordFactory.getInstance());}
 */
public DefaultPositionFastaFileDataStore() {
    super();
}

public DefaultPositionFastaFileDataStore(File fastaFile,PositionFastaRecordFactory fastaRecordFactory) throws FileNotFoundException {
    super(fastaRecordFactory);
    parseFastaFile(fastaFile);
}
public DefaultPositionFastaFileDataStore(File fastaFile) throws FileNotFoundException {
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
    datastore = new SimpleDataStore<PositionFastaRecord<Sequence<ShortGlyph>>>(map);
}
@Override
public boolean contains(String id) throws DataStoreException {
    return datastore.contains(id);
}
@Override
public PositionFastaRecord<Sequence<ShortGlyph>> get(String id)
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
public CloseableIterator<PositionFastaRecord<Sequence<ShortGlyph>>> iterator() {
    return datastore.iterator();
}
}
