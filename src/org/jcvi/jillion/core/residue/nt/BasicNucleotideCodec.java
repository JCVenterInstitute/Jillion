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
 * Encodes each Nucleotide as 4 bits.
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
		protected void encodeLastGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
	        byte b0 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	        byte b1 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	       
	       
	        result.put((byte) ((b0<<4 | b1) &0xFF));
	    }
		
		
	    
	    @Override
		protected void encodeCompleteGroup(Iterator<Nucleotide> glyphs,
				ByteBuffer result, int offset) {
	    	 byte b0 = getByteFor(glyphs.next());
		     byte b1 = getByteFor(glyphs.next());
		       
		       
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
