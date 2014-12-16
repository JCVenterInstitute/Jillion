/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceElement;
import org.jcvi.jillion.internal.assembly.util.ConsensusCompactedSlice;
import org.jcvi.jillion.internal.assembly.util.NoConsensusCompactedSlice;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;
/**
 * {@code SliceBuilder} is a {@link Builder}
 * object that builds a single {@link Slice}
 * instance which contains given
 * {@link SliceElement}s.
 * @author dkatzel
 *
 */
public final class SliceBuilder implements Builder<Slice>{

	
    private GrowableShortArray bytes = new GrowableShortArray(1024);
    private List<String> ids = new ArrayList<String>();
    private Nucleotide consensus;
    /**
     * {@code SliceElementFilter} is used to remove
     * {@link SliceElement}s <strong>currently</strong>
     * in the Slice being built.
     * Applying a filter on a SliceBuilder
     * will iterate over all the current
     * SliceElements in the SliceBuilder
     * and remove any elements that are
     * not accepted by the filter.
     * @author dkatzel
     *
     */
    public interface SliceElementFilter{
    	/**
    	 * Should the given SliceElement 
    	 * be included in the SliceBuilder.
    	 * @param e the current SliceElement to inspect;
    	 * will never be null.
    	 * @return {@code true} if this {@link SliceElement}
    	 * should be kept by the SliceBuilder;
    	 * {@code false} if the SliceElement should be removed.
    	 */
		boolean accept(SliceElement e);
	}
    /**
     * Create a new {@link SliceBuilder}
     * which will start off empty with a null consensus.
     */
    public SliceBuilder(){
    	//creates empty builder with null consensus
    }
    /**
     * Create a new {@link SliceBuilder}
     * which will start off empty with a null consensus.
     */
    public SliceBuilder(Nucleotide consensus){
    	setConsensus(consensus);
    }
    /**
     * Create a new {@link SliceBuilder}
     * which will start off containing
     * the same SliceElements as the given Slice.
     * Each SliceElement will be a deep
     * copy.
     * @param slice the {@link Slice} to copy;
     * can not be null.
     * @throws NullPointerException if slice is null.
     */
    public SliceBuilder(Slice slice){
    	if(slice ==null){
    		throw new NullPointerException("Slice can not be null");
    	}
    	addAll(slice);
    	setConsensus(slice.getConsensusCall());
    }
    /**
     * Create a new {@link SliceBuilder}
     * which will start off containing
     * the only the SliceElements from the given Slice
     * that are accepted by the SliceElementFilter.
     * This should be the same result
     * as 
     * {@code new SliceBuilder(slice).filter(filter)}
     * but may be implemented more efficiently.
     * @param slice the {@link Slice} to copy;
     * can not be null.
     * @param filter an instance of {@link SliceElementFilter}
     * to filter the input SliceElements to this builder.
     * @throws NullPointerException if slice is null.
     */
    public SliceBuilder(Slice slice, SliceElementFilter filter){
    	if(slice ==null){
    		throw new NullPointerException("Slice can not be null");
    	}
    	if(filter ==null){
    		throw new NullPointerException("filter can not be null");
    	}
		for(SliceElement e: slice){
			if(filter.accept(e)){
				add(e);
			}
    	}
    	setConsensus(slice.getConsensusCall());
    }
    /**
     * Filter the <strong>current</strong>
     * {@link SliceElement}s in this builder.
     * Any SliceElements not accepted by the filter
     * will be removed.
     * @param filter the {@link SliceElementFilter} instance
     * that will be used to filter the SliceElements;
     * can not be null.
     * @return this.
     * @throws NullPointerException if filter is null.
     */
    public SliceBuilder filter(SliceElementFilter filter){
    	if(filter==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	GrowableShortArray newBytes = new GrowableShortArray(bytes.getCurrentLength());
    	List<String> newIds = new ArrayList<String>(ids.size());
    	
    	for(int i=0; i<ids.size(); i++){
    		String id = ids.get(i);
    		short value =bytes.get(i);
    		if(filter.accept(CompactedSliceElement.create(id, value))){
    			newBytes.append(value);
    			newIds.add(id);
    		}
    	}
    	this.ids = newIds;
    	this.bytes = newBytes;
    	return this;
    }
    /**
     * Create a new {@link SliceBuilder}
     * which will start off given
     * SliceElements.
     * Each SliceElement will be a deep
     * copy.
     * @param elements the {@link SliceElement}s to copy;
     * can not be null and each SliceElement in the {@link Iterable}
     * can not be null.
     * @throws NullPointerException if slice is null.
     */
    public SliceBuilder(Iterable<? extends SliceElement> elements){
    	if(elements ==null){
    		throw new NullPointerException("Slice can not be null");
    	}
    	addAll(elements);
    }
    public int getCurrentCoverageDepth(){
    	return ids.size();
    }
    
    private SliceBuilder(SliceBuilder copy){
    	this.ids = new ArrayList<String>(copy.ids);
    	this.bytes = copy.bytes.copy();
    	this.consensus = copy.consensus;
    }
    
    public SliceBuilder setConsensus(Nucleotide consensus){
    	this.consensus = consensus;
    	return this;
    }
    
    /**
     * Add the given {@link SliceElement} to this builder.
     * Adding a SliceElement with the same id
     * as a pre-existing element already
     * present in this builder will cause the new
     * value to overwrite the existing value.
     * 
     * @param element the SliceElement to add;
     * can not be null.
     * @return this
     * @throws NullPointerException if element is null.
     */
    public SliceBuilder add(SliceElement element){  
    	if(element ==null){
    		throw new NullPointerException("SliceElement can not be nul");
    	}
        return add(element.getId(),element.getBase(), element.getQuality(), element.getDirection());
    }
    public SliceBuilder addAll(Iterable<? extends SliceElement> elements){
        for(SliceElement e : elements){
            add(e);
        }
        return this;
    }
    /**
     * Add a new SliceElement with the following values
     * to this builder.
     * Adding a SliceElement with the same id
     * as a pre-existing element already
     * present in this builder will cause the new
     * value to overwrite the existing value.
     * 
     * @param id the id of the SliceElement to add;
     * can not be null.
     * @param base the {@link Nucleotide} basecall of the SliceElement to add;
     * can not be null.
     * @param quality the {@link PhredQuality} of the SliceElement to add;
     * can not be null.
     * @param dir the {@link Direction} of the SliceElement to add;
     * can not be null.
     * @return this
     * @throws NullPointerException if any parameter is null.
     */
    public SliceBuilder add(String id, Nucleotide base, PhredQuality quality, Direction dir){
    	
    	CompactedSliceElement compacted = new CompactedSliceElement(id, base, quality, dir);
    	int value = compacted.getEncodedDirAndNucleotide() <<8;
    	value |= (compacted.getEncodedQuality() &0xFF);
    	
    	int index =ids.indexOf(id);
    	if(index == -1){
    		//append
    		bytes.append((short)value);            
            ids.add(id);    		
    	}else{    		
          //overwrite
    		bytes.replace(index, (short)value);
    		ids.remove(index);
    		ids.add(index, id);
    	}
    	
        
        return this;
    }

    /**
     * Does this builder contain
     * a SliceElement with the given id.
     * @param id the SliceElement id to find;
     * can not be null.
     * @return {@code true} if a SliceElement with this
     * id is present in the builder; 
     * {@code false} otherwise.
     * @throws NullPointerException if id is null.
     */
	public boolean containsId(String id) {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		return ids.contains(id);
	}
    /**
     * Removes the SliceElement with the given
     * id.  If no SliceElement exists with the given
     * id, then no changes are made.
     * @param id the id of the SliceElement to remove,
     * can not be null.
     * @return this.
     * @throws NullPointerException if id is null.
     */
    public SliceBuilder removeById(String id){
    	int index =ids.indexOf(id);
    	if(index !=-1){
    		ids.remove(index);
    		bytes.remove(index);
    	}
    	return this;
    }
    /**
     * Make a deep copy of this SlliceBuilder.
     * Any changes to either this instance
     * or its copy will not affect the other.
     * @return a new SliceBuilder instance
     * which contains a copy of all the current
     * SliceElements.
     */
    public SliceBuilder copy(){
    	return new SliceBuilder(this);
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public Slice build() {
    	if(consensus==null){
    		return new NoConsensusCompactedSlice(bytes.toArray(), ids);
    	}
    	return new ConsensusCompactedSlice(bytes.toArray(), ids,consensus);
    }
	@Override
	public String toString() {
		return "SliceBuilder [bytes=" + bytes + ", ids=" + ids + ", consensus="
				+ consensus + "]";
	}


}
