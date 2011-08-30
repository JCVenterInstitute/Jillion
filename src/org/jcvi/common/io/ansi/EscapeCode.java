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

package org.jcvi.common.io.ansi;

/**
 * {@code EscapeCode} is a 
 * class that encapsulates the 
 * ANSI escape code for a particular
 * formatting instruction.
 * @author dkatzel
 *
 *
 */
final class EscapeCode {

    /** The ANSI control index. */
    private byte code;
    /** The full ANSI control string for this attribute. */
    private String controlCode;
    
    /**
     * Creates a new <code>ANSIColor</code>.
     * 
     * @param code The ANSI graphics mode index.
     */
    EscapeCode(int code)
    {
        this.code = (byte)code;
        this.controlCode = AnsiUtil.generateControlCodeFor(code);
    }

    /**
     * @return the code
     */
    public byte getCode() {
        return code;
    }

    /**
     * @return the controlCode
     */
    public String getControlCode() {
        return controlCode;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return controlCode;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EscapeCode)) {
            return false;
        }
        EscapeCode other = (EscapeCode) obj;
        if (code != other.code) {
            return false;
        }
        return true;
    }
    
    
}
