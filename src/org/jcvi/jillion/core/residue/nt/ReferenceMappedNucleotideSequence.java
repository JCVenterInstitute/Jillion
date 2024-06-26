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
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.stream.Stream;

import lombok.Data;
/**
 * {@code ReferenceMappedNucleotideSequence} is
 * a NucleotideSequence that has been mapped
 * to another reference NucleotideSequence.
 * This sub-interface of {@link NucleotideSequence}
 * which has extra methods to get the differences between
 * the reference and this sequence and the to get the actual 
 * reference sequence used.
 * <p>
 * It is possible to reduce the memory footprint for
 * {@link ReferenceMappedNucleotideSequence}s by only
 * storing these  2 fields.  All other return values from
 * {@link NucleotideSequence} can be computed.  
 * This should keep the memory footprint
 * quite low since an underlying sequence should map to a reference 
 * with a high identity.  If the reference is the consensus,
 * the underlying sequence should map more than 90%.
 * @author dkatzel
 *
 *
 */
public interface ReferenceMappedNucleotideSequence extends NucleotideSequence{
    
    /**
     * Get a Mapping of all the offsets (as Integers) 
     * of this read compared to the reference.
     * All coordinates are 0-based gapped offset locations in the read coordinate system;
     * so if a difference is located in the first base of the read,
     * then its integer will be zero.  The Map is sorted by 
     * offset, increasing from smallest offset to largest.
     * @return a Map of all the differences between
     * this sequence and its reference; will never be null 
     * but may be empty if there are no differences.
     */
    SortedMap<Integer, Nucleotide> getDifferenceMap();
    /**
     * Get the Reference sequence that this
     * sequence is mapped to.
     * @return the {@link NucleotideSequence} of the reference.
     */
    NucleotideSequence getReferenceSequence();
    
    public static Stream<Polymorphism> computePolymorphisms(NucleotideSequence reference, NucleotideSequence query, PolymorphismComputationOption...computationOptions){
    	return new NucleotideSequenceBuilder(query)
    			.setReferenceHint(reference, 0)
    			.turnOffDataCompression(true)
    			.buildReferenceEncodedNucleotideSequence()
    			.computePolymorphisms(computationOptions);
    }
    
    enum PolymorphismType{
    	INSERTION,
    	DELETION,
    	POLYMORPHISM
    	;
    }
    
    @Data
    public static class Polymorphism{
    	private final int offset;
    	private final PolymorphismType type;
    	private final NucleotideSequence referenceSequence;
    	private final NucleotideSequence mappedSequence;
    	
    	public int getLength() {
    		return (int) referenceSequence.getLength();
    	}
    	public static Polymorphism create(int offset, Nucleotide reference, Nucleotide mapped) {
        	return create(offset, NucleotideSequence.of(reference), NucleotideSequence.of(mapped));
    	}
    	public static Polymorphism create(int offset, NucleotideSequence reference, NucleotideSequence mapped) {
    		if(offset < 0) {
    			throw new IllegalArgumentException("offset must be >=0");
    		}
    		Objects.requireNonNull(reference);
    		Objects.requireNonNull(mapped);
    		
    		
    		if(reference.isAllGapsOrBlank()) {
    			return new Polymorphism(offset, PolymorphismType.INSERTION, reference, mapped);
    		}
    		if(mapped.isAllGapsOrBlank()) {
    			return new Polymorphism(offset, PolymorphismType.DELETION, reference, mapped);
        		
    		}
    		return new Polymorphism(offset, PolymorphismType.POLYMORPHISM, reference, mapped);
    		
    	}
    }
    
    public enum PolymorphismComputationOptions implements PolymorphismComputationOption{
    	IGNORE_MAPPED_RUN_NS{

			@Override
			public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
				return !mapped.isAllNs();
			}
    		
    	},
    	IGNORE_MAPPED_RUN_GAPS{

			@Override
			public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
				return !mapped.isAllGapsOrBlank();
			}
    		
    	},
    	IGNORE_REFERENCE_RUN_GAPS{

			@Override
			public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
				return !ref.isAllGapsOrBlank();
			}
    		
    	},
    	IGNORE_MAPPED_AMBIGUITIES{

			@Override
			public boolean include(Nucleotide ref, Nucleotide mapped) {
				return !mapped.isAmbiguity();
			}
    		
    	},
    	IGNORE_INSERTION{
    		@Override
			public boolean include(Nucleotide ref, Nucleotide mapped) {
				return !ref.isGap();
			}
    	},
    	IGNORE_DELETION{
    		@Override
			public boolean include(Nucleotide ref, Nucleotide mapped) {
				return !mapped.isGap();
			}
    	},
    	INCLUDE_ALL
    	;
    	
    	public boolean include(NucleotideSequence ref, NucleotideSequence mapped) {
    		return true;
    	}
    	
    	public boolean include(Nucleotide ref, Nucleotide mapped) {
    		return true;
    	}
    }
    
    default Stream<Polymorphism> computePolymorphisms(PolymorphismComputationOption...computationOptions){
    	
    	//this is sorted so we can check for when we get a break in the offset
    	Iterator<Entry<Integer, Nucleotide>> iter = getDifferenceMap().entrySet().iterator();
    	if(!iter.hasNext()) {
    		return Stream.empty();
    	}
    	PolymorphismComputationOption polyCompOptions;
    	if(computationOptions.length ==0) {
    		polyCompOptions = PolymorphismComputationOptions.INCLUDE_ALL;
    	}else {
    		polyCompOptions = PolymorphismComputationOptions.INCLUDE_ALL.combine(computationOptions);
    	}
    	List<Polymorphism> ret = new ArrayList<>();
    	
    	//first entry
//    	Entry<Integer, Nucleotide> firstEntry = iter.next();
    	
//    	int previousOffset=firstEntry.getKey().intValue();
    	NucleotideSequenceBuilder refSeq= new NucleotideSequenceBuilder(3)
    											.turnOffDataCompression(true);
    	NucleotideSequenceBuilder mapSeq=new NucleotideSequenceBuilder(3)
    											.turnOffDataCompression(true);
    	
    	int previousOffset= Integer.MIN_VALUE;
    	int polymorphOffset=Integer.MIN_VALUE;
    	boolean inside=false;
    	while(iter.hasNext()) {
    		
    		Entry<Integer, Nucleotide> entry = iter.next();
    		int currentOffset = entry.getKey().intValue();
    		if(inside) {
    			//we are inside one check to see if it's connected to this new SNP
    			if(previousOffset +1 < currentOffset) {
    				//it is not connected - add old one if it passes tests
    				NucleotideSequence r = refSeq.build();
        			NucleotideSequence m = mapSeq.build();
    				if(polyCompOptions.include(r, m)) {
        				ret.add(Polymorphism.create(polymorphOffset, r, m));
        			}
        			refSeq.clear();
        			mapSeq.clear();
        			inside=false;
    			}
    			
    			
    		}
    		//now check if current offset should be added
    		Nucleotide r = getReferenceSequence().get(currentOffset);
    		Nucleotide m = entry.getValue();
    		if(polyCompOptions.include(r, m)) {
	    		refSeq.append(r);
	    		mapSeq.append(m);
	    		if(!inside) {
	    			polymorphOffset= currentOffset;
	    		}
	    		inside=true;
	    		previousOffset = currentOffset;
    		}
    		
    	}
    	//if we were here we are done
    	NucleotideSequence r = refSeq.build();
		NucleotideSequence m = mapSeq.build();
		
    	if(!r.isEmpty() &&  polyCompOptions.include(r, m)) {
    		ret.add(Polymorphism.create(polymorphOffset,r, m));
    	}
    	return ret.stream();
    }
}
