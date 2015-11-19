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
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Encodes each basecall into two bytes.
 * The sentinel value can then be used to store
 * a 5th type of base that is rarely used
 * (gaps or N's).
 * @author dkatzel
 *
 */
abstract class AbstractTwoBitEncodedNucleotideCodec extends AbstractNucleotideCodec{
	private static final int ORDINAL_A = 12;
	private static final int ORDINAL_C = 13;
	private static final int ORDINAL_G = 14;
	private static final int ORDINAL_T = 15;
	
	
	@Override
	 protected byte getByteFor(Nucleotide nuc){
		int ordinal = nuc.ordinal();
         switch(ordinal){
 
         
         	case ORDINAL_A : return (byte)0;
             case ORDINAL_C : return (byte)1;
             case ORDINAL_G : return (byte)2;
             case ORDINAL_T : return (byte)3;
             default : throw new IllegalArgumentException("only A,C,G,T supported : "+ nuc);
         }
     }
	@Override
     protected Nucleotide getGlyphFor(byte b){
     	switch(b){
	        	case 0 : return Nucleotide.Adenine;
	        	case 1: return Nucleotide.Cytosine;
	        	case 2: return Nucleotide.Guanine;
	        	case 3: return Nucleotide.Thymine;
	        	default: throw new IllegalArgumentException("unknown encoded value : "+b);
     	}
     }
	@Override
	protected Nucleotide getNucleotide(byte encodedByte, int index){
		//shift by 1 is the same as *2
		int j= (index%4) <<1;
		return getGlyphFor((byte)((encodedByte >>j) &0x3));
    }
	@Override
	protected void encodeLastGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
        byte b0 = getNextByte(glyphs);
        byte b1 = getNextByte(glyphs);
        byte b2 = getNextByte(glyphs);
        byte b3 = getNextByte(glyphs);
        
        result.put((byte) ((b3<<6 | b2<<4 | b1<<2 | b0) &0xFF));
    }
	private byte getNextByte(Iterator<Nucleotide> glyphs) {
		return glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
	}
	
	
	@Override
	protected void encodeCompleteGroup(Iterator<Nucleotide> glyphs,
			ByteBuffer result, int offset) {
		byte b0 = getSentienelByteFor(glyphs.next());
        byte b1 = getSentienelByteFor(glyphs.next());
        byte b2 = getSentienelByteFor(glyphs.next());
        byte b3 = getSentienelByteFor(glyphs.next());
        
        result.put((byte) ((b3<<6 | b2<<4 | b1<<2 | b0) &0xFF));
		
	}
	protected AbstractTwoBitEncodedNucleotideCodec(Nucleotide sententialBase) {
		super(sententialBase);
	}

	@Override
	protected int getNucleotidesPerGroup() {
		return 4;
	}

	

}
