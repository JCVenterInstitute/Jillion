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
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.trace.TraceDecoderException;
/**
 * <code>SCFParserException</code> is a subclass of
 * {@link TraceDecoderException} which is used if an SCF
 * file fails to parse.
 * @author dkatzel
 *
 *
 */
public class ScfDecoderException extends TraceDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = -8636660736340790019L;

    /**
     * @param message
     * @param cause
     */
    public ScfDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ScfDecoderException(String message) {
        super(message);
    }

}
