/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * Utility class for working with sff
 * encoded data.
 * @author dkatzel
 *
 */
public final class SffUtil {
   /**
     * {@value}
     */
    private static final float ONE_HUNDRED = 100F;
    /**
     * This is the magic number all SFF files
     * must start with to be recognized as sff 
     * binary files.
     */
    static final byte[] SFF_MAGIC_NUMBER = new byte[]{
        0x2E,
        0x73,
        0x66,
        0x66,
        0,
        0,
        0,
        1,
    };
    
    /**
	 * 255 ^ 3 = {@value}.
	 */
	private static final long POW_3 = 16581375;
	/**
	 * 255 ^ 2 = {@value}.
	 */
	private static final long POW_2 = 65025;
	/**
	 * 255 ^ 1 = {@value}.
	 */
	private static final long POW_1 = 255;
    /**
     * Currently SFF only has 1 format code which has a value of <code>1</code>.
     */
    public static final byte FORMAT_CODE = 1;
    /**
     * If a clip point is not set,
     * then this is the default value
     * for an empty clip specified by the SFF
     * format.
     */
    static final byte[] EMPTY_CLIP_BYTES = new byte[]{0,0,0,0};
    
    public static final Range EMPTY_CLIP = Range.of(CoordinateSystem.RESIDUE_BASED, -1, -1);
    public static final Pattern SFFINFO_ENCODED_FLOWGRAM_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)");
   
    private SffUtil(){}
   /**
    * Mated Sff reads contain both mate data in the same
    * read with a "linker" sequence in between. 
    * {@code Linkers} contains the common linkers
    * used by 454 machines.
    * @author dkatzel
    *
    *
    */
   public static enum Linkers{
       /**
        * The Linker sequence used by 454 FLX machines.
        */
       FLX("GTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAAC"),
       /**
        * The linker sequence used by Titanium machines.
        */
       TITANIUM("TCGTATAACTTCGTATAATGTATGCTATACGAAGTTATTACG")
       ;
       
       private final NucleotideSequence forwardSequence;
       private final NucleotideSequence reverseSequence;

    /**
     * @param sequence
     */
    private Linkers(String sequence) {
    	NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(sequence);
        this.forwardSequence = builder.build();
        this.reverseSequence = builder.reverseComplement().build();
    }

    public NucleotideSequence getForwardSequence() {
        return forwardSequence;
    }

    public NucleotideSequence getReverseSequence() {
        return reverseSequence;
    }

   
       
       
   }
   /**
    * Parse the byte array whose first 4 bytes
    * contain an encoded 454 sff file index offset
    * for a single read.
    * @param values
    * @return the file offset into the sff file
    * for the read.
    */
   public static long parseSffIndexOffsetValue(byte[] values){
		return IOUtil.toUnsignedByte(values[3]) 
				+ POW_1	* IOUtil.toUnsignedByte(values[2]) 
				+ POW_2	* IOUtil.toUnsignedByte(values[1])
				+ POW_3	* IOUtil.toUnsignedByte(values[0]);
	}
   
   
   /**
    * Convert a file offset into P encoded 454 sff file index offset
    * for a single read.
    * @param offset the 454 byte offset to convert into a byte array.
    * @return the file offset into the sff file
    * for the read.
    */
   public static byte[] toSffIndexOffsetValue(long offset){
	   long currentOffset = offset;
	   byte[] values = new byte[4];
	   short place4 = (short)(currentOffset / POW_3);
	   values[0] = IOUtil.toSignedByte(place4);
	   currentOffset -= place4 *POW_3;
	   
	   short place3 = (short)(currentOffset / POW_2);
	   values[1] = IOUtil.toSignedByte(place3);
	   currentOffset -= place3 *POW_2;
	   
	   short place2 = (short)(currentOffset / POW_1);
	   values[2] = IOUtil.toSignedByte(place2);
	   currentOffset -= place2*POW_1;
	   
	   values[3] = IOUtil.toSignedByte((short)(currentOffset));

		return values;
	}
   
     public static int caclulatePaddedBytes(int bytesReadInSection){
         final int remainder = bytesReadInSection % 8;
         if(remainder ==0){
             return 0;
         }
        return 8- remainder;
    }
     /**
      * Convert the given encoded flowgram value 
      * into the intensity value
      * @param encodedValue
      * @return
      */
    public static float convertFlowgramValue(short encodedValue){
         return encodedValue / ONE_HUNDRED;
    }
    public static short parseSffInfoEncodedFlowgram(String sffinfoEncodedFlowgram){
        Matcher matcher = SFFINFO_ENCODED_FLOWGRAM_PATTERN.matcher(sffinfoEncodedFlowgram);
        if(matcher.find()){
            return Short.parseShort(matcher.group(1)+ matcher.group(2));
        }
        throw new IllegalArgumentException("could not parse sffinfo encoded flowgram value "+ sffinfoEncodedFlowgram);
    }
    
   
    public static int getReadDataLength(int numberOfFlows, int numberOfBases) {
        return numberOfFlows * 2 + 3*numberOfBases;
        
    }
    
    public static int numberOfIntensities(Iterable<Nucleotide> sequence){
        int count=0;
        Nucleotide currentBase= null;
        for(Nucleotide n : sequence){
            if(currentBase != n){
                currentBase =n;
                count++;
            }
        }
        return count;
        
    }
    /**
     * Compute the trim {@link Range} that should be used
     * for this {@link SffFlowgram}.  This method
     * uses the values from both {@link SffFlowgram#getQualityClip()} 
     * and {@link SffFlowgram#getAdapterClip()} using
     * the algorithm described in the sff file format
     * documentation:
     * <pre>
     * (pseudocode)
     * trimRange =   
     *      Range.of(CoordinateSystem.RESIDUE_BASED,
     *           max(1, max(clip_qual_left, clip_adapter_left)),
     *           min( (clip_qual_right == 0 ? number_of_bases : clip_qual_right), (clip_adapter_right == 0 ? number_of_bases : clip_adapter_right) )
     * </pre>
     * @param flowgram the {@link SffFlowgram} to get the trim range of;
     * can not be null.
     * @return a {@link Range} representing the trimRange to use.
     * @throws NullPointerException if flowgram is null.
     * @see <a href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?cmd=show&f=formats&m=doc&s=formats#sff">
     	sff file specification</a>
     */
    public static Range computeTrimRangeFor(SffFlowgram flowgram){

        return getTrimRangeFor(flowgram.getQualityClip(), flowgram.getAdapterClip(), 
        		flowgram.getNucleotideSequence().getLength());
    }
    
	private static Range getTrimRangeFor(Range qualityClip, Range adapterClip,
			long fullSequenceLength) {
		long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getBegin(CoordinateSystem.RESIDUE_BASED), 
                                adapterClip.getBegin(CoordinateSystem.RESIDUE_BASED)));
        long lastBaseOfInsert = Math.min(
                qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?fullSequenceLength:qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED), 
                        adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?fullSequenceLength:adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        
        return Range.of(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
	}
	 /**
     * Compute the trim {@link Range} that should be used
     * for this {@link SffReadHeader}.  This method
     * uses the values from both {@link SffReadHeader#getQualityClip()} 
     * and {@link SffReadHeader#getAdapterClip()} using
     * the algorithm described in the sff file format
     * documentation:
     * <pre>
     * (pseudocode)
     * trimRange =   
     *      Range.of(CoordinateSystem.RESIDUE_BASED,
     *           max(1, max(clip_qual_left, clip_adapter_left)),
     *           min( (clip_qual_right == 0 ? number_of_bases : clip_qual_right), (clip_adapter_right == 0 ? number_of_bases : clip_adapter_right) )
     * </pre>
     * @param readHeader the {@link SffReadHeader} to get the trim range of;
     * can not be null.
     * @return a {@link Range} representing the trimRange to use.
     * @throws NullPointerException if readHeader is null.
     * @see <a href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?cmd=show&f=formats&m=doc&s=formats#sff">
     	sff file specification</a>
     */
    public static Range computeTrimRangeFor(SffReadHeader readHeader){

        return getTrimRangeFor(readHeader.getQualityClip(), 
        						readHeader.getAdapterClip(), 
    							readHeader.getNumberOfBases());
    }
    
    
    
    
}
