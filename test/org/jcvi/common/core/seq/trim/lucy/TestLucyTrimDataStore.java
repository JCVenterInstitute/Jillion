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

package org.jcvi.common.core.seq.trim.lucy;

import java.io.File;
import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.trim.lucy.LucySeqTrimDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestLucyTrimDataStore {

    ResourceFileServer RESOURCES = new ResourceFileServer(TestLucyTrimDataStore.class);
    
    @Test
    public void parseLucyTrimPoints() throws IOException, DataStoreException{
        File lucyTrimFile = RESOURCES.getFile("files/lucyTrimPoints.seq");
        LucySeqTrimDataStore sut = new LucySeqTrimDataStore(lucyTrimFile);
        assertEquals(127, sut.size());
        
        assertEquals("first record",
                Range.buildRange(0,139),
                sut.get("CVJHE01T00MANA01F") );
        assertEquals(Range.buildRange(313,688),
                sut.get("CVJHE01T00MAND08F") );        
        assertEquals("middle record",
                Range.buildRange(28,588),
                sut.get("CVJHE01T00MANH08R") );
        assertEquals("last record",
                Range.buildRange(51,868),
                sut.get("CVJHF01T00MANH07R") );
    }
}
