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
package org.jcvi.common.core.assembly;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public abstract class AbstractContig<T extends AssembledRead> implements Contig<T>{
    private NucleotideSequence consensus;
    private String id;
    private Map<String, T> mapById;
    protected AbstractContig(String id, NucleotideSequence consensus, Set<T> assembledReads){
    	if(id==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(consensus==null){
    		throw new NullPointerException("consensus can not be null");
    	}
        this.id = id;
        this.consensus = consensus;
        mapById = new LinkedHashMap<String, T>(assembledReads.size()+1, 1F);
        for(T r : assembledReads){
            mapById.put(r.getId(), r);
        }
       
    }

    @Override
    public NucleotideSequence getConsensus() {
        return consensus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getNumberOfReads() {
        return mapById.size();
    }
    @Override
    public T getRead(String id) {
        return mapById.get(id);
    }
    @Override
    public CloseableIterator<T> getReadIterator() {       
        return CloseableIteratorAdapter.adapt(mapById.values().iterator());
    }

    


    @Override
    public boolean containsRead(String placedReadId) {
        return mapById.containsKey(placedReadId);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ consensus.hashCode();
		result = prime * result +  id.hashCode();
		result = prime * result +  mapById.hashCode();
		return result;
	}

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
		Contig<?> other = (Contig<?>) obj;
		
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!consensus.equals(other.getConsensus())) {
			return false;
		}
		if (getNumberOfReads()!=other.getNumberOfReads()) {
			return false;
		}
		for(Entry<String, T> entry : mapById.entrySet()){
			String readId = entry.getKey();
			if(!other.containsRead(readId)){
				return false;
			}
			if(!entry.getValue().equals(other.getRead(readId))){
				return false;
			}
		}
		
		return true;
	}

	
    
    
}
