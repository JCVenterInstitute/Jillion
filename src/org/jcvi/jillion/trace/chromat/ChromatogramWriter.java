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
package org.jcvi.jillion.trace.chromat;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@code ChromatogramWriter} can write
 * {@link Chromatogram} objects to outputstreams.
 * Various implementations may encode the chromatogram writer
 * in various ways.
 * @author dkatzel
 *
 *
 */
public interface ChromatogramWriter {
    /**
     * Writes the given {@link Chromatogram}
     * to the given {@link OutputStream}.
     * @param chromatogram the {@link Chromatogram} to write.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the chromatogram
     * or any problems writing to the {@link OutputStream}.
     */
    void write(Chromatogram chromatogram, OutputStream out) throws IOException;
}