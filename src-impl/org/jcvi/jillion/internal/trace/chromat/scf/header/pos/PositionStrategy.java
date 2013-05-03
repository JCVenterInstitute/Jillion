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
 * <code>PositionStrategy</code> hides the implementation
 * details of how the SCF file stores position information.
 * @author dkatzel
 *
 *
 */
public interface PositionStrategy {
    /**
     * Get the next position from the SCF data.
     * @param in {@link DataInputStream} of the SCF data.
     * @return a <code>short</code> which is the next position.
     * @throws IOException if there are any problems
     * fetching the next position.
     */
    short getPosition(DataInputStream in) throws IOException;
    /**
     * encode the given position into the given {@link ByteBuffer}.
     * @param position the position to encode.
     * @param buffer the ByteBuffer to write the encoded position.
     */
    void setPosition(short position,ByteBuffer buffer);
    /**
     * The maximum value a position is allowed to be for this
     * {@link PositionStrategy} implementation.
     * @return the max possible value a position can be.
     */
    int getMaxAllowedValue();
    /**
     * The number of bytes required for each encoded position.
     * @return a very small number, probably <code>1</code> or <code>2</code>.
     */
    byte getSampleSize();
}
