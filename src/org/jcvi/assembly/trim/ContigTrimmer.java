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

package org.jcvi.assembly.trim;

import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;


/**
 * {@code ContigTrimmer} is a way to perform complex trimming
 * operations against a Contig to build a new trimmed version of
 * that Contig with the consensus and/or reads trimmed.
 * @author dkatzel
 * @param <P> the type of PlacedRead
 * @param <C> the type of Contig which has placedReads of type P
 *
 */
public interface ContigTrimmer<P extends PlacedRead, C extends Contig<P>> {
    
    /**
     * Trim the given contig which has the given coverage map.  
     * @param contig the contig to trim.
     * @return a trimmed version of the contig, the returned object may 
     * be the given contig input if no trimming occurred, a new Contig object
     * if any trimming did occur, or {@code null} if the entire contig
     * was trimmed off so there are no longer any underlying reads.
     * @throws TrimmerException if there is a problem during trimming.
     */
     C trimContig(C contig) throws TrimmerException;
}
