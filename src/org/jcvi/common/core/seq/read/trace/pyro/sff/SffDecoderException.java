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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;

class SffDecoderException extends TraceDecoderException {

    /**
     * 
     */
    private static final long serialVersionUID = 5849079908602188978L;

    /**
     * @param message
     * @param cause
     */
    public SffDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SffDecoderException(String message) {
        super(message);
    }

}
