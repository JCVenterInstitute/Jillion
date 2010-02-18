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

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;

public class SequenceCoverageWriter <T extends PlacedRead> implements Closeable{
    private final MultiplePngCoverageWriter<T> coverageMapWriter;
    
    public SequenceCoverageWriter(File fileToWrite, String id){
        coverageMapWriter = new MultiplePngCoverageWriter<T>(fileToWrite,id);
    }

    public <C extends Contig<T>> void write(C contigToWrite, CoverageMap<CoverageRegion<T>> sequenceCoverageMap){
            final NucleotideEncodedGlyphs consensus = contigToWrite.getConsensus();
            CoverageMap<CoverageRegion<T>> ungappedCoverageMap = new UngappedCoverageMap.Builder<T>(
                    sequenceCoverageMap,
                    consensus
                    ).build();
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
            List<T> reverseReads, final String direction) {
        coverageMapWriter.add(direction,new UngappedCoverageMap.Builder<T>(
        new DefaultCoverageMap.Builder<T>(reverseReads).build(),
        consensus
        ).build());
    }

    @Override
    public void close() throws IOException {
        coverageMapWriter.close();
        
    }

}
