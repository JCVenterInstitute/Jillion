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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.common.core.datastore.DataStoreException;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestContigFileDataStore extends TestContigFileParser{

    @Test
    public void thereAre4Contigs() throws DataStoreException, IOException{
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(getFile());
        assertEquals(4, dataStore.size());
    }
    @Override
    protected Contig getContig925From(File file) throws FileNotFoundException {
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(file);
        return getContig(dataStore, "925");
    }
    @Override
    protected Contig getContig928From(File file) throws Exception{
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(file);
        return getContig(dataStore, "928");
    }
    private Contig getContig(
            ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore, String id) {
        try {
            return dataStore.get(id);
        } catch (DataStoreException e) {
            e.printStackTrace();
            throw new RuntimeException("error getting contig "+id,e);
        }
    }
    protected abstract ContigDataStore<PlacedRead, Contig<PlacedRead>> buildContigFileDataStore(
            File file) throws FileNotFoundException;

}
