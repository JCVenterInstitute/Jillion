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
 * Created on Jan 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.qualClass;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.common.core.seq.Glyph;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.read.SequenceDirection;

public enum QualityClass implements Glyph, Comparable<QualityClass>{

    
    ZERO_COVERAGE(0),
    /**
     * QualityClass 1.
     */
    NO_CONFLICT_HIGH_QUAL_BOTH_DIRS(1),
    /**
     * QualityClass 2.
     */
    NO_CONFLICT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR(2),
    /**
     * QualityClass 3.
     */
    NO_CONFLICT_HIGH_QUAL_AND_LOW_QUAL_SAME_DIR(3),
    /**
     * QualityClass 4.
     */
    NO_CONFLICT_LOW_QUAL_BOTH_DIR(4),
    /**
     * QualityClass 5.
     */
    NO_CONFLICT_2_LOW_QUAL_SAME_DIR(5),
    /**
     * QualityClass 6.
     */
    LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRECTIONS_AGREE(6),
    /**
     * QualityClass 7.
     */
    LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE(7),
    /**
     * QualityClass 8.
     */
    LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE(8),
    /**
     * QualityClass 9.
     */
    ONE_X_COVERAGE_HIGH_QUAL(9),
    /**
     * QualityClass 10.
     */
    LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE(10),
    /**
     * QualityClass 11.
     */
    ONE_X_COVERAGE_LOW_QUAL(11),
    /**
     * QualityClass 12.
     */
    LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE(12),
    /**
     * QualityClass 13.
     */
    TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE(13),
    /**
     * QualityClass 14.
     */
    TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE(14),
    /**
     * QualityClass 15.
     */
    HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRS_AGREE(15),
    /**
     * QualityClass 16.
     */
    HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE(16),
    /**
     * QualityClass 17.
     */
    HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE(17),
    /**
     * QualityClass 18.
     */
    HIGH_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRS_AGREE(18),
    /**
     * QualityClass 19.
     */
    HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE(19),
    /**
     * QualityClass 20.
     */
    TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE(20),
    /**
     * QualityClass 21.
     */
    AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT(21),
    /**
     * QualityClass 22.
     */
    AMBIGUIOUS_CONSENSUS(22),
    /**
     * QualityClass 23.
     */
    HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE(23),
    /**
     * Quality class 23 actually has 2 use cases
     * I have broken it into 2 separate enums
     * but {@link HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE}
     * will be the canonical one which should be
     * returned by any 1:1 enum to value mapping.
     */
    MISCALLED_CONSENSUS(23)
    ;
    private static final Map<Byte, QualityClass> QUALITY_CLASS_BY_VALUE;
    
    static{
        QUALITY_CLASS_BY_VALUE = new HashMap<Byte, QualityClass>();
        for(QualityClass qualityClass : QualityClass.values()){
            QUALITY_CLASS_BY_VALUE.put(Byte.valueOf(qualityClass.value), qualityClass);
        }
        //force
        QUALITY_CLASS_BY_VALUE.put(Byte.valueOf(HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE.value),
                HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE);
    }
    
    public static QualityClass valueOf(Number value){
        Byte byteValue = value.byteValue();
        if(QUALITY_CLASS_BY_VALUE.containsKey(byteValue)){
            return QUALITY_CLASS_BY_VALUE.get(byteValue);
        }
        throw new IllegalArgumentException("unknown quality class value: " +value);
    }
    private final byte value;
 
    private QualityClass(int value){
        this.value = (byte)value;
    }

    public final byte getValue() {
        return value;
    }
    
    
    
    @Override
    public String toString() {
        
        return this.name() + " ( "+ getValue() + " )";
    }



    public static class Builder{
        private final boolean gapConsensus;
        private final boolean hasAmbiguiousConsensus;
        private static final Integer ZERO = Integer.valueOf(0);
        private boolean built;
        private Map<SequenceDirection, Integer> highQualityAgreementMap;
        private Map<SequenceDirection, Integer> highQualityConflictMap;        
        private Map<SequenceDirection, Integer> lowQualityAgreementMap;
        private Map<SequenceDirection, Integer> lowQualityConflictMap;
        private int numberOfAgreeingReads;
        private int numberOfReads;
        private final PhredQuality highQualitythreshold;
        
        public Builder(NucleotideGlyph consensusGlyph,PhredQuality highQualitythreshold){
            gapConsensus = consensusGlyph.isGap();
            hasAmbiguiousConsensus = consensusGlyph.isAmbiguity();
            this.highQualitythreshold = highQualitythreshold;
            createAndInitializeMaps();            
        }
        public Builder(NucleotideGlyph consensus, PhredQuality highQualitythreshold, Slice slice){
            this(consensus,highQualitythreshold);
            for(SliceElement sliceElement : slice){
                if(isHighQuality(sliceElement.getQuality())){
                    handleHighQualitySliceElement(sliceElement,consensus);
                }
                else{
                    handleLowQualitySliceElement(sliceElement,consensus);
                }
                
            }
            
        }
        private void handleLowQualitySliceElement(SliceElement sliceElement,
                NucleotideGlyph consensus) {
            if(sliceElement.getBase() == consensus ){
                addLowQualityAgreement(sliceElement.getSequenceDirection());
            }
            else{
                addLowQualityConflict(sliceElement.getSequenceDirection());
            }
            
        }
        private void handleHighQualitySliceElement(SliceElement sliceElement,NucleotideGlyph consensus) {
            if(sliceElement.getBase() == consensus ){
                addHighQualityAgreement(sliceElement.getSequenceDirection());
            }
            else{
                addHighQualityConflict(sliceElement.getSequenceDirection());
            }
            
        }
        private boolean isHighQuality(PhredQuality quality){
            return highQualitythreshold.compareTo(quality)<=0;
        }
        private void createAndInitializeMaps() {
            highQualityAgreementMap = new EnumMap<SequenceDirection, Integer>(SequenceDirection.class);
            highQualityConflictMap = new EnumMap<SequenceDirection, Integer>(SequenceDirection.class);
            lowQualityAgreementMap = new EnumMap<SequenceDirection, Integer>(SequenceDirection.class);
            lowQualityConflictMap = new EnumMap<SequenceDirection, Integer>(SequenceDirection.class);
            
            initializeMap(highQualityAgreementMap);
            initializeMap(highQualityConflictMap);
            initializeMap(lowQualityAgreementMap);
            initializeMap(lowQualityConflictMap);
        }

        private void initializeMap(Map<SequenceDirection, Integer> map) {
            map.put(SequenceDirection.FORWARD, ZERO);
            map.put(SequenceDirection.REVERSE, ZERO);
        }
        public final boolean isGapConsensus() {
            return gapConsensus;
        }

        public final boolean hasAmbiguiousConsensus() {
            return hasAmbiguiousConsensus;
        }


        public final Map<SequenceDirection, Integer> getHighQualityAgreementMap() {
            return highQualityAgreementMap;
        }

        public final Map<SequenceDirection, Integer> getHighQualityConflictMap() {
            return highQualityConflictMap;
        }

        public final Map<SequenceDirection, Integer> getLowQualityAgreementMap() {
            return lowQualityAgreementMap;
        }


        public final Map<SequenceDirection, Integer> getLowQualityConflictMap() {
            return lowQualityConflictMap;
        }


        public final int getNumberOfAgreeingReads() {
            return numberOfAgreeingReads;
        }

        public final int getNumberOfReads() {
            return numberOfReads;
        }
        
        public synchronized Builder addHighQualityAgreement(SequenceDirection direction){
            increment(highQualityAgreementMap, direction);
            numberOfAgreeingReads++;            
            return this;
        }
        public synchronized Builder addHighQualityConflict(SequenceDirection direction){
            increment(highQualityConflictMap, direction);
            return this;
        }
        public synchronized Builder addLowQualityAgreement(SequenceDirection direction){
            increment(lowQualityAgreementMap, direction);
            numberOfAgreeingReads++;
            return this;
        }
        public synchronized Builder addLowQualityConflict(SequenceDirection direction){
            increment(lowQualityConflictMap, direction);
            return this;
        }
        
        private void increment(Map<SequenceDirection, Integer> map, SequenceDirection direction) {
            map.put(direction, Integer.valueOf(map.get(direction).intValue()+1));  
            numberOfReads++;
        }

     
        public synchronized QualityClass build() {
            if(built){
                throw new IllegalStateException("already built");
            }
            built = true;
            return QualityState.getQualityClassFor(this);            
        }
    }

    @Override
    public String getName() {
        return toString();
    }
    
    public String getCode(){
        return "QC"+value;
    }
    
    
}
