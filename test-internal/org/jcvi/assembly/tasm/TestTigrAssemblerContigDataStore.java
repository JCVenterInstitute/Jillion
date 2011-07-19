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

package org.jcvi.assembly.tasm;

import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.io.fileServer.FileServer;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestTigrAssemblerContigDataStore {

    private static final FileServer RESOURCES = new ResourceFileServer(TestTigrAssemblerContigDataStore.class);
    
    private static final DefaultContigFileDataStore contigDataStore;
    private static final DefaultTigrAssemblerFileContigDataStore tasmDataStore;
    static{
        try {
            contigDataStore= new DefaultContigFileDataStore(RESOURCES.getFile("files/giv-15050.contig"));
        } catch (Exception e) {
            throw new IllegalStateException("could not parse contig file",e);
        } 
        try {
            tasmDataStore= new DefaultTigrAssemblerFileContigDataStore(RESOURCES.getFile("files/giv-15050.tasm"));
        } catch (Exception e) {
            throw new IllegalStateException("could not parse contig file",e);
        } 
    }
    
    @Test
    public void PB2() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15044"), tasmDataStore.get("1122071329926"));
    }
    @Test
    public void PB1() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15045"), tasmDataStore.get("1122071329927"));
    }
    @Test
    public void PA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15046"), tasmDataStore.get("1122071329928"));
    }
    @Test
    public void NP() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15047"), tasmDataStore.get("1122071329929"));
    }
    @Test
    public void MP() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15048"), tasmDataStore.get("1122071329930"));
    }
    @Test
    public void NS() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15057"), tasmDataStore.get("1122071329931"));
    }
    @Test
    public void HA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("26303"), tasmDataStore.get("1122071329932"));
    }
    @Test
    public void NA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("27233"), tasmDataStore.get("1122071329933"));
    }
    @Test
    public void lastContig() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("27235"), tasmDataStore.get("1122071329934"));
    }

    private void assertContigDataMatches(Contig<PlacedRead> contig, TigrAssemblerContig tasm){
        assertEquals("consensus",contig.getConsensus(), tasm.getConsensus());
        assertEquals("#reads",contig.getNumberOfReads(), tasm.getNumberOfReads());
        
        
        for(PlacedRead read : contig.getPlacedReads()){
            assertEquals(read, tasm.getPlacedReadById(read.getId()));
        }
    }
}
