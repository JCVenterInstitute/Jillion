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

package org.jcvi.assembly.cas.read;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.assembly.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.assembly.cas.CasMatch;
import org.jcvi.assembly.cas.alignment.CasAlignment;

/**
 * {@code DefaultReadIndexToContigLookup} is the default
 * implementation of {@link ReadIndexToContigLookup}
 * which loads all the read to contig mapping data from a .cas file
 * into a Map object.
 * @author dkatzel
 *
 *
 */
public class DefaultCasFileReadIndexToContigLookup extends AbstractOnePassCasFileVisitor implements ReadIndexToContigLookup{

    private Map<Long, Long> read2ContigMap;
    
    @Override
    public synchronized void visitMetaData(long numberOfContigSequences,
            long numberOfReads) {
        super.visitMetaData(numberOfContigSequences, numberOfReads);
        read2ContigMap = new HashMap<Long, Long>((int)numberOfReads);
    }

    @Override
    public synchronized void visitMatch(CasMatch match, long readCounter) {        
        if(match.matchReported()){
            CasAlignment alignment = match.getChosenAlignment();
            long referenceId = alignment.contigSequenceId();
            read2ContigMap.put(readCounter, referenceId);
        }
    }
    @Override
    public Long getContigIdForRead(long readIndex){
        return read2ContigMap.get(readIndex);
    }
    
}
