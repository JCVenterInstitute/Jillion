package org.jcvi.jillion.assembly.util;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.assembly.util.CompactedSlice;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceElement;
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

    GrowableShortArray bytes = new GrowableShortArray(1024);
    List<String> ids = new ArrayList<String>();
    
    /**
     * Create a new {@link SliceBuilder}
     * which will start off empty.
     */
    public SliceBuilder(){
    	
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
    	addSliceElements(slice);
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
    	addSliceElements(elements);
    }
    
    private SliceBuilder(SliceBuilder copy){
    	this.ids = new ArrayList<String>(copy.ids);
    	this.bytes = copy.bytes.copy();
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
    public SliceBuilder addSliceElement(SliceElement element){  
    	if(element ==null){
    		throw new NullPointerException("SliceElement can not be nul");
    	}
        return addSliceElement(element.getId(),element.getBase(), element.getQuality(), element.getDirection());
    }
    public SliceBuilder addSliceElements(Iterable<? extends SliceElement> elements){
        for(SliceElement e : elements){
            addSliceElement(e);
        }
        return this;
    }
    /**
     * Add a new SliceElement witht the following values
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
    public SliceBuilder addSliceElement(String id, Nucleotide base, PhredQuality quality, Direction dir){
    	
    	CompactedSliceElement compacted = new CompactedSliceElement(id, base, quality, dir);
    	int value = compacted.getEncodedDirAndNucleotide() <<8;
    	value |= (compacted.getEncodedQuality() &0xFF);
    	
    	int index =ids.indexOf(id);
    	if(index !=-1){
    		//overwrite
    		bytes.replace(index, (short)value);
    		ids.remove(index);
    		ids.add(index, id);
    		
    	}else{
    		//append
    		bytes.append((short)value);            
            ids.add(id);
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
    public boolean containsElement(String id){
    	return containsId(id);
    }
	private boolean containsId(String id) {
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
    public SliceBuilder removeSliceElement(String id){
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
        return new CompactedSlice(bytes.toArray(), ids);
    }


}
