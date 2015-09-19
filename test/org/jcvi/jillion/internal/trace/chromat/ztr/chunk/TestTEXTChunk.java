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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestTEXTChunk {

    private static final byte NULL_TERMINATOR = 0;
    static Map<String,String> expected ;
    Chunk sut = Chunk.COMMENTS;
    
    private static final byte[] encodedBytes;
    static{
    	 expected = new LinkedHashMap<String,String>();
         expected.put("DATE", "Sun 09 Sep 20:29:52 2007 to Sun 09 Sep 21:48:16 2007");
         expected.put("SIGN", "A:5503,C:5140,G:3030,T:5266");
         expected.put("NAME", "TIGR_GBKAK82TF_980085_1106817232495");
         
    	ByteBuffer buf = ByteBuffer.allocate(134);
        buf.put((byte)0);
        for(Entry<String, String>  entry : expected.entrySet()){
            final String key = entry.getKey().toString();
            final String value = entry.getValue().toString();
            buf.put(key.getBytes());
            buf.put(NULL_TERMINATOR);
            buf.put(value.getBytes());
            buf.put(NULL_TERMINATOR);
        }
        buf.put(NULL_TERMINATOR);
        
        encodedBytes = buf.array();
    }
   
    
    @Test
    public void parse() throws IOException{
        
        ZtrChromatogramBuilder struct = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedBytes, struct);
        assertEquals(struct.comments(), expected);
    }

    	@Test
        public void encode() throws IOException{
        	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
        	expect(mockChromatogram.getComments()).andReturn(expected);
        	
        	replay(mockChromatogram);
        	byte[] actual =sut.encodeChunk(mockChromatogram);
        	assertArrayEquals(encodedBytes, actual);
        	verify(mockChromatogram);
    }
    
    
}
