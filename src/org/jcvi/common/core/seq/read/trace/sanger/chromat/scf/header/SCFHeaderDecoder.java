/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header;

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
