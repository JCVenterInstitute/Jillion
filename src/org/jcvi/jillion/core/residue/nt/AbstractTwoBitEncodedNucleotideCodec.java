package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;

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
 	   //endian is backwards
 	   int j = (3-index%4)*2;
 	   return getGlyphFor((byte)((encodedByte>>j) &0x3));
    }
	@Override
	protected GrowableIntArray encodeNextGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
        byte b0 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b1 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b2 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        byte b3 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
        
        GrowableIntArray sentenielOffsets = new GrowableIntArray(4);
        if(b0== SENTENTIAL_BYTE){
            sentenielOffsets.append(offset);
            b0=0;
        }
        if(b1== SENTENTIAL_BYTE){
            sentenielOffsets.append(offset+1);
            b1=0;
        }
        if(b2== SENTENTIAL_BYTE){
            sentenielOffsets.append(offset+2);
            b2=0;
        }
        if(b3== SENTENTIAL_BYTE){
            sentenielOffsets.append(offset+3);
            b3=0;
        }
        result.put((byte) ((b0<<6 | b1<<4 | b2<<2 | b3) &0xFF));
        return sentenielOffsets;
    }
    
    
    
	
	
	protected AbstractTwoBitEncodedNucleotideCodec(Nucleotide sententialBase) {
		super(sententialBase);
	}

	@Override
	protected int getNucleotidesPerGroup() {
		return 4;
	}

	

}
