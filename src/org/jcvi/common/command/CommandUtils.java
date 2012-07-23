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

package org.jcvi.common.command;

/**
 * @author dkatzel
 *
 *
 */
public final class CommandUtils {
	
	

    /** An array of characters that should be escaped in shell strings. */
    private static final char[] UNSAFE_CHARS = {'\\', '\''};
    
    private CommandUtils(){
		//private constructor
	}
    /**
     * Produces a quoted and escaped version of a string for .  This ensures the string will be
     * treated as a single element by shell command parsers, regardless of the inclusion of
     * various parse tokens such as quotation marks and spaces.
     *
     * @param value The value to format for shell usage.
     * @return A shell-safe string, with quotes and escpaed characters as needed.
     */
    public static String escape(String value)
    {
        String escaped = value;
        for (final char unsafeChar : UNSAFE_CHARS)
        {
            escaped = escaped.replace(String.valueOf(unsafeChar), "\\" + unsafeChar);
        }

        if (value.indexOf(' ') == -1){
            return escaped;
        }

        final StringBuilder quoted = new StringBuilder(value.length() + 2);
        quoted.append('\'');
        quoted.append(escaped);
        quoted.append('\'');

        return quoted.toString();
    }
    
    /**
     * Waits for {@link Process} to end.  This will block execution until the given
     * <code>Process</code> either completes successfully, fails, or the current thread is
     * interrupted.  This is basically a utility wrapper around {@link Process#waitFor()} that
     * is capable of handling any exceptions.
     * <p>
     * <em>Note:</em> In the case that the current thread is interrupted, the interrupt signal
     * is considered to be a request to force the <code>Process</code> to end as well.
     *
     * @param proc The <code>Process</code> to wait for.
     * @return The exit value of the <code>Process</code> or <code>-1</code> if the
     * <code>Process</code> was forcefully ended via interrupt.
     */
    public static int waitFor(Process proc)
    {
        try
        {
            final int result = proc.waitFor();
            return result;
        }catch (final InterruptedException e){

            //We've been interrupted by another local thread.  This is handled
            //as a cancel request.
            proc.destroy();
            return -1;
        }
    }

}
