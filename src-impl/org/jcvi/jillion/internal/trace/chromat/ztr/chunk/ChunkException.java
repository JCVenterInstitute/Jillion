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
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import org.jcvi.jillion.trace.TraceDecoderException;
/**
 * <code>ChunkException</code> is the Exception that should
 * be thrown whenver a problem encoding/decoding a {@link Chunk}
 * occurs.
 * @author dkatzel
 *
 *
 */
@SuppressWarnings("serial")
public class ChunkException extends TraceDecoderException {

    /**
     * Constructor.
     * @param message the error message
     */
    public ChunkException(String message) {
        super(message);
    }
    /**
     * Constructor.
     * @param message the error message
     * @param cause the cause of the error.
     */
    public ChunkException(String message, Throwable cause) {
        super(message, cause);
    }


}
