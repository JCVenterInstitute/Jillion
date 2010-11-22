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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
        this.virtualIdToActualIdMap = new LinkedHashMap<String, String>();
        

        this.circular = circular;
        mapByStart = new LinkedHashMap<Long, List<VirtualPlacedRead<T>>>();
        mapById = new LinkedHashMap<String, VirtualPlacedRead<T>>();
        
        this.virtualReads = new LinkedHashSet<VirtualPlacedRead<T>>(virtualReads);
        this.realPlacedReads = new LinkedHashSet<T>();
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (circular ? 1231 : 1237);
		result = prime * result
				+ ((consensus == null) ? 0 : consensus.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + numberOfReads;
		result = prime * result
				+ ((realPlacedReads == null) ? 0 : realPlacedReads.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Contig)) {
			return false;
		}
		Contig other = (Contig) obj;
		if (circular != other.isCircular()) {
			return false;
		}
		if (consensus == null) {
			if (other.getConsensus()!= null) {
				return false;
			}
		} else if (!consensus.equals(other.getConsensus())) {
			return false;
		}
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		if (numberOfReads != other.getNumberOfReads()) {
			return false;
		}
		if (realPlacedReads == null) {
			if (other.getPlacedReads() != null) {
				return false;
			}
		} else if (!realPlacedReads.equals(other.getPlacedReads())) {
			return false;
		}
		return true;
	}
    
    
}
