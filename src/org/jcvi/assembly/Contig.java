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
