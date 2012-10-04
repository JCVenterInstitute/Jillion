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

package org.jcvi.common.core.assembly.tasm;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.assembly.tasm.DefaultTigrAssemblerFileContigDataStore;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerContig;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.io.fileServer.FileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestTigrAssemblerContigDataStore {

    private static final FileServer RESOURCES = new ResourceFileServer(TestTigrAssemblerContigDataStore.class);
    
    private static final ContigDataStore<AssembledRead, Contig<AssembledRead>> contigDataStore;
    private static final DefaultTigrAssemblerFileContigDataStore tasmDataStore;
    static{
        try {
            contigDataStore= DefaultContigFileDataStore.create(RESOURCES.getFile("files/giv-15050.contig"));
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

    private void assertContigDataMatches(Contig<AssembledRead> contig, TigrAssemblerContig tasm){
        assertEquals("consensus",contig.getConsensusSequence(), tasm.getConsensusSequence());
        assertEquals("#reads",contig.getNumberOfReads(), tasm.getNumberOfReads());
        
        TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);

    }
}
