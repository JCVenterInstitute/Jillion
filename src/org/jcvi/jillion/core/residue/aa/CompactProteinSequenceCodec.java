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
package org.jcvi.jillion.core.residue.aa;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * {@code CompactProteinSequenceCodec} is able to store
 * {@link AminoAcid} in a byte array where each {@link AminoAcid}
 * only takes up 5 bits. This is a 37.5% memory reduction compared to 
 * encoding the data as one byte each or 68% memory reduction compared
 * to encoding each AminoAcid as one char each.
 * @author dkatzel
 *
 */
enum CompactProteinSequenceCodec implements AminoAcidCodec {
	/**
	 * Singleton instance.
	 */
	INSTANCE
	;
	private static final int BITS_PER_AA = 5;
	
	private CompactProteinSequenceCodec(){
		//private constructor
	}
	@Override
	public byte[] encode(AminoAcid[] aas) {
		int numberOfAminoAcids = aas.length;
		int numBits = numberOfAminoAcids * BITS_PER_AA;
		BitSet bits = new BitSet(numBits);
		int offset=0;
		for(AminoAcid aa : aas){
			byte ordinal = aa.getOrdinalAsByte();
			
			for(int i=0; i<BITS_PER_AA; i++){
				if((ordinal & (1<< i))!=0){
					bits.set(offset);
				}
				offset++;
			}
		}
		byte[] encodedData = IOUtil.toByteArray(bits,numBits);
		ByteBuffer buf = ByteBuffer.allocate(4 + encodedData.length);
		buf.putInt(numberOfAminoAcids);
		buf.put(encodedData);
		return buf.array();
	}
	

	protected AminoAcid getAminoAcidFor(BitSet subSet) {
		final AminoAcid aa;
		if(subSet.isEmpty()){
			aa =AminoAcid.values()[0];
		}else{
			aa =AminoAcid.values()[new BigInteger(IOUtil.toByteArray(subSet,BITS_PER_AA)).intValue()];
		}
		return aa;
	}

	@Override
	public AminoAcid decode(byte[] encodedGlyphs, long index) {
		byte[] tmp = Arrays.copyOfRange(encodedGlyphs, 4, encodedGlyphs.length);
		
		BitSet bits = IOUtil.toBitSet(tmp);
		int bitOffset = BITS_PER_AA *(int)index;
		BitSet subSet = bits.get(bitOffset, bitOffset+BITS_PER_AA);
		return getAminoAcidFor(subSet);
	}

	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
		return buf.getInt();
	}

}
