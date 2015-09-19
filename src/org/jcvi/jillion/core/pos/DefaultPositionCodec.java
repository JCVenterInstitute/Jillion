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
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.pos;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.GlyphCodec;


enum DefaultPositionCodec implements GlyphCodec<Position>{

   
    INSTANCE;
   
   

    @Override
    public Position decode(byte[] encodedGlyphs, long index) {
        int indexIntoShortAray = (int)(index*2);
        final int hi = encodedGlyphs[indexIntoShortAray]<<8;
        final byte low = encodedGlyphs[indexIntoShortAray+1];
        int value = hi | (low & 0xFF);
        return Position.valueOf(IOUtil.toUnsignedShort((short)value));
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length/2;
    }

    public byte[] encode(Collection<Position> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size()*2);
        for(Position g : glyphs){
            buf.putShort(IOUtil.toSignedShort(g.getValue()));
        }
        return buf.array();
    }

}
