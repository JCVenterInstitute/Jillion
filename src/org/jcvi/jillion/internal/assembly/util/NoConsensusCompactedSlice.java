/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.assembly.util;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public class NoConsensusCompactedSlice implements Slice{
	
    private final short[] elements;
    private final String[] ids;
    
    public static final NoConsensusCompactedSlice EMPTY = (NoConsensusCompactedSlice) new SliceBuilder().build();
    
    /**
     * @param elements array of encoded element data.
     * @param ids list of ids in the same order as the encoded elements.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    		value = {"EI_EXPOSE_REP2"},
    		justification = "only used internally by builders so don't"
    						+ " have to worry about leaking ref")
    public NoConsensusCompactedSlice(short[] elements, List<String> ids) {
        this.elements = elements;
        this.ids = ids.toArray(new String[ids.size()]);
    }

    @Override
    public Stream<SliceElement> elements(){
    	//can't directly stream primitive short array
    	return StreamSupport.stream(Spliterators.spliterator(iterator(), elements.length, Spliterator.IMMUTABLE), false);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<SliceElement> iterator() {
    	
        return new Iterator<SliceElement>(){
        	int i=0;
            @Override
            public boolean hasNext() {
                return i<ids.length;
            }

            @Override
            public SliceElement next() {
            	if(!hasNext()){
            		throw new NoSuchElementException();
            	}
            	//i++ returns old value of i
                return getElement(i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
                
            }
            
        };
    }
    
    
    @Override
	public Map<Nucleotide, Integer> getNucleotideCounts() {
		int[] counts = new int[Nucleotide.getDnaValues().size()];
		for(int i=0; i < elements.length; i++){
			int ordinal =(elements[i] >>>8) &0xF;
			counts[ordinal]++;
		}
		Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(int i=0; i < counts.length; i++){
			int count = counts[i];
			map.put(Nucleotide.getDnaValues().get(i), count);
		}
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//this is so 2 Slices that have the same ids but in different order still have same hashcode
		result = prime * result + sum(elements);
		
		result = prime * result + new HashSet<>(Arrays.asList(ids)).hashCode();
		
		
		
		return result;
	}

	private static int sum(short[] s){
	    int sum=0;
	    for(int i=0; i< s.length; i++){
	        sum +=s[i];
	    }
	    return sum;
	}
	@Override
	public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Slice)){
            return false;
        }
        Slice other = (Slice) obj;
        if(getCoverageDepth() !=other.getCoverageDepth()){
        	return false;
        }
        Nucleotide consensusCall = getConsensusCall();
		Nucleotide otherConsensusCall = other.getConsensusCall();
		if(consensusCall ==null){
        	if(otherConsensusCall!=null){
        		return false;
        	}
        }else{
        	if(!consensusCall.equals(otherConsensusCall)){
        		return false;
        	}
        }
		Iterator<SliceElement> iter = other.iterator();
		while(iter.hasNext()){
			
			SliceElement element = iter.next();
			if(!element.equals(getSliceElement(element.getId()))){
				return false;
			}
			
		}
		 
       return true;
            
    }
    
    private CompactedSliceElement getElement(int i){
    	String id = ids[i];
        return CompactedSliceElement.create(id, elements[i]);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getCoverageDepth() {
        return ids.length;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsElement(String elementId) {
    	return indexOf(elementId) >=0;
    }

    private int indexOf(String id){
    	if(id==null){
    		for(int i=0; i< ids.length; i++){
        		if(ids[i]==null){
        			return i;
        		}
        	}
    	}else{
    		for(int i=0; i< ids.length; i++){
        		if(id.equals(ids[i])){
        			return i;
        		}
        	}
    	}
    	return -1;
    }
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("consensus = ")
        						.append(getConsensusCall())
        						.append(" ");
        for(SliceElement element : this){
            builder.append(element).append( " | ");
        }
        
        
        return builder.toString();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public SliceElement getSliceElement(String elementId) {
        int index= indexOf(elementId);
        if(index<0){
            return null;
        }
        return getElement(index);
    }

	@Override
	public Nucleotide getConsensusCall() {
		return null;
	}
    
   

}
