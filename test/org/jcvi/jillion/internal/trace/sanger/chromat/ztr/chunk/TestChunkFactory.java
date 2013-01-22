/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.sanger.chromat.ztr.chunk;

import java.io.InputStream;


import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.chunk.ChunkException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestChunkFactory {

    InputStream inputStream = createMock(InputStream.class);
    @Test
    public void smp4() throws ChunkException{
        assertSame(Chunk.getChunk("SMP4"),Chunk.SMP4);
    }
    
    @Test
    public void base() throws ChunkException{
        assertSame(Chunk.getChunk("BASE"), Chunk.BASE);
    }
    
    @Test
    public void bpos() throws ChunkException{
        assertSame(Chunk.getChunk("BPOS"), Chunk.POSITIONS);
        
    }
    @Test
    public void cnf4() throws ChunkException{
        assertSame(Chunk.getChunk("CNF4"), Chunk.CONFIDENCES);
    }
    @Test
    public void text() throws ChunkException{
        assertSame(Chunk.getChunk("TEXT"),Chunk.COMMENTS);
    }
    @Test
    public void clip() throws ChunkException{
        assertSame(Chunk.getChunk("CLIP"),Chunk.CLIP);
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
