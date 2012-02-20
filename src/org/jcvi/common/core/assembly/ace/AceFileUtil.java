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

package org.jcvi.common.core.assembly.ace;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * {@code AceFileUtil} is a utility class to perform 
 * common operations on ace related objects.
 * @author dkatzel
 *
 *
 */
public class AceFileUtil {
    /**
     * This is the default value in consed that is used to distinguish
     * between high and low quality basecalls.  In consed
     * high quality bases are represented by uppercase
     * letters and low quality by lowercase letters.
     * Currently that value is set to 26.
     * @return a {@link PhredQuality} object for quality 26.
     */
    public static final PhredQuality ACE_DEFAULT_HIGH_QUALITY_THRESHOLD = PhredQuality.valueOf(26);
    /**
     * The date format used in consed to represent chromatogram time stamps 
     * in phd records as well as the DS lines in an ace file.
     * A read's timestamps must be identical strings in both the phd
     * and the ace for consed to make the read editable and to see the qualities
     * in the align window.
     */
    public static final DateTimeFormatter CHROMAT_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM d HH:mm:ss yyyy");
    /**
     * This is the timestamp format used in some consed 
     * tags.
     */
    public static final DateTimeFormatter TAG_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("YYMMdd:HHmmss");
    /**
     * Convert a {@link NucleotideSequence} into a string
     * where the gaps are represented by '*'s like ace files require.
     * @param basecalls input basecalls, can not be null.
     * @return a String with the all uppercase basecalls except all the '-'
     * have been replaced by '*'.
     * @throws NullPointerException if basecalls are null.
     */
    public static String convertToAcePaddedBasecalls(NucleotideSequence basecalls){
        return convertToAcePaddedBasecalls(basecalls.asList(),null);
     }
    /**
     * Convert a {@link List} of {@link Nucleotides} into a string
     * where the gaps are represented by '*'s like ace files require.
     * If the optional qualities list is provided, then the returned 
     * String will return a basecalls in both upper and lowercase
     * depending on the quality value as determined by {@link AceFileUtil#ACE_DEFAULT_HIGH_QUALITY_THRESHOLD}.
     * 
     * @param basecalls input basecalls, can not be null or contain any null elements.
     * @param optionalQualities optional ungapped quality list in the same
     * orientation as the basecalls; or null if no qualities are to be used.
     * @return a String with the all uppercase basecalls except all the '-'
     * have been replaced by '*'.
     * @throws NullPointerException if basecalls are null or any Nucleotide in the basecall list 
     * is null or if the optionalQualities list is not null but contains a null in the list.
     * @throws IllegalArgumentException if optionalQualities is provided but does not have 
     * enough ungapped qualities to cover all the ungapped basecalls in the input list.
     */
     public static String convertToAcePaddedBasecalls(List<Nucleotide> basecalls,List<PhredQuality> optionalQualities){
         StringBuilder result = new StringBuilder();
         int numberOfGapsSoFar=0;
         
         for(int i=0; i< basecalls.size(); i++){
             Nucleotide base = basecalls.get(i);
             if(base ==null){
                 throw new NullPointerException(String.format("%d th basecall is null", i));
             }
             if(base == Nucleotide.Gap){
                 result.append("*");
                 numberOfGapsSoFar++;
             }
             else{
                 if(optionalQualities!=null){
                     int offset = i- numberOfGapsSoFar;
                     if(optionalQualities.size()<=offset){
                         throw new IllegalArgumentException(
                                 String.format("not enough ungapped qualities for input basecalls found only %d qualities",offset));
                     }
                     PhredQuality quality =optionalQualities.get(offset);
                     if(quality.compareTo(ACE_DEFAULT_HIGH_QUALITY_THRESHOLD)<0){
                         result.append(base.toString().toLowerCase(Locale.ENGLISH));
                     }
                     else{
                         result.append(base);
                     }
                 }else{
                     result.append(base);
                 }
             }
         }
         
         String consedBasecalls= result.toString().replaceAll("(.{50})", "$1"+String.format("%n"));
         if(basecalls.size() %50 ==0){
             //if the last line is full, then we will have an extra %n
             //so strip it off
             return consedBasecalls.substring(0,consedBasecalls.length()-1);
         }
         return consedBasecalls;
     }

    
    private static String createPhdRecord(PhdInfo phdInfo){
        return String.format("DS CHROMAT_FILE: %s PHD_FILE: %s TIME: %s", 
                
                phdInfo.getTraceName(),
                phdInfo.getPhdName(),
                AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.print(phdInfo.getPhdDate().getTime())
                );
    }
    
    private static String createQualityRangeRecord(NucleotideSequence gappedValidBases, 
            Range ungappedValidRange, Direction dir, long ungappedFullLength){
        int numberOfGaps = gappedValidBases.getNumberOfGaps();
        Range gappedValidRange =buildGappedValidRangeFor(
                ungappedValidRange,numberOfGaps,dir,ungappedFullLength);

        
       return String.format("QA %d %d %d %d",
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd(),
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd()
                );
    }
    private static Range buildGappedValidRangeFor(Range ungappedValidRange, int numberOfGaps,Direction dir, long ungappedFullLength){
       Range gappedValidRange=  Range.buildRange( 
               ungappedValidRange.getStart(),
               ungappedValidRange.getEnd()+numberOfGaps);
        
        if(dir==Direction.REVERSE){
            gappedValidRange = AssemblyUtil.reverseComplimentValidRange(gappedValidRange, ungappedFullLength+numberOfGaps);
           
        }
        return gappedValidRange.convertRange(CoordinateSystem.RESIDUE_BASED);
    }
    public static String createAcePlacedReadRecord(String readId, PlacedRead placedRead, Phd phd, PhdInfo phdInfo){
        
        NucleotideSequence nucleotideSequence = placedRead.getNucleotideSequence();
		final NucleotideSequence gappedValidBasecalls = nucleotideSequence; 
        final Range ungappedValidRange = placedRead.getValidRange();
        final Direction dir = placedRead.getDirection(); 
        final NucleotideSequence fullBasecalls = phd.getBasecalls();
        final List<Nucleotide> phdFullBases = fullBasecalls.asList();
        
        final List<Nucleotide> fullGappedValidRange = AssemblyUtil.buildGappedComplimentedFullRangeBases(placedRead, phdFullBases);
        final List<PhredQuality> qualities =phd.getQualities().asList();  
        if(qualities.isEmpty()){
            throw new IllegalStateException("empty qualities for read "+ readId);
        }
        if(dir == Direction.REVERSE){
            Collections.reverse(qualities);            
        }
        StringBuilder readRecord = new StringBuilder();
        readRecord.append(String.format("RD %s %d 0 0%n",
                                            readId,
                                            fullGappedValidRange.size()));
        
        
        readRecord.append(String.format("%s%n%n",
                AceFileUtil.convertToAcePaddedBasecalls(fullGappedValidRange,qualities)));
        readRecord.append(String.format("%s%n",createQualityRangeRecord(
                gappedValidBasecalls,ungappedValidRange,dir, 
                fullBasecalls.getUngappedLength())));
        readRecord.append(String.format("%s%n",createPhdRecord(phdInfo)));
        return readRecord.toString();
    }
    
}
