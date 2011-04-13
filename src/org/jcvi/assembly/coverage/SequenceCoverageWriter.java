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
 * Created on May 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;

public class SequenceCoverageWriter <T extends PlacedRead> implements Closeable{
    private final MultiplePngCoverageWriter<T> coverageMapWriter;
    
    public SequenceCoverageWriter(File fileToWrite, String id){
        coverageMapWriter = new MultiplePngCoverageWriter<T>(fileToWrite,id);
    }

    public <C extends Contig<T>> void write(C contigToWrite){
            final NucleotideEncodedGlyphs consensus = contigToWrite.getConsensus();

            CoverageMap<CoverageRegion<T>> ungappedCoverageMap = AssemblyUtil.buildUngappedCoverageMap(contigToWrite);
        coverageMapWriter.add("total",ungappedCoverageMap);
        
        Map<SequenceDirection, List<T>> readmap = createReadMapByDirection(contigToWrite);
        addDirectionalCoverageMap(consensus, readmap.get(SequenceDirection.FORWARD), "forward");
        addDirectionalCoverageMap(consensus, readmap.get(SequenceDirection.REVERSE), "reverse");
        
    }

    private Map<SequenceDirection, List<T>> createReadMapByDirection(
            Contig<T> contigToWrite) {
        Map<SequenceDirection, List<T>> readmap = new EnumMap<SequenceDirection, List<T>>(SequenceDirection.class);
        for(SequenceDirection dir : SequenceDirection.values()){
            readmap.put(dir, new ArrayList<T>(contigToWrite.getNumberOfReads()/2));
        }
        for(T read :contigToWrite.getPlacedReads()){
            readmap.get(read.getSequenceDirection()).add(read);
        }
        return readmap;
    }

    private void addDirectionalCoverageMap(
            final NucleotideEncodedGlyphs consensus,
            List<T> reads, final String direction) {
        CoverageMap<CoverageRegion<T>> coverageMap = AssemblyUtil.buildUngappedCoverageMap(consensus, reads);
        coverageMapWriter.add(direction,coverageMap);
    }

    @Override
    public void close() throws IOException {
        coverageMapWriter.close();
        
    }

}
