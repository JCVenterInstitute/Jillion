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
package org.jcvi.jillion.core.residue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.Triplet;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence.Variant;
import org.jcvi.jillion.core.util.iter.IteratorUtil;

public enum Frame{
    ONE(1),
    TWO(2),
    THREE(3),
    
    
    NEGATIVE_ONE(-1){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_TWO(-2){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_THREE(-3){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    }
    ;
    
    private int frame;
    
    private static final Frame[] VALUES = values();
    //need ArrayList to get the rollover iterator to work
    //since we need marker interface RandomAccess
    private static final ArrayList<Frame> FORWARDS = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
    private static final ArrayList<Frame> REVERSE = new ArrayList<>(Arrays.asList(NEGATIVE_ONE, NEGATIVE_TWO, NEGATIVE_THREE));
    
    public  final int getFrame() {
        return frame;
    }
    Frame(int frame){
        this.frame = frame;
    }
    
    public Frame shift(int amount){
        
        if(amount <1){
            throw new IllegalArgumentException("amount must be positive");
        }
        int shift = amount %3;
        Iterator<Frame> rollOverIter;
        if(ordinal() < 3){
            rollOverIter = IteratorUtil.rollover(FORWARDS, ordinal()+1);
        }else{
            rollOverIter = IteratorUtil.rollover(REVERSE, ordinal()-2);
        }
        Frame f = this;
        for(int i=0; i< shift; i++){
            f = rollOverIter.next();
        }
        return f;
    }
    /**
     * Parse a {@link Frame} from the given int value.
     * Valid values are <code>-3</code> to <code>3</code>
     * inclusive excluding 0.
     * @param frame the frame number as an int (1, 2, 3, -1, -2, -3).
     * 
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame &lt; 1 || frame &gt; 2</code>
     */
    public static Frame parseFrame(int frame){
        for(Frame f : Frame.values()){
            if(f.frame == frame){
                return f;
            }
        }
     
        throw new IllegalArgumentException("unable to parse frame " + frame);
    }
    
    
    public static List<Frame> forwardFrames(){
        return FORWARDS;
    }
    
    public static List<Frame> reverseFrames(){
        return REVERSE;
    }
    public boolean onReverseStrand() {
        // overridden by -1, -2 and -3
        return false;
    }
    
    public Frame getOppositeFrame(){
        return VALUES[ (this.ordinal() +3)%VALUES.length];
    }
    public Iterator<Set<Triplet>> asTriplets(NucleotideSequence sequence, boolean ignoreGaps){
    	return asTriplets(sequence, ignoreGaps, null);
    }
    public Iterator<Set<Triplet>> asTriplets(NucleotideSequence sequence, boolean ignoreGaps, Integer length){
    	Iterator<Nucleotide> iter = handleFrame(sequence, this, length);
        return new Iterator<Set<Triplet>>() {

            Set<Triplet> next;
            {
                next = getNextTriplet(iter, ignoreGaps);
            }
            @Override
            public boolean hasNext() {
                return next !=null;
            }

            @Override
            public Set<Triplet> next() {
                if(!hasNext()){
                    throw new NullPointerException();
                }
                Set<Triplet> ret = next;
                next = getNextTriplet(iter, ignoreGaps);
                return ret;
            }
        };
    }
    public Iterator<Set<Triplet>> asTriplets(VariantNucleotideSequence sequence, boolean ignoreGaps){
    	return asTriplets(sequence, ignoreGaps, null);
    }
    public Iterator<Set<Triplet>> asTriplets(VariantNucleotideSequence sequence, boolean ignoreGaps, Integer limit){
    	Iterator<List<Nucleotide>> iter = handleFrame(sequence, this, limit);
        return new Iterator<Set<Triplet>>() {

            Set<Triplet> next;
            {
                next = getNextVariantTriplet(iter, ignoreGaps);
            }
            @Override
            public boolean hasNext() {
                return next !=null;
            }

            @Override
            public Set<Triplet> next() {
                if(!hasNext()){
                    throw new NullPointerException();
                }
                Set<Triplet> ret = next;
                next = getNextVariantTriplet(iter, ignoreGaps);
                return ret;
            }
        };
    }
    public Iterator<Set<Triplet>> asTriplets(NucleotideSequence sequence){
        return asTriplets(sequence, false);
    }

    private Set<Triplet> getNextTriplet(Iterator<Nucleotide> iter, boolean ignoreGaps) {

        Nucleotide first = getNextNucleotide(iter, ignoreGaps);
        Nucleotide second = getNextNucleotide(iter, ignoreGaps);
        Nucleotide third = getNextNucleotide(iter, ignoreGaps);
        if (first == null || second == null || third == null) {
            // no more bases
            return null;
        }
        //check for ambiguities
        if(first.isAmbiguity() || second.isAmbiguity() || third.isAmbiguity()) {
        	//handle ambiguities
        	Set<Triplet> triplets = new LinkedHashSet<Triplet>();
        	for(Nucleotide f : first.getBasesFor()) {
        		for(Nucleotide s : second.getBasesFor()) {
        			for(Nucleotide t: third.getBasesFor()) {
        				triplets.add(Triplet.create(f, s, t));
        			}
        		}
        	}
        	return triplets;
        }else {
        	//simple path no ambiguities
        	return Set.of(Triplet.create(first, second, third));
        }
    }
    private Set<Triplet> getNextVariantTriplet(Iterator<List<Nucleotide>> iter, boolean ignoreGaps) {

        List<Nucleotide> first = getNextVariantNucleotide(iter, ignoreGaps);
        List<Nucleotide> second = getNextVariantNucleotide(iter, ignoreGaps);
        List<Nucleotide> third = getNextVariantNucleotide(iter, ignoreGaps);
        if (first == null || second == null || third == null) {
            // no more bases
            return null;
        }
        
    	//handle ambiguities and variants
    	Set<Triplet> triplets = new LinkedHashSet<Triplet>();
    	for(Nucleotide f : first.stream().flatMap( n-> n.getBasesFor().stream()).collect(Collectors.toCollection(LinkedHashSet::new))) {
    		for(Nucleotide s : second.stream().flatMap( n-> n.getBasesFor().stream()).collect(Collectors.toCollection(LinkedHashSet::new))) {
    			for(Nucleotide t: third.stream().flatMap( n-> n.getBasesFor().stream()).collect(Collectors.toCollection(LinkedHashSet::new))) {
    				triplets.add(Triplet.create(f, s, t));
    			}
    		}
    	}
    	return triplets;
       
    }
    private List<Nucleotide> getNextVariantNucleotide(Iterator<List<Nucleotide>> iter, boolean ignoreGaps) {
        if (!iter.hasNext()) {
            return null;
        }
        
        List<Nucleotide> n = iter.next();
        if(ignoreGaps) {
        	while(n !=null &&  n.get(0)==Nucleotide.Gap) {
        		n = iter.hasNext()? iter.next(): null;
        	}
        }
        return n;
    }
    private Nucleotide getNextNucleotide(Iterator<Nucleotide> iter, boolean ignoreGaps) {
        if (!iter.hasNext()) {
            return null;
        }
        
        Nucleotide n = iter.next();
        if(ignoreGaps) {
        	while( n==Nucleotide.Gap) {
        		n = iter.hasNext()? iter.next(): null;
        	}
        }
        return n;
    }
    @SuppressWarnings("fallthrough")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
    private Iterator<Nucleotide> handleFrame(NucleotideSequence sequence, Frame frame, Integer limit) {
        Iterator<Nucleotide> iter;
        if(frame.onReverseStrand()){
            iter = sequence.reverseComplementIterator();
          //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case NEGATIVE_THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case NEGATIVE_TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }else{
            iter = sequence.iterator();
            //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }
        if(limit ==null) {
        	return iter;
        }
        return CountingIterator.of(iter, limit);
    }
    
    @SuppressWarnings("fallthrough")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
    private Iterator<List<Nucleotide>> handleFrame(VariantNucleotideSequence sequence, Frame frame, Integer limit) {
        Iterator<List<Nucleotide>> iter;
        if(frame.onReverseStrand()){
            iter = IteratorUtil.map(sequence.reverseComplementVariantIterator(),
            		Variant::getOrderedAlleles);
          //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case NEGATIVE_THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case NEGATIVE_TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }else{
            iter = IteratorUtil.map(sequence.variantIterator(),
            		Variant::getOrderedAlleles);
            //switch uses fall through
            //so frame 2 skips first 2 bp           
            switch(frame){
                    case THREE:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                    case TWO:
                                    if(iter.hasNext()){
                                            iter.next();
                                    }
                                    break;
                    default:
                                    //no-op
                            break;
            }
        }
        if(limit ==null) {
        	return iter;
        }
        return CountingIterator.of(iter, limit);
    }
    
    private static class CountingIterator<T> implements Iterator<T>{
    	private final Iterator<T> delegate;

    	private int count=0;
    	private final int limit;
    	
    	public static <T> CountingIterator<T> of(Iterator<T> iter, int limit){
    		return new CountingIterator<T>(iter, limit);
    	}
		public CountingIterator(Iterator<T> delegate, int limit) {
			this.delegate = delegate;
			this.limit = limit;
		}
		
		@Override
		public boolean hasNext() {
			return count < limit && delegate.hasNext();
		}

		@Override
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			T ret = delegate.next();
			count++;
			return ret;
		}


		@Override
		public void remove() {
			delegate.remove();
		}
		public int getCount() {
			return count;
		}

    	
    }
}
