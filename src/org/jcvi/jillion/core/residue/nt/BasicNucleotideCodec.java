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
 * {@code BasicNucleotideCodec} is a {@link NucleotideCodec}
 * that will store each base in 4 bits.
 * @author dkatzel
 *
 */
final class BasicNucleotideCodec extends AbstractNucleotideCodec{

	 public static final BasicNucleotideCodec INSTANCE = new BasicNucleotideCodec();
	    
	    private BasicNucleotideCodec(){
	        super(Nucleotide.Gap);
	    }
	    
	    @Override
		protected Nucleotide getNucleotide(byte encodedByte, int index){
	 	   byte value;
	    	if((index & 0x01)==0){
	 		   value = (byte)((encodedByte>>4) &0x0F);
	 	   }else{
	 		  value = (byte)(encodedByte &0x0F);
	 	   }
	 	   return getGlyphFor(value);
	    }
		@Override
		protected void encodeNextGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
	        byte b0 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	        byte b1 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	       
	       
	        result.put((byte) ((b0<<4 | b1) &0xFF));
	    }
	    
	    @Override
		protected byte getByteFor(Nucleotide nuc) {
			return nuc.getOrdinalAsByte();
		}



		@Override
		protected Nucleotide getGlyphFor(byte b) {
			return Nucleotide.VALUES.get(b);
		}
	    
	@Override
	protected int getNucleotidesPerGroup() {
		return 2;
	}
}
