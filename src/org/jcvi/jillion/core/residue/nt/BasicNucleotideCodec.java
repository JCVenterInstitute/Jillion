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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
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
			return Nucleotide.getDnaValues().get(b);
		}
	    
	@Override
	protected int getNucleotidesPerGroup() {
		return 2;
	}
	
	
	@Override
	public List<Range> getNRanges(byte[] encodedData){
    	List<Range> ranges = new ArrayList<>();
    	Iterator<Nucleotide> iter = iterator(encodedData);
    	int offset=0;
    	Range.Builder currentBuilder = null;
    	while(iter.hasNext()){
    		Nucleotide n = iter.next();
    		if(n == Nucleotide.Unknown){
    			if(currentBuilder == null){
    				currentBuilder = new Range.Builder(1)
    										.shift(offset);
    			}else{
    				//do we grow or make a new one?
    				if(currentBuilder.getEnd() == offset -1){
    					//grow
    					currentBuilder.expandEnd(1);
    				}else{
    					//new range
    					ranges.add(currentBuilder.build());
    					
    					currentBuilder = new Range.Builder(1)
								.shift(offset);
    				}
    			}
    		}
    		offset++;
    	}
    	if(currentBuilder !=null){
    		ranges.add(currentBuilder.build());
			
    	}
    	return ranges;
    }
	
	@Override
	public List<Integer> getGapOffsets(byte[] encodedData) {
		GrowableIntArray array = this.getSentinelOffsets(encodedData);
		
		return array.toBoxedList();
	}
    
}
