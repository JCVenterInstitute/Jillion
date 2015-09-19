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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestBASEChunk {
   
    private static final String decodedBases = "ACGTACGTNW-";
   Chunk sut =Chunk.BASE;
   private static final byte[] encodedBases;
	static{
		ByteBuffer buf = ByteBuffer.allocate(decodedBases.length()+1);
	    buf.put((byte)0 ); //padding
	    buf.put(decodedBases.getBytes());
	    encodedBases = buf.array();
	}
	
    @Test
    public void valid() throws IOException{
       
        ZtrChromatogramBuilder builder = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedBases, builder);        
        assertEquals(decodedBases, builder.basecalls().toString());
    }
    
    @Test
    public void encode() throws IOException, IOException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	NucleotideSequence basecalls = new NucleotideSequenceBuilder(decodedBases).build();
    	expect(mockChromatogram.getNucleotideSequence()).andReturn(basecalls);
    	
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedBases, actual);
    	verify(mockChromatogram);
    }
}
