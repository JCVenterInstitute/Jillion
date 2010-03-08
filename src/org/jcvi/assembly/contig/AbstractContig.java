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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class AbstractContig<T extends PlacedRead> implements Contig<T>{
    private NucleotideEncodedGlyphs consensus;
    private String id;
    private Map<Long, List<VirtualPlacedRead<T>>> mapByStart;
    private Map<String, VirtualPlacedRead<T>> mapById;
    private final Map<String, String> virtualIdToActualIdMap;
    private final int numberOfReads;
    private final boolean circular;
    private final Set<VirtualPlacedRead<T>> virtualReads;
    private final Set<T> realPlacedReads;
    protected AbstractContig(String id, NucleotideEncodedGlyphs consensus, Set<VirtualPlacedRead<T>> virtualReads,boolean circular){
        this.id = id;
        this.consensus = consensus;
        this.virtualIdToActualIdMap = new HashMap<String, String>();
        

        this.circular = circular;
        mapByStart = new HashMap<Long, List<VirtualPlacedRead<T>>>();
        mapById = new HashMap<String, VirtualPlacedRead<T>>();
        
        this.virtualReads = new HashSet<VirtualPlacedRead<T>>(virtualReads);
        this.realPlacedReads = new HashSet<T>();
        for(VirtualPlacedRead<T> r : virtualReads){
            addVirtualReadToStartMap(r);
            mapById.put(r.getId(), r);
            virtualIdToActualIdMap.put(r.getId(), r.getRealPlacedRead().getId());
            realPlacedReads.add(r.getRealPlacedRead());
        }
        this.numberOfReads = realPlacedReads.size();
    }

    private void addVirtualReadToStartMap(VirtualPlacedRead<T> r) {
        final Long start = Long.valueOf(r.getStart());
        if(!mapByStart.containsKey(start)){
            mapByStart.put(start, new ArrayList<VirtualPlacedRead<T>>());
        }
        mapByStart.get(start).add(r);
    }


    @Override
    public Set<VirtualPlacedRead<T>> getVirtualPlacedReads() {
        return virtualReads;
    }

    @Override
    public boolean isCircular() {
        return circular;
    }
    @Override
    public NucleotideEncodedGlyphs getConsensus() {
        return consensus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getNumberOfReads() {
        return numberOfReads;
    }
    @Override
    public VirtualPlacedRead<T> getPlacedReadById(String id) {
        return mapById.get(id);
    }
    @Override
    public Set<T> getPlacedReads() {       
        return realPlacedReads;
    }

    


    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return mapById.containsKey(placedReadId);
    }
    
    
}
