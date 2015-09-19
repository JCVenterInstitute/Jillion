/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import java.io.InputStream;


import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.ChunkException;

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
