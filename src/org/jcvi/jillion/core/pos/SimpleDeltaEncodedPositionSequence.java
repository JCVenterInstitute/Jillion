package org.jcvi.jillion.core.pos;

import java.util.BitSet;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;

public class SimpleDeltaEncodedPositionSequence implements PositionSequence{

	//private final byte[] deltaBytes;
	
	public SimpleDeltaEncodedPositionSequence(short[] positions){
		
		int[] deltas = new int[positions.length];
		deltas[0]=positions[0];
		int previous = positions[0];
		int maxDelta=0;
		for(int i=1; i< positions.length; i++){
			int delta = positions[i]-previous;
			if(delta <0){
				throw new IllegalArgumentException("can not have negative delta");
			}
			if(delta > maxDelta){
				maxDelta = delta;
			}
			deltas[i]=delta;
		}
		int numberOfBitsPerDelta = IOUtil.computeNumberOfBitsIn(maxDelta);
		int numberOfBytesPerGroup = computeNumberOfBytesPerGroup(numberOfBitsPerDelta);
		int bitsPerGroup = numberOfBytesPerGroup*8;
		int numberOfValuesPerGroup = bitsPerGroup/numberOfBitsPerDelta;
		
		int mask = (int) Math.pow(2, numberOfBitsPerDelta);
		
		int numberOfGroups = positions.length/numberOfValuesPerGroup;
		for(int i=0; i<numberOfGroups; i++){
			
			BitSet bits = new BitSet(bitsPerGroup);			
			int offset = i*numberOfValuesPerGroup;
			for(int j=offset, k=0; j<numberOfValuesPerGroup; j++, k+=numberOfBitsPerDelta){
				int delta = deltas[j];
				setBitsFor(bits, k,delta,mask);
			}
			IOUtil.toByteArray(bits, bitsPerGroup);
		}
	}
	
	private void setBitsFor(BitSet bits, int offset, int value, int initialMask) {
		int mask = initialMask;
		int shift=0;
		while(mask >0){
			if((value & mask ) !=0){
				bits.set(offset+shift);            	
			}
			shift++;
			mask >>>=2;
		}
		
	}
	
	private int computeNumberOfBytesPerGroup(int numberOfBitsPerDelta) {
		int bits = 8;
		do{
			if(bits %numberOfBitsPerDelta==0){
				return bits/8;
			}
			bits <<=2;
		}while(true);
	}

	@Override
	public Position get(long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLength() {
		//return deltaBytes.length -1;
		return 0;
	}

	@Override
	public Iterator<Position> iterator(Range range) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Position> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
