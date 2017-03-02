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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

final class DefaultReferenceEncodedNucleotideSequence extends AbstractResidueSequence<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> implements ReferenceMappedNucleotideSequence{

    /**
	 * 
	 */
	private static final long serialVersionUID = 938806681397322051L;
	private final int length;
    private final int startOffset;
    private final NucleotideSequence reference;
    /**
     * All of the differences between this 
     * read and the reference it is aligned 
     * to (which could be a contig consensus)
     * are encoded here. If there are no SNPs in this read:
     * meaning that this read aligns to its reference at 100%
     * identity, then this value <strong> will be null</strong>.
     * <br/>
     * The encoding uses {@link ValueSizeStrategy}
     * to pack the data in as few bytes as possible.
     * Here is the current encoding:<br/>
     * <ul>
     *  <li>first byte is the ordinal value of the {@link ValueSizeStrategy}
     *   used to store the number of elements</li>
     *  
     *  <li>the next 1,2 or 4 bytes (depending on the {@link ValueSizeStrategy}
     *  specified in the previous byte) denote the number of SNPs encoded.</li>
     *  
     *  <li>the next byte is the ordinal value of the {@link ValueSizeStrategy}
     *   used to store the number of bytes
     *   required for each SNP offset value</li>
     *  
     *  <li>the next $num_snps * 1,2 or 4 bytes (depending on the {@link ValueSizeStrategy}
     *  specified in the previous byte) encode the SNP offsets in the read.</li>
     *  
     *  </li> the remaining bytes store the actual SNP values as Nucleotide ordinal values
     *  packed as 4 bits each.  This means that each byte actually stores
     *  2 SNPs.</li>
     *  <ul/>
     */
    private final byte[] encodedSnpsInfo;
    /**
     * Our HashCode value,
     * This value is lazy loaded
     * so we only have 
     * to compute the hashcode value
     * once.
     * 
     * We can afford to store it because
     * the Java memory model will padd out
     * the bytes anyway so we don't
     * take up any extra memory.
     */
    private transient int hash;
    
    
    @Override
	public SortedMap<Integer, Nucleotide> getDifferenceMap() {
    	if(encodedSnpsInfo==null){
    		return new TreeMap<Integer, Nucleotide>();
    	}
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        ValueSizeStrategy numSnpsSizeStrategy = ValueSizeStrategy.values()[buf.get()];
		int size = numSnpsSizeStrategy.getNext(buf);
        ValueSizeStrategy snpSizeStrategy = ValueSizeStrategy.values()[buf.get()];
    	byte[] snps = getSnpArray(numSnpsSizeStrategy, size, snpSizeStrategy);
    	SortedMap<Integer, Nucleotide> differenceMap = new TreeMap<Integer, Nucleotide>();
    	for(int i=0; i<size; i++){        	
            Integer offset = snpSizeStrategy.getNext(buf);
            int index = i/2;
        	if(i%2==0){
        		int temp1 = snps[index]>>4;
				differenceMap.put(offset, Nucleotide.VALUES.get(temp1 & 0x0F));
        	}else{
        		differenceMap.put(offset,Nucleotide.VALUES.get(snps[index] & 0x0F));
        	}
			
        }
		return differenceMap;
	}

    public DefaultReferenceEncodedNucleotideSequence(NucleotideSequence reference,
            String toBeEncoded, int startOffset){
    	this(reference, new NucleotideSequenceBuilder(toBeEncoded), startOffset);
    }

	public DefaultReferenceEncodedNucleotideSequence(NucleotideSequence reference,
            NucleotideSequenceBuilder toBeEncoded, int startOffset){
    
        this.startOffset = startOffset;
        this.length = (int)toBeEncoded.getLength();
        this.reference = reference;
        SortedMap<Integer, Nucleotide> differentGlyphMap = populateFields(reference, toBeEncoded, startOffset);
        int numSnps = differentGlyphMap.size();
        if(numSnps ==0){
        	//no snps
        	encodedSnpsInfo =null;
        	return;
        }
       
    	ValueSizeStrategy snpSizeStrategy = ValueSizeStrategy.getStrategyFor(differentGlyphMap.lastKey().intValue());
        int snpByteLength = computeNumberOfBytesToStore(numSnps,snpSizeStrategy);
        ValueSizeStrategy numSnpsStrategy = ValueSizeStrategy.getStrategyFor(snpByteLength);
        int bufferSize = numSnpsStrategy.getNumberOfBytesPerValue()+ snpByteLength;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        
        buffer.put((byte)numSnpsStrategy.ordinal());
		numSnpsStrategy.put(buffer, numSnps);
        
        buffer.put((byte)snpSizeStrategy.ordinal());
        int i=0;
        byte[] snpValues = new byte[(numSnps+1)/2];
    	for(Entry<Integer, Nucleotide> entry : differentGlyphMap.entrySet()){
        
        	snpSizeStrategy.put(buffer, entry.getKey().intValue());
        	byte ordinal = entry.getValue().getOrdinalAsByte();
        	int index = i/2;
        	if(i%2==0){
        		snpValues[index] = (byte)(ordinal<<4 & 0xF0);
        	}else{
        		snpValues[index] = (byte)(snpValues[index] | ordinal);
        	}
        	  	
        	i++;
        }
    	
		buffer.put(snpValues);	        
        encodedSnpsInfo = buffer.array();
    
    }
    
    
    private int computeNumberOfBytesToStore(int numSnps,ValueSizeStrategy snpSizeStrategy) {
    	int numBytesPerSnpIndex = snpSizeStrategy.getNumberOfBytesPerValue();
    	int numBytesRequiredToStoreSnps = (numSnps+1)/2;
    	int numberOfBytesToStoreSnpOffsets=numBytesPerSnpIndex*numSnps;
    	return 2+numberOfBytesToStoreSnpOffsets + numBytesRequiredToStoreSnps;
	}


	private SortedMap<Integer, Nucleotide> populateFields(
			NucleotideSequence reference,
            NucleotideSequenceBuilder toBeEncoded, int startOffset) {
        handleBeforeReference(startOffset);
        handleAfterReference(reference, toBeEncoded, startOffset);
        TreeMap<Integer, Nucleotide> differentGlyphMap = new TreeMap<Integer, Nucleotide>();
        
       Iterator<Nucleotide> readIterator = toBeEncoded.iterator();
       Iterator<Nucleotide> refIterator = reference.iterator(new Range.Builder(length)
    		   													.shift(startOffset)
    		   													.build());
       int i=0;
       while(readIterator.hasNext()){
            Nucleotide g = readIterator.next();
            final Nucleotide referenceGlyph = refIterator.next();            
            if(isDifferent(g, referenceGlyph)){
                    differentGlyphMap.put(Integer.valueOf(i), g);
            }
            i++;
        }
        return differentGlyphMap;
    }

    private void handleAfterReference(Sequence<Nucleotide> reference,
            NucleotideSequenceBuilder toBeEncoded, int startOffset) {
        int lastOffsetOfSequence = (int)toBeEncoded.getLength()+startOffset;
        if(lastOffsetOfSequence > reference.getLength()){
            int overhang = (int)(toBeEncoded.getLength()+startOffset - reference.getLength());
            throw new IllegalArgumentException(String.format("sequences extends beyond reference by %d base(s)", overhang));
        }
    }


    private void handleBeforeReference(int startOffset) {
        if(startOffset<0){
            //handle before values
           throw new IllegalArgumentException("can not start before reference: "+ startOffset);
        }
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private boolean isDifferent(Nucleotide g, final Nucleotide referenceGlyph) {
        return g!=referenceGlyph;
    }

    @Override
	public Iterator<Nucleotide> iterator() {
    	Nucleotide[] array = asNucleotideArray();
    	return new ArrayIterator<>(array);
	}

	@Override
	public Iterator<Nucleotide> iterator(Range range) {		
		Nucleotide[] array = asNucleotideArray(range);
		return new ArrayIterator<>(array);
	}




	private Nucleotide[] createReferenceArray(Range range){
		Nucleotide[] array = new Nucleotide[(int)range.getLength()];
		Iterator<Nucleotide> iter = reference.iterator(range);
		int i=0;
		while(iter.hasNext()){
			array[i] = iter.next();
			i++;
		}
		return array;
	}
	private Nucleotide[] asNucleotideArray(Range range) {
		if(range==null){
			throw new NullPointerException("range can not be null");
		}
		//make sure begining of range is in bounds
		if(range.getBegin() < 0){
			throw new IndexOutOfBoundsException();
		}
		//make sure range is in bounds
		if(range.getEnd()>= length){
			throw new IndexOutOfBoundsException(range.getEnd() + " is beyond end of sequence (length = " + length + ")");
		}
		Nucleotide[] array = asNucleotideArray();
		
		return Arrays.copyOfRange(array, (int)range.getBegin(), (int)range.getEnd()+1);
	}
	private Nucleotide[] asNucleotideArray() {
		//get the reference bases as an array
		//we convert to an array since
		//we need to replace with our SNPs
		//and its simpler than
		//list.remove(offset); list.add(offset, snp);
		//with boundary checking
		//or 
		//list.add(offset, snp);
		//list.remove(offset+1);
		//without resizing list everytime.
		Nucleotide[] array= createReferenceArray(new Range.Builder(length)
										.shift(startOffset).build());
		if(encodedSnpsInfo !=null){
			//pull out all of our SNP data at the same
			//time and 
	        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
	        
	        ValueSizeStrategy numSnpsSizeStrategy = ValueSizeStrategy.values()[buf.get()];
			int size = numSnpsSizeStrategy.getNext(buf);
	        ValueSizeStrategy sizeStrategy = ValueSizeStrategy.values()[buf.get()];
	        byte[] snps = getSnpArray(numSnpsSizeStrategy, size, sizeStrategy);
	        for(int i=0; i<size; i++){        	
	            int index = sizeStrategy.getNext(buf); 
				int snpIndex = i/2;
				if(i%2==0){
					array[index]= Nucleotide.VALUES.get(snps[snpIndex]>>4 &0x0F);
				}else{
					array[index]= Nucleotide.VALUES.get(snps[snpIndex] & 0x0F);
				}
	        }
        }
		return array;
	}
    @Override
    public Nucleotide get(long index) {
        if(index <0 || index >= length){
            throw new IndexOutOfBoundsException("invalid offset " +index);
        }
        
        
        if(encodedSnpsInfo !=null){
        	//there are snps so we need to check them first
        
	        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
	        
	        ValueSizeStrategy numSnpsSizeStrategy = ValueSizeStrategy.values()[buf.get()];
			int size = numSnpsSizeStrategy.getNext(buf);
	        ValueSizeStrategy sizeStrategy = ValueSizeStrategy.values()[buf.get()];
	        byte[] snps = getSnpArray(numSnpsSizeStrategy, size, sizeStrategy);
	        for(int i=0; i<size; i++){        	
	            int nextValue = sizeStrategy.getNext(buf);
				if(index ==nextValue){
					int snpIndex = i/2;
					if(i%2==0){
						return Nucleotide.VALUES.get(snps[snpIndex]>>4 &0x0F);
					}else{
						return Nucleotide.VALUES.get(snps[snpIndex] & 0x0F);
					}

	            }
	        }
        }
        long referenceIndex = index+startOffset;
        return reference.get(referenceIndex);
    }



	private byte[] getSnpArray(ValueSizeStrategy numSnpsSizeStrategy,
			int size, ValueSizeStrategy sizeStrategy) {
		int from = numSnpsSizeStrategy.getNumberOfBytesPerValue()+2+size*sizeStrategy.getNumberOfBytesPerValue();
		return Arrays.copyOfRange(encodedSnpsInfo, from, encodedSnpsInfo.length);
	}


    @Override
    public boolean isGap(int index) {
        return getGapOffsets().contains(Integer.valueOf(index));
        
    }
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public List<Integer> getGapOffsets() {
    	GrowableIntArray referenceGapOffsets = shiftReferenceGaps();
        if(encodedSnpsInfo !=null){
	        return modifyForSnps(referenceGapOffsets);
        }
        return ArrayUtil.asList(referenceGapOffsets.toArray());
    }

	private List<Integer> modifyForSnps(GrowableIntArray gaps) {
		//now check our snps to see
		//1. if we have snp where the ref has a gap
		//2. if we have gap
		ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
		int size = ValueSizeStrategy.values()[buf.get()].getNext(buf);
		ValueSizeStrategy sizeStrategy = ValueSizeStrategy.values()[buf.get()];
		GrowableIntArray snps = new GrowableIntArray(size);
		for(int i=0; i<size; i++){
		    int snpOffset = sizeStrategy.getNext(buf);
		    //if we have a snp where 
		    //the reference has a gap
		    //remove it from our list of gaps
		    int index = gaps.binarySearch(snpOffset);
			if(index>=0){
		        gaps.remove(index);
		    }
		    snps.append(snpOffset);
		}
		if(buf.hasRemaining()){
			int numBytesRemaining =buf.remaining();
			
			 byte[] snpArray = Arrays.copyOfRange(encodedSnpsInfo, encodedSnpsInfo.length- numBytesRemaining, encodedSnpsInfo.length);
		     for(int i=0; i<size; i++){
		    	 int snpIndex = i/2;
		    	 final Nucleotide snp;
					if(i%2==0){
						snp= Nucleotide.VALUES.get(snpArray[snpIndex]>>4 &0x0F);
					}else{
						snp= Nucleotide.VALUES.get(snpArray[snpIndex] & 0x0F);
					}
		    	 if(Nucleotide.Gap == snp){
		    		 gaps.append(snps.get(i));
		    	 }
		     }
		    
		}
		//sort gaps so they are in order
		//before this line, our gaps are in
		//sorted ref gaps
		//followed by sorted snps which happen to be gaps
		gaps.sort();
		return ArrayUtil.asList(gaps.toArray());
	}
	//first, get gaps from our aligned section of the reference
    //we may have a snp in the gap location
    //so we need to check for that
	/**
	 * Most reference gaps should also
	 * be present in our gapped sequence
	 * so we need to get the reference gaps
	 * that overlap with our sequence range
	 * and shift them accordingly so read coordinate space.
	 * (offset 0 is first base in our read)
	 * @return
	 */
	private GrowableIntArray shiftReferenceGaps() {
		List<Integer> refGapOffsets = reference.getGapOffsets();
		GrowableIntArray gaps = new GrowableIntArray(refGapOffsets.size());
        for(Integer refGap : refGapOffsets){
            int adjustedCoordinate = refGap.intValue() - startOffset;
            if(adjustedCoordinate >=0 && adjustedCoordinate<length){
                gaps.append(adjustedCoordinate);
            }
        }
		return gaps;
	}

	@Override
    public int hashCode() {
		long length = getLength();
		if(hash==0 && length >0){
	        final int prime = 31;
	        int result = 1;
	        Iterator<Nucleotide> iter = iterator();
	        while(iter.hasNext()){
	        	result = prime * result + iter.next().hashCode();
	        }
	        hash= result;
		}
	    return hash;
    }
    @Override
    public boolean equals(Object obj) {
    	 if (this == obj){
             return true;
         }
         if (!(obj instanceof NucleotideSequence)){
             return false;
         }
         NucleotideSequence other = (NucleotideSequence) obj;
         if(getLength() != other.getLength()){
         	return false;
         }
        Iterator<Nucleotide> iter = iterator();
        Iterator<Nucleotide> otherIter = other.iterator();
        while(iter.hasNext()){
     	   if(!iter.next().equals(otherIter.next())){
     		   return false;
     	   }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfGaps() {
         return getGapOffsets().size();
     }
    
    
     @Override
     public String toString(){
    	 Nucleotide[] array = asNucleotideArray();
    	 StringBuilder builder = new StringBuilder(array.length);
    	 for(int i=0; i< array.length; i++){
    		 builder.append(array[i]);
    	 }
         return builder.toString();
     }



	@Override
	public NucleotideSequence getReferenceSequence() {
		return reference;
	}

	@Override
	public NucleotideSequenceBuilder toBuilder() {
		return new NucleotideSequenceBuilder(this)
						.setReferenceHint(reference, startOffset);
	}
	
	@Override
        public NucleotideSequenceBuilder toBuilder(Range range) {
                return new NucleotideSequenceBuilder(this, range)
                                                .setReferenceHint(reference, (int)(startOffset + range.getBegin()));
        }

	 @Override
	    public NucleotideSequence asSubtype(){
	        return this;
	    }
}
