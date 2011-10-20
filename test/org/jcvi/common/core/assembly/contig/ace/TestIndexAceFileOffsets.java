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

package org.jcvi.common.core.assembly.contig.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
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
    
    
    private static IndexedFileRange sut;
    
    @BeforeClass
    public static void setup() throws IOException{
        sut = new DefaultIndexedFileRange(8);
        File aceFile = resources.getFile("files/fluSample.ace");
        IndexedAceFileDataStore.create(aceFile, sut);
    }
    
    @Test
    public void firstContig(){
        assertEquals(Range.buildRange(21,45890), sut.getRangeFor("22934-PB2"));
    }
    
    @Test
    public void middleContigPB1(){
        assertEquals(Range.buildRange(45891,77407), sut.getRangeFor("22934-PB1"));
    }
    @Test
    public void middleContigPA(){
        assertEquals(Range.buildRange(77408,97284), sut.getRangeFor("22934-PA"));
    }
    @Test
    public void middleContigHA(){
        assertEquals(Range.buildRange(97285,148972), sut.getRangeFor("22934-HA"));
    }
    @Test
    public void middleContigNP(){
        assertEquals(Range.buildRange(148973,175349), sut.getRangeFor("22934-NP"));
    }
    @Test
    public void middleContigNA(){
        assertEquals(Range.buildRange(175350,226494), sut.getRangeFor("22934-NA"));
    }
    @Test
    public void middleContigMP(){
        assertEquals(Range.buildRange(226495,272700), sut.getRangeFor("22934-MP"));
    }
    @Test
    public void lastContigThatIsEndOfFile(){
        assertEquals(Range.buildRange(272701,313955), sut.getRangeFor("22934-NS"));
    }
    
    @Test
    public void aceFileThatHasTagsAtEnd() throws IOException, DataStoreException{
        File aceFile = resources.getFile("files/sample.ace");
        IndexedFileRange fileRange = new DefaultIndexedFileRange();
        AceContigDataStore datastore =IndexedAceFileDataStore.create(aceFile, fileRange);
        datastore.get("Contig1");
        assertEquals(Range.buildRange(8,14667), fileRange.getRangeFor("Contig1"));
    }
}
