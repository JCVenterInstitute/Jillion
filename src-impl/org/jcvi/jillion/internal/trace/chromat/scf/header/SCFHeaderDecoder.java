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
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import java.io.DataInputStream;

/**
 * <code>SCFHeaderDecoder</code> can parse the beginning of an SCF data stream
 * and create an {@link SCFHeader}.
 *
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderDecoder {
    /**
     * Parse the beginning of the SCF data contain in the {@link DataInputStream}
     * and create a {@link SCFHeader}.
     * @param in {@link DataInputStream} of the SCF data.
     * @return a populated {@link SCFHeader}.
     * @throws SCFHeaderDecoderException if there are any
     * problems parsing the {@link SCFHeader}.
     */
    SCFHeader decode(DataInputStream in) throws SCFHeaderDecoderException;
}
