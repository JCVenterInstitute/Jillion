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
package org.jcvi.common.core.seq.trace.sanger.chromat.scf;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
/**
 * <code>SCFParserException</code> is a subclass of
 * {@link TraceDecoderException} which is used if an SCF
 * file fails to parse.
 * @author dkatzel
 *
 *
 */
public class SCFDecoderException extends TraceDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = -8636660736340790019L;

    /**
     * @param message
     * @param cause
     */
    public SCFDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SCFDecoderException(String message) {
        super(message);
    }

}