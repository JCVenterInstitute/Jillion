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

package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestIndexAceFileOffsets {

    private static ResourceFileServer resources = new ResourceFileServer(TestIndexAceFileOffsets.class);
    private static IndexedAceFileDataStore sut;
    @BeforeClass
    public static void setup() throws IOException{
        File aceFile = resources.getFile("files/fluSample.ace");
        sut =(IndexedAceFileDataStore)IndexedAceFileDataStore.create(aceFile);
    }
    
    @Test
    public void firstContig(){
        assertEquals(Range.create(21,45890), sut.getIndexRangeFor("22934-PB2"));
    }
    
    @Test
    public void middleContigPB1(){
        assertEquals(Range.create(45891,77407), sut.getIndexRangeFor("22934-PB1"));
    }
    @Test
    public void middleContigPA(){
        assertEquals(Range.create(77408,97284), sut.getIndexRangeFor("22934-PA"));
    }
    @Test
    public void middleContigHA(){
        assertEquals(Range.create(97285,148972), sut.getIndexRangeFor("22934-HA"));
    }
    @Test
    public void middleContigNP(){
        assertEquals(Range.create(148973,175349), sut.getIndexRangeFor("22934-NP"));
    }
    @Test
    public void middleContigNA(){
        assertEquals(Range.create(175350,226494), sut.getIndexRangeFor("22934-NA"));
    }
    @Test
    public void middleContigMP(){
        assertEquals(Range.create(226495,272700), sut.getIndexRangeFor("22934-MP"));
    }
    @Test
    public void lastContigThatIsEndOfFile(){
        assertEquals(Range.create(272701,313955), sut.getIndexRangeFor("22934-NS"));
    }
    
    @Test
    public void aceFileThatHasTagsAtEnd() throws IOException, DataStoreException{
        File aceFile = resources.getFile("files/sample.ace");
        IndexedAceFileDataStore datastore =(IndexedAceFileDataStore) IndexedAceFileDataStore.create(aceFile);
        datastore.get("Contig1");
        assertEquals(Range.create(8,14667), datastore.getIndexRangeFor("Contig1"));
    }
}
