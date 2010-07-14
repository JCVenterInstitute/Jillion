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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<Long, List<Long>> contig2ReadsMap;
    private int numberOfTotalReads;
    private int numberOfTotalContigs;
    @Override
    public synchronized void visitMetaData(long numberOfContigSequences,
            long numberOfReads) {
        super.visitMetaData(numberOfContigSequences, numberOfReads);
        numberOfTotalReads = (int)numberOfReads;
        numberOfTotalContigs = (int)numberOfContigSequences;
        read2ContigMap = new HashMap<Long, Long>(numberOfTotalReads);
        contig2ReadsMap = new HashMap<Long, List<Long>>(numberOfTotalContigs);
        for(long i=0; i< numberOfTotalContigs; i++){
          //initialize capacity to avg # of reads per contig
            //this should speed up performance a little
            contig2ReadsMap.put(i, new ArrayList<Long>(numberOfTotalReads/numberOfTotalContigs));
        }
    }

    @Override
    public synchronized void visitMatch(CasMatch match, long readCounter) {        
        if(match.matchReported()){
            CasAlignment alignment = match.getChosenAlignment();
            Long referenceId = alignment.contigSequenceId();
            Long readIndex = Long.valueOf(readCounter);
            read2ContigMap.put(readIndex, referenceId);           
            contig2ReadsMap.get(referenceId).add(readIndex);
        }
    }
    @Override
    public Long getContigIdForRead(long readIndex){
        return read2ContigMap.get(readIndex);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<Long> getReadIdsForContig(long contigId) {
        return contig2ReadsMap.get(contigId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfContigs() {
        return contig2ReadsMap.size();
    }
    
}
