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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header.pos;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
/**
 * <code>BytePositionStrategy</code> is the implementation
 * of {@link PositionStrategy} that encodes positions
 * as a single byte.
 * @author dkatzel
 *
 *
 */
public class BytePositionStrategy implements PositionStrategy {

    @Override
    public short getPosition(DataInputStream in) throws IOException {
        return in.readByte();
    }
    /**
     * Max allowed value is the maximum value of a <code>byte</code>.
     * @return {@link Byte#MAX_VALUE}
     */
    @Override
    public int getMaxAllowedValue() {
        return Byte.MAX_VALUE;
    }

    @Override
    public void setPosition(short position, ByteBuffer buffer) {
        if(position > Byte.MAX_VALUE){
            throw new IllegalArgumentException("position to put is too big :"+ position);
        }
        buffer.put((byte)position);
    }

    @Override
    public byte getSampleSize() {
       return 1;
    }

}
