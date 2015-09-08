/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * {@code AbstractTwoBitEncodedNucleotideCodec} is a 
 * version of {@link NucleotideCodec} that will
 * pack each basecall into two bytes.
 * The sentiental value can then be used to store
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
