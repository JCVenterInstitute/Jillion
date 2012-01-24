package org.jcvi.common.core.symbol.residue.aa;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.GlyphCodec;
/**
 * {@code CompactAminoAcidSequenceCodec} is able to store
 * {@link AminoAcid} in a byte array where each {@link AminoAcid}
 * only takes up 5 bits. This is a 37.5% memory reduction compared to 
 * encoding the data as one byte each or 68% memory reduction compared
 * to encoding each AminoAcid as one char each.
 * @author dkatzel
 *
 */
public enum CompactAminoAcidSequenceCodec implements GlyphCodec<AminoAcid> {
	/**
	 * Singleton instance.
	 */
	INSTANCE
	;
	private static final int BITS_PER_AA = 5;
	
	private CompactAminoAcidSequenceCodec(){
		//private constructor
	}
	@Override
	public byte[] encode(Collection<AminoAcid> glyphs) {
		int numberOfAminoAcids = glyphs.size();
		int numBits = numberOfAminoAcids * BITS_PER_AA;
		BitSet bits = new BitSet(numBits);
		int offset=0;
		for(AminoAcid aa : glyphs){
			byte ordinal = aa.getOrdinalAsByte();
			
			for(int i=0; i<BITS_PER_AA; i++){
				if((ordinal & (1<< i))!=0){
					bits.set(offset);
				}
				offset++;
			}
		}
		byte[] encodedData = IOUtil.toByteArray(bits);
		ByteBuffer buf = ByteBuffer.allocate(4 + encodedData.length);
		buf.putInt(numberOfAminoAcids);
		buf.put(encodedData);
		return buf.array();
	}

	@Override
	public List<AminoAcid> decode(byte[] encodedGlyphs) {
		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
		int size = buf.getInt();
		byte[] tmp = Arrays.copyOfRange(encodedGlyphs, 4, encodedGlyphs.length);
		
		BitSet bits = IOUtil.toBitSet(tmp);
		List<AminoAcid> list = new ArrayList<AminoAcid>(size);
		int offset=0;
		while(list.size()<size){
			BitSet subSet =bits.get(offset, offset+BITS_PER_AA);
			final AminoAcid aa = getAminoAcidFor(subSet);
			list.add(aa);
			offset+=BITS_PER_AA;
		}
		return list;
	}

	protected AminoAcid getAminoAcidFor(BitSet subSet) {
		final AminoAcid aa;
		if(subSet.isEmpty()){
			aa =AminoAcid.values()[0];
		}else{
			aa =AminoAcid.values()[new BigInteger(IOUtil.toByteArray(subSet)).intValue()];
		}
		return aa;
	}

	@Override
	public AminoAcid decode(byte[] encodedGlyphs, int index) {
		byte[] tmp = Arrays.copyOfRange(encodedGlyphs, 4, encodedGlyphs.length);
		
		BitSet bits = IOUtil.toBitSet(tmp);
		int bitOffset = BITS_PER_AA *index;
		BitSet subSet = bits.get(bitOffset, bitOffset+BITS_PER_AA);
		return getAminoAcidFor(subSet);
	}

	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
		return buf.getInt();
	}

}
