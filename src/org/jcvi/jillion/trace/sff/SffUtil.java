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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;

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
    
    public static Range getTrimRangeFor(Flowgram flowgram){
        Range qualityClip = flowgram.getQualityClip();
        Range adapterClip = flowgram.getAdapterClip();
        long numberOfBases = flowgram.getNucleotideSequence().getLength();
        long firstBaseOfInsert = Math.max(1,
                        Math.max(qualityClip.getBegin(CoordinateSystem.RESIDUE_BASED), 
                                adapterClip.getBegin(CoordinateSystem.RESIDUE_BASED)));
        long lastBaseOfInsert = Math.min(
                qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED), 
                        adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED)==0?numberOfBases:adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        
        return Range.of(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
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
        
        return Range.of(CoordinateSystem.RESIDUE_BASED, firstBaseOfInsert, lastBaseOfInsert);
    }
    
    
    
    /**
     * Create a new {@link TrimPointsDataStore} which contains
     * trim points of all the reads contained in the given sff file.
     * Each read's trim points will be the clip points set in the sff file.
     * @param sffFile the sffFile to parse.
     * @return a new {@link TrimPointsDataStore}; never null but could be empty
     * if there are no reads in the given sff file.
     * @throws IOException if there is a problem parsing the file.
     */
    public static TrimPointsDataStore createTrimPointsDataStoreFrom(File sffFile) throws IOException{
    	SffTrimDataStoreBuilder builder = new SffTrimDataStoreBuilder();
    	SffFileParser.parse(sffFile, builder);
    	return builder.build();
    }
    
    private static final class SffTrimDataStoreBuilder implements SffFileVisitor, Builder<TrimPointsDataStore>{

        private final Map<String, Range> trimRanges = new LinkedHashMap<String, Range>();
       
       

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
            return CommonHeaderReturnCode.PARSE_READS;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ReadDataReturnCode visitReadData(SffReadData readData) {
            return ReadDataReturnCode.PARSE_NEXT_READ;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
            trimRanges.put(readHeader.getId(), SffUtil.getTrimRangeFor(readHeader));
            return ReadHeaderReturnCode.SKIP_CURRENT_READ;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TrimPointsDataStore build() {
        	return DataStoreUtil.adapt(TrimPointsDataStore.class,trimRanges);
        }

    }
}
