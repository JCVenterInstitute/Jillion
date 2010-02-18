/*
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.List;
import java.util.Set;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public interface Contig<T extends PlacedRead>{
    String getId();
    int getNumberOfReads();
    Set<T> getPlacedReads();
    NucleotideEncodedGlyphs getConsensus();
    VirtualPlacedRead<T> getPlacedReadById(String id);
    boolean containsPlacedRead(String placedReadId);
    Contig<T> without(List<T> reads);
    boolean isCircular();
    
    Set<VirtualPlacedRead<T>> getVirtualPlacedReads();
}
