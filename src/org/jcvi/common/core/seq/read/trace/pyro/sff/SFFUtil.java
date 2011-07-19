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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.io.IOUtil;


public final class SFFUtil {
   /**
     * 
     */
    private static final float ONE_HUNDRED = 100F;
private SFFUtil(){}
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
        this.forwardSequence = new DefaultNucleotideSequence(sequence);
        this.reverseSequence = new DefaultNucleotideSequence(NucleotideGlyph.reverseCompliment(
                forwardSequence.decode()));
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
   
   public static final Range EMPTY_CLIP = Range.buildRange(CoordinateSystem.RESIDUE_BASED, -1, -1);
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
    public static List<Integer> computeCalledFlowIndexes(SFFReadData readData){
        final byte[] indexes = readData.getFlowIndexPerBase();
        List<Integer> calledIndexes = new ArrayList<Integer>();
        
        int position=-1;
        int i=0;

        while( i < indexes.length){
            if(indexes[i] != 0){
                position+=IOUtil.convertToUnsignedByte(indexes[i]);
                calledIndexes.add(Integer.valueOf(position));
            }
            i++;
        }
        return calledIndexes;
    }
    public static List<Short> computeValues(SFFReadData readData) {
        final byte[] indexes = readData.getFlowIndexPerBase();
        final short[] encodedValues =readData.getFlowgramValues();
        verifyHashEncodedValues(encodedValues);
        return computeValues(indexes, encodedValues);
    }

    private static List<Short> computeValues(final byte[] indexes,
            final short[] encodedValues) {
        List<Short> values = new ArrayList<Short>();
        // positions are 1-based so start with -1 to compensate.
        int position=-1;
        int i=0;

        while( i < indexes.length){
            if(indexes[i] != 0){
                position+=IOUtil.convertToUnsignedByte(indexes[i]);
                values.add(encodedValues[position]);
            }
            i++;
        }

        return Collections.unmodifiableList(values);
    }

    private static void verifyHashEncodedValues(final short[] encodedValues) {
        if(encodedValues ==null || encodedValues.length==0){
            throw new IllegalArgumentException("read data must contain Flowgram values");
        }
    }
    public static int getReadDataLength(int numberOfFlows, int numberOfBases) {
        return numberOfFlows * 2 + 3*numberOfBases;
        
    }
    public static int getReadDataLengthIncludingPadding(int numberOfFlows, int numberOfBases) {
        int lengthWithoutPadding = getReadDataLength(numberOfFlows, numberOfBases);
        int padding= SFFUtil.caclulatePaddedBytes(lengthWithoutPadding);
        return lengthWithoutPadding+padding;
    }
    
    public static int numberOfIntensities(List<NucleotideGlyph> glyphs){
        int count=0;
        NucleotideGlyph currentBase= null;
        for(NucleotideGlyph glyph : glyphs){
            if(currentBase != glyph){
                currentBase =glyph;
                count++;
            }
        }
        return count;
        
    }
    
    public static Range getTrimRangeFor(Flowgram flowgram){
        Range qualityClip = flowgram.getQualitiesClip();
        Range adapterClip = flowgram.getAdapterClip();
        long numberOfBases = flowgram.getBasecalls().getLength();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getLocalStart(), 
                                adapterClip.getLocalStart()));
        long lastBaseOfInsert = Math.min(
                qualityClip.getLocalEnd()==0?numberOfBases:qualityClip.getLocalEnd(), 
                        adapterClip.getLocalEnd()==0?numberOfBases:adapterClip.getLocalEnd());
        
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    public static Range getTrimRangeFor(SFFReadHeader readHeader){
        Range qualityClip = readHeader.getQualityClip();
        Range adapterClip = readHeader.getAdapterClip();
        long numberOfBases = readHeader.getNumberOfBases();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getLocalStart(), 
                                adapterClip.getLocalStart()));
        long lastBaseOfInsert = Math.min(
                qualityClip.getLocalEnd()==0?numberOfBases:qualityClip.getLocalEnd(), 
                        adapterClip.getLocalEnd()==0?numberOfBases:adapterClip.getLocalEnd());
        
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    
    public static SFFFlowgram buildSFFFlowgramFrom(SFFReadHeader readHeader,
            SFFReadData readData) {
        return new SFFFlowgram(
                readHeader.getName(),
                new DefaultNucleotideSequence(
                        NucleotideGlyph.getGlyphsFor(readData.getBasecalls())),
                        new EncodedQualitySequence(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                                PhredQuality.valueOf(readData.getQualities())),
                SFFUtil.computeValues(readData),
                readHeader.getQualityClip(),
                readHeader.getAdapterClip());
    }
    
  
    
    
}
