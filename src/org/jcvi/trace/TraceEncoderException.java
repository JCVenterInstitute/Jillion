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

package org.jcvi.trace;

public class TraceEncoderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7796781674291231608L;

	public TraceEncoderException() {
	}

	public TraceEncoderException(String message) {
		super(message);
	}

	public TraceEncoderException(Throwable cause) {
		super(cause);
	}

	public TraceEncoderException(String message, Throwable cause) {
		super(message, cause);
	}

}
