/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
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
	@Override
	 protected byte getByteFor(Nucleotide nuc){
         switch(nuc){
         	case Adenine : return (byte)0;
             case Cytosine : return (byte)1;
             case Guanine : return (byte)2;
             case Thymine : return (byte)3;
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
	protected void encodeNextGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
        byte b0 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b1 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b2 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b3 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        
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
