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

package org.jcvi.assembly.util;

import org.jcvi.Range;
import org.jcvi.io.TextFileVisitor;

/**
 * @author dkatzel
 *
 *
 */
public interface TrimFileVisitor extends TextFileVisitor{
    /**
     * Visit a Trim record.
     * @param id the ID of the record being trimmed.
     * @param trimRange the Range of the records clear
     * valid range.
     * @return {@code true} if the parser should
     * continue to visit records; {@code false} otherwise.
     */
    boolean visitTrim(String id, Range trimRange);
}
