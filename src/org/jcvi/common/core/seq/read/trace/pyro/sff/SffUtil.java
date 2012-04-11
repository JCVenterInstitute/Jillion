/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

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
        this.reverseSequence = builder.reverseCompliment().build();
    }

    public NucleotideSequence getForwardSequence() {
        return forwardSequence;
    }

    public NucleotideSequence getReverseSequence() {
        return reverseSequence;
    }

   
       
       
   }
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
   
   public static final Range EMPTY_CLIP = Range.create(CoordinateSystem.RESIDUE_BASED, -1, -1);
   public static final Pattern SFFINFO_ENCODED_FLOWGRAM_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)");
    public static int caclulatePaddedBytes(int bytesReadInSection){
         final int remainder = bytesReadInSection % 8;
         if(remainder ==0){
             return 0;
         }
        return 8- remainder;
    }

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
    public static int getReadDataLengthIncludingPadding(int numberOfFlows, int numberOfBases) {
        int lengthWithoutPadding = getReadDataLength(numberOfFlows, numberOfBases);
        int padding= SffUtil.caclulatePaddedBytes(lengthWithoutPadding);
        return lengthWithoutPadding+padding;
    }
    
    public static int numberOfIntensities(List<Nucleotide> glyphs){
        int count=0;
        Nucleotide currentBase= null;
        for(Nucleotide glyph : glyphs){
            if(currentBase != glyph){
                currentBase =glyph;
                count++;
            }
        }
        return count;
        
    }
    
    public static Range getTrimRangeFor(Flowgram flowgram){
        Range qualityClip = flowgram.getQualityClip();
        Range adapterClip = flowgram.getAdapterClip();
        long numberOfBases = flowgram.getBasecalls().getLength();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getBegin(CoordinateSystem.RESIDUE_BASED), 
                                adapterClip.getBegin(CoordinateSystem.RESIDUE_BASED)));
        long lastBaseOfInsert = Math.min(
                qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED), 
                        adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        
        return Range.create(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    public static Range getTrimRangeFor(SffReadHeader readHeader){
        Range qualityClip = readHeader.getQualityClip();
        Range adapterClip = readHeader.getAdapterClip();
        long numberOfBases = readHeader.getNumberOfBases();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getBegin(CoordinateSystem.RESIDUE_BASED), 
                                adapterClip.getBegin(CoordinateSystem.RESIDUE_BASED)));
        long lastBaseOfInsert = Math.min(
                qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED), 
                        adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        
        return Range.create(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    
    
    
  
    
    
}
