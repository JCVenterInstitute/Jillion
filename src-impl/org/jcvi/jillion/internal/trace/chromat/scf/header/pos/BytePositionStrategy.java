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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
