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


import org.jcvi.trace.sanger.chromatogram.ztr.chunk.ChunkException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.*;
public class TestChunkFactory {

    InputStream inputStream = createMock(InputStream.class);
    @Test
    public void smp4() throws ChunkException{
        assertThat(Chunk.getChunk("SMP4"), is(Chunk.SMP4));
    }
    
    @Test
    public void base() throws ChunkException{
        assertThat(Chunk.getChunk("BASE"), is(Chunk.BASE));
    }
    
    @Test
    public void bpos() throws ChunkException{
        assertThat(Chunk.getChunk("BPOS"), is(Chunk.POSITIONS));
        
    }
    @Test
    public void cnf4() throws ChunkException{
        assertThat(Chunk.getChunk("CNF4"), is(Chunk.CONFIDENCES));
    }
    @Test
    public void text() throws ChunkException{
        assertThat(Chunk.getChunk("TEXT"), is(Chunk.COMMENTS));
    }
    @Test
    public void clip() throws ChunkException{
        assertThat(Chunk.getChunk("CLIP"), is(Chunk.CLIP));
    }
    
    @Test
    public void invalidChunkShouldThrowChunkException() {
        try {
            Chunk.getChunk("invalid");
            fail("should throw chunkException if unknown chunk encountered");
        } catch (ChunkException e) {
            assertEquals("header 'invalid' is unknown", e.getMessage());
        }
    }

}
