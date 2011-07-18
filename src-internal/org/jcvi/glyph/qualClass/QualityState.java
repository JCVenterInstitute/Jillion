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
 * Created on Feb 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.qualClass;

import java.util.Map;

import static org.jcvi.glyph.qualClass.QualityClass.*;

import org.jcvi.common.core.seq.read.SequenceDirection;
final class QualityState{
    
   
    private boolean zeroCoverage;
    private boolean gapConsensus;
    private boolean hasConflict;
    private boolean hasAmbiguiousConsensus;
    private boolean hasHighQualityAgreement;
    private boolean hasAnotherHighQualityAgreementOtherDirection;
    private boolean hasHighQualityConflict;
    private boolean hasLowQualityConflict;
    private boolean onlySingleCoverageAgreesWithConsensus;
    private boolean hasLowQualityAgreement;
    private boolean hasAnotherLowQualityAgreementSameDirection;
    private boolean hasLowQualityAgreementOtherDirection;
    private boolean hasLowQualityAgreementBothDirections;
    private boolean hasAnotherAgreementInSameDirection;
    
    public static QualityClass getQualityClassFor(QualityClass.Builder builder){
        final QualityState state = new QualityState(builder);
        return state.getQualityClass();
    }
    private QualityState(QualityClass.Builder builder) {
        hasAmbiguiousConsensus = builder.hasAmbiguiousConsensus();
        gapConsensus = builder.isGapConsensus();
        computeIsZeroCoverage(builder);
        computeIfHasConflict(builder);
        computeHasHighQualityAgreement(builder);
        computeHasAnotherHighQualityAgreementInOtherDirection(builder);
        computeHasHighQualityConflict(builder);
        computeHasLowQualityConflict(builder);
        computeHasLowQualityAgreement(builder);
        computeHasAnotherLowQualityAgreementInOtherDirection(builder);
        computeHasAnotherLowQualityAgreementInSameDirection(builder);
        computeHasLowQualityAgreementInBothDirections(builder);
        computeHasAnotherAgreementInSameDirection(builder);
        computeOnlySingleCoverageAgreesWithConsensus(builder);
    }

    private QualityClass getQualityClass(){
        if (zeroCoverage) {
            return ZERO_COVERAGE;
        }
        
        if (hasAmbiguiousConsensus){
            if(onlySingleCoverageAgreesWithConsensus){
                return AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT;
            }
            return AMBIGUIOUS_CONSENSUS;
        }            
        
        if (gapConsensus && !hasConflict) {
            return AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT;
        }
        
        if (!hasHighQualityAgreement && !hasLowQualityAgreement) {
            return MISCALLED_CONSENSUS;
        }
        if (!hasConflict){
            if (onlySingleCoverageAgreesWithConsensus) {
                if (hasLowQualityAgreement){
                    return ONE_X_COVERAGE_LOW_QUAL;
                }
                return ONE_X_COVERAGE_HIGH_QUAL;
            }
            
            if (hasHighQualityAgreement){
                if (hasAnotherHighQualityAgreementOtherDirection) {
                    return NO_CONFLICT_HIGH_QUAL_BOTH_DIRS;
                }
                if (hasLowQualityAgreementOtherDirection) {
                    return NO_CONFLICT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR;
                }
                if (hasAnotherAgreementInSameDirection) {
                    return NO_CONFLICT_HIGH_QUAL_AND_LOW_QUAL_SAME_DIR;
                }
            }
            
            if (hasLowQualityAgreementBothDirections) {
                return NO_CONFLICT_LOW_QUAL_BOTH_DIR;
            }
            if (hasAnotherLowQualityAgreementSameDirection) {
                return NO_CONFLICT_2_LOW_QUAL_SAME_DIR;
            }
            
        }
        
        
        if (hasHighQualityConflict){
            
            if (hasHighQualityAgreement){
                if (hasAnotherHighQualityAgreementOtherDirection) {
                    return HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRS_AGREE;
                }
                if (hasLowQualityAgreementOtherDirection) {
                    return HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE;
                }
                if (hasAnotherAgreementInSameDirection) {
                    return HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE;
                }
                if (onlySingleCoverageAgreesWithConsensus) {
                    return TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE;
                } 
            }
            
            if (hasLowQualityAgreementOtherDirection) {
                return HIGH_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRS_AGREE;
            }
            if (hasAnotherLowQualityAgreementSameDirection) {
                return HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE;
            }
                       
            
            if (hasLowQualityAgreement  && onlySingleCoverageAgreesWithConsensus) {
                return HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE;
            }
        }
        
        if (hasLowQualityConflict){
            
            if (hasHighQualityAgreement){
                if (hasAnotherHighQualityAgreementOtherDirection) {
                    return LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRECTIONS_AGREE;
                }
                if (hasLowQualityAgreementOtherDirection) {
                    return LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE;
                }
                if (hasAnotherAgreementInSameDirection) {
                    return LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE;
                }
                if (onlySingleCoverageAgreesWithConsensus) {
                    return TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE;
                }
            }
            
            if (hasLowQualityAgreementOtherDirection) {
                return LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE;
            }
            
            if (hasAnotherLowQualityAgreementSameDirection) {
                return LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE;
            }
            
            if (hasLowQualityAgreement && onlySingleCoverageAgreesWithConsensus) {
                return TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE;
            }
        }
        return ZERO_COVERAGE;
    }
    private void computeIsZeroCoverage(QualityClass.Builder builder){
        this.zeroCoverage = builder.getNumberOfReads() ==0;
    }
    private void computeIfHasConflict(QualityClass.Builder builder){
        hasConflict = hasAConflict(builder.getHighQualityConflictMap()) || hasAConflict(builder.getLowQualityConflictMap()); 
    }

    private boolean hasAConflict(Map<SequenceDirection, Integer> conflictMap) {
      return  hasAnyReads(conflictMap,SequenceDirection.FORWARD ) || hasAnyReads(conflictMap,SequenceDirection.REVERSE );
    }
    private boolean hasNoReads(Map<SequenceDirection, Integer> map, SequenceDirection direction){
        return map.get(direction).intValue() == 0;
    }
    private boolean hasAnyReads(Map<SequenceDirection, Integer> map, SequenceDirection direction){
        return !hasNoReads(map, direction);
    }
    private boolean hasMoreThanOneRead(Map<SequenceDirection, Integer> map, SequenceDirection direction){
        return map.get(direction).intValue()>1;
    }
    private void computeHasHighQualityAgreement(QualityClass.Builder builder){
        final Map<SequenceDirection, Integer> highQualityAgreementMap = builder.getHighQualityAgreementMap();
        hasHighQualityAgreement = hasAnyReads(highQualityAgreementMap,SequenceDirection.FORWARD ) ||
                                    hasAnyReads(highQualityAgreementMap,SequenceDirection.REVERSE );
    }

    private void computeHasAnotherHighQualityAgreementInOtherDirection(QualityClass.Builder builder){
        final Map<SequenceDirection, Integer> highQualityAgreementMap = builder.getHighQualityAgreementMap();
        hasAnotherHighQualityAgreementOtherDirection = hasAnyReads(highQualityAgreementMap,SequenceDirection.FORWARD ) &&
                                    hasAnyReads(highQualityAgreementMap,SequenceDirection.REVERSE );
    }
    
    private void computeHasLowQualityAgreementInBothDirections(QualityClass.Builder builder){
        hasLowQualityAgreementBothDirections = hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD ) &&
        hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE );
    }
    private void computeHasHighQualityConflict(QualityClass.Builder builder){
        hasHighQualityConflict = hasAnyReads(builder.getHighQualityConflictMap(),SequenceDirection.FORWARD ) ||
                                    hasAnyReads(builder.getHighQualityConflictMap(),SequenceDirection.REVERSE );
    }
    private void computeHasLowQualityConflict(QualityClass.Builder builder){
        hasLowQualityConflict = hasAnyReads(builder.getLowQualityConflictMap(),SequenceDirection.FORWARD ) ||
                                    hasAnyReads(builder.getLowQualityConflictMap(),SequenceDirection.REVERSE );
    }
    private void computeHasLowQualityAgreement(QualityClass.Builder builder){
        hasLowQualityAgreement = hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD ) ||
                                    hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE );
    }
    private void computeOnlySingleCoverageAgreesWithConsensus(QualityClass.Builder builder){
        onlySingleCoverageAgreesWithConsensus = builder.getNumberOfAgreeingReads()==1;
    }
    private void computeHasAnotherLowQualityAgreementInOtherDirection(QualityClass.Builder builder){
        hasLowQualityAgreementOtherDirection =
            (hasAnyReads(builder.getHighQualityAgreementMap(),SequenceDirection.FORWARD ) && hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE )) ||
        (hasAnyReads(builder.getHighQualityAgreementMap(),SequenceDirection.REVERSE ) && hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD )) ||
        hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD ) && hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE );
    }

    private void computeHasAnotherLowQualityAgreementInSameDirection(QualityClass.Builder builder){
        hasAnotherLowQualityAgreementSameDirection = hasMoreThanOneRead(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD ) ||
                                            hasMoreThanOneRead(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE );
    }
    
    private void computeHasAnotherAgreementInSameDirection(QualityClass.Builder builder) {
        hasAnotherAgreementInSameDirection = (hasAnyReads(builder.getHighQualityAgreementMap(),SequenceDirection.FORWARD ) &&
                            hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.FORWARD )) ||
                            (hasAnyReads(builder.getHighQualityAgreementMap(),SequenceDirection.REVERSE ) &&
                                    hasAnyReads(builder.getLowQualityAgreementMap(),SequenceDirection.REVERSE )) ||
                                    hasMoreThanOneRead(builder.getHighQualityAgreementMap(), SequenceDirection.FORWARD) ||
                                    hasMoreThanOneRead(builder.getHighQualityAgreementMap(), SequenceDirection.REVERSE) ||
                                    hasMoreThanOneRead(builder.getLowQualityAgreementMap(), SequenceDirection.FORWARD) ||
                                    hasMoreThanOneRead(builder.getLowQualityAgreementMap(), SequenceDirection.REVERSE)
                            ;
    }


   
    
}
