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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.io.InputStream;

import org.jcvi.trace.sanger.chromatogram.ztr.chunk.BASEChunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.BPOSChunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.CLIPChunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.CNF4Chunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.ChunkException;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.ChunkFactory;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.SMP4Chunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.TEXTChunk;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestChunkFactory {

    InputStream inputStream = createMock(InputStream.class);
    @Test
    public void smp4() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("SMP4") instanceof SMP4Chunk);
    }
    
    @Test
    public void base() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("BASE") instanceof BASEChunk);
    }
    
    @Test
    public void bpos() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("BPOS") instanceof BPOSChunk);
    }
    @Test
    public void cnf4() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("CNF4") instanceof CNF4Chunk);
    }
    @Test
    public void text() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("TEXT") instanceof TEXTChunk);
    }
    @Test
    public void clip() throws ChunkException{
        assertTrue(ChunkFactory.getChunk("CLIP") instanceof CLIPChunk);
    }
    
    @Test
    public void invalidChunkShouldThrowChunkException() {
        try {
            ChunkFactory.getChunk("invalid");
            fail("should throw chunkException if unknown chunk encountered");
        } catch (ChunkException e) {
            assertEquals("Could not find Chunk type invalid", e.getMessage());
        }
    }

}
