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

package org.jcvi.assembly.trim.lucy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.util.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class LucyDebugFileDataStore implements LucyDebugTrimRecordDataStore{

    final DataStore<LucyDebugTrimRecord> datastore;
    public LucyDebugFileDataStore ( File lucyDebugTrimFile) throws IOException{
        Map<String, LucyDebugTrimRecord> recordMap = new LinkedHashMap<String, LucyDebugTrimRecord>();
        BufferedReader reader = new BufferedReader(new FileReader(lucyDebugTrimFile));
        
        String line;
        while((line = reader.readLine()) !=null){
            LucyDebugTrimRecord record = createRecordFrom(line);
            recordMap.put(record.getId(), record);
        }
        datastore = new SimpleDataStore<LucyDebugTrimRecord>(recordMap);
        
    }
    /**
     * @param line
     * @return
     */
    private LucyDebugTrimRecord createRecordFrom(String line) {
        String[] elements = line.split("\\s+");
        String id = elements[0];
        Range clr = buildTrimRangeFrom(elements[2], elements[3]);
        Range clb = buildTrimRangeFrom(elements[5], elements[6]);
        Range clv = buildTrimRangeFrom(elements[14], elements[15]);
        return new LucyDebugTrimRecord(id, clr, clb, clv);
    }
    /**
     * @param string
     * @param string2
     * @return
     */
    private Range buildTrimRangeFrom(String left, String right) {
        if(left.equals("0") || right.equals("0")){
            return Range.buildEmptyRange(CoordinateSystem.RESIDUE_BASED, 1);
        }
        int start = left.equals("0")? Integer.MIN_VALUE: Integer.parseInt(left);
        int stop = right.equals("0")? Integer.MAX_VALUE: Integer.parseInt(right);
        
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED,
                start,
                stop);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public LucyDebugTrimRecord get(String id) throws DataStoreException {
        return datastore.get(id);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<LucyDebugTrimRecord> iterator() {
        return datastore.iterator();
    }
}
