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
package org.jcvi.jillion.assembly;


import java.util.*;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.INucleotideSequence;
import org.jcvi.jillion.core.residue.nt.INucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.streams.ThrowingIndexedConsumer;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedConsumer;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
/**
 * {@code GappedReferenceBuilder} builds 
 * a GAPPED reference {@link NucleotideSequence}
 * from an initial ungapped reference sequence and 
 * lots of insertions to various offsets made by 
 * aligned reads.
 * 
 * If many reads have insertions at the same ungapped offset,
 * the the largest insertion is used. 
 * @author dkatzel
 *
 */
public final class GappedReferenceBuilder<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S,B>> {

	/**
	 * The length of the sequence to start using
	 * a sparse matrix to keep track of insertions.
	 *
	 */
	private static final long LENGTH_TO_SWITCH_TO_SPARSE = 1_000_000;
	private interface Insertions{

		void addReadInsertion(int offset, int insertionSize);
		int computeTotalNumberOfGaps();

		<E extends Throwable> void forEach(ThrowingIntIndexedConsumer<Insertion, E> consumer) throws E;

		static Insertions create(ResidueSequence<?,?,?> seq){
			if(seq.getLength() < LENGTH_TO_SWITCH_TO_SPARSE){
				return new ArrayInsertions((int) seq.getLength()+1);
			}
			return new SparseInsertions();
		}
	}

	private static class SparseInsertions implements Insertions{
		//so we iterate backwards
		NavigableMap<Integer, Insertion> map = new TreeMap<>(Collections.reverseOrder());

		@Override
		public void addReadInsertion(int offset, int insertionSize) {
			map.computeIfAbsent(offset, k-> new Insertion(insertionSize)).updateSize(insertionSize);
		}

		@Override
		public int computeTotalNumberOfGaps() {
			return map.values().stream().mapToInt(Insertion::getSize).sum();
		}

		@Override
		public <E extends Throwable> void forEach(ThrowingIntIndexedConsumer<Insertion, E> consumer) throws E {
			for(Map.Entry<Integer, Insertion> entry : map.entrySet()) {
				consumer.accept(entry.getKey(), entry.getValue());
			};

		}
	}
	private static class ArrayInsertions implements Insertions{
		private Insertion[] insertions;

		public ArrayInsertions(int capacity){
			insertions = new Insertion[capacity];
		}

		@Override
		public void addReadInsertion(int offset, int insertionSize) {

			if(insertions[offset] ==null){
				insertions[offset] = new Insertion(insertionSize);
			}else{
				insertions[offset].updateSize(insertionSize);
			}
		}

		public int computeTotalNumberOfGaps() {
			int numberOfGaps = 0;
			for(int i=0; i<insertions.length; i++){
				Insertion insertion = insertions[i];
				if(insertion !=null){
					numberOfGaps += insertion.getSize();
				}
			}
			return numberOfGaps;
		}

		@Override
		public <E extends Throwable> void forEach(ThrowingIntIndexedConsumer<Insertion, E> consumer) throws E{
			Objects.requireNonNull(consumer);
			//iterates in reverse to keep offsets in sync
			for(int i= insertions.length-1; i>=0; i--){
				Insertion insertion = insertions[i];
				if(insertion !=null){
					consumer.accept(i, insertion);
				}
			}
		}
	}
	private final S ungappedReference;
	private final Insertions insertions;
	
	/**
	 * Create a new instance using the given initial
	 * ungapped reference.
	 * @param ungappedReference the ungapped reference sequence
	 * to use; can not be null or have any gaps or have a length
	 * longer than {@link Integer#MAX_VALUE}
	 * @throws NullPointerException if ungappedReference is null.
	 * @throws IllegalArgumentException if ungappedReference is gapped or if
	 * the length &gt; Integer.MAX_VALUE
	 */
	public GappedReferenceBuilder(S ungappedReference){
		if(ungappedReference ==null){
			throw new NullPointerException("ungapped reference can not be null");
		}
		if(ungappedReference.getNumberOfGaps() >0){
			throw new IllegalArgumentException("reference can not have gaps");
		}
		long length = ungappedReference.getLength();
		if(length > Integer.MAX_VALUE){
			throw new IllegalArgumentException("reference too big > int MAX");
		}
		this.ungappedReference = ungappedReference;
		insertions = Insertions.create(ungappedReference);
	}
	/**
	 * Add an insertion at the given ungapped offset
	 * of the given insertion size.
	 * @param offset the ungapped offset into the reference.
	 * @param insertionSize the insertion size this read has against the reference.
	 * @return this
	 * @throws IllegalArgumentException if insertionSize is negative.
	 */
	public GappedReferenceBuilder<R,S,B> addReadInsertion(int offset, int insertionSize){

		insertions.addReadInsertion(offset, insertionSize);
		return this;
	}
	/**
	 * Add all inserts from the given {@link Cigar}.
	 * @param offset the ungapped offset into the reference.
	 * @param cigar the cigar; can not be null.
	 * @return this
	 * @throws NullPointerException if cigar is null.
	 * @throws IndexOutOfBoundsException if offset is negative or larger than the reference sequence.
	 * @since 6.0
	 */
	public GappedReferenceBuilder<R,S,B> addReadByCigar(int offset, Cigar cigar) {
		int currentOffset = offset;
		
		Iterator<CigarElement> iter =cigar.getElementIterator();
		while(iter.hasNext()){
			CigarElement element = iter.next();
			
			CigarOperation op = element.getOp();
			
			if(op == CigarOperation.HARD_CLIP || op == CigarOperation.SOFT_CLIP || op == CigarOperation.PADDING){
				//ignore gaps and clipping
			}else if(op == CigarOperation.INSERTION){				
					addReadInsertion(currentOffset, element.getLength());
			}else{
				currentOffset+=element.getLength();
			}
		}
		return this;
	}
	
	public S build(){
		//compute total number of gaps
		//first so we don't have to keep resizing builder
		int numberOfGaps = insertions.computeTotalNumberOfGaps();

		B gappedSequenceBuilder =ungappedReference.toBuilder((int)(ungappedReference.getLength() + numberOfGaps));
		insertions.forEach((i, insertion)->{
			gappedSequenceBuilder.insert( i, createGapStringOf(insertion.getSize()));
		});

		return gappedSequenceBuilder.build();
	}


	
	
	private char[] createGapStringOf(int maxGapSize) {
		char[] gaps = new char[maxGapSize];
		Arrays.fill(gaps, '-');
		return gaps;
	}


	private static class Insertion{
	        private int size=0;
	        
	        public  Insertion(int initialSize){
	        	if(initialSize<0){
	        		throw new IllegalArgumentException("insertion size can not be negative : " + initialSize);
	        	}
	            this.size = initialSize;
	        }
	        public void updateSize(int newSize){
	            if(newSize > size){
	                this.size = newSize;
	            }
	        }
	        public int getSize(){
	            return size;
	        }
	    }
}
