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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.VirtualPlacedReadAdapter;
import org.jcvi.assembly.cas.CasIdLookup;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class AceContigAdapter implements AceContig{

    private final Contig<PlacedRead> delegate;
    private final Map<String, AcePlacedRead> adaptedReads = new HashMap<String, AcePlacedRead>();
    
    /**
     * @param delegate
     */
    public AceContigAdapter(Contig<PlacedRead> delegate, Date phdDate,CasIdLookup idLookup) {
        this.delegate = delegate;
        for(PlacedRead read : delegate.getPlacedReads()){
            final String readId = read.getId();
            adaptedReads.put(readId, new AcePlacedReadAdapter(read,
                    phdDate, 
                    idLookup.getFileFor(readId)));
        }
    }

    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return delegate.containsPlacedRead(placedReadId);
    }

    @Override
    public NucleotideEncodedGlyphs getConsensus() {
        return delegate.getConsensus();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    @Override
    public VirtualPlacedRead<AcePlacedRead> getPlacedReadById(String id) {
        return new VirtualPlacedReadAdapter<AcePlacedRead>(adaptedReads.get(id));
    }

    @Override
    public Set<AcePlacedRead> getPlacedReads() {
        return new HashSet<AcePlacedRead>(adaptedReads.values());
    }

    @Override
    public Set<VirtualPlacedRead<AcePlacedRead>> getVirtualPlacedReads() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCircular() {
        return delegate.isCircular();
    }

    
}
