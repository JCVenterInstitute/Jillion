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
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import java.nio.ByteBuffer;
/**
 * <code>SCFHeaderEncoder</code> is used to encode
 * a {@link SCFHeader} into the format specified by the SCF File
 * Format.
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderEncoder {
    /**
     * Encodes the given {@link SCFHeader} into
     * the format specified by the SCF File Format.
     * @param header the header to encode.
     * @return a {@link ByteBuffer} containing
     * the SCF Header data encoded to the SCF
     * File Format Specification.
     */
    ByteBuffer encode(SCFHeader header);
}
