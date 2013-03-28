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
package org.jcvi.jillion.trace.fastq;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.qual.PhredQuality;

/**
 * {@code SolexaUtil} is a utility class for working with 
 * Solexa formatted fastq data.
 * @author dkatzel
 * @see <a href="http://nar.oxfordjournals.org/cgi/content/full/38/6/1767"
 * >Cock et al (2009) The Sanger FASTQ file format for sequences with quality scores, and the Solexa/Illumina FASTQ variants. Nucleic Acids Research</a>
 *
 */
final class SolexaUtil {
    /**
     * 
     */
    private static final double TEN = 10.0;
    /**
     * Cache of solexa to phred quality mappings so we
     * only have to perform the expensive calculations once.
     */
    private static final Map<Integer, PhredQuality> SOLEXA_2_PHRED_MAP;
    /**
     * Cache of phred  to solexa quality mappings so we
     * only have to perform the expensive calculations once.
     */
    private static final Map<PhredQuality, Integer> PHRED_2_SOLEXA_MAP;
    /**
     * Populate the caches.
     */
    static{
        SOLEXA_2_PHRED_MAP = new HashMap<Integer, PhredQuality>();
        PHRED_2_SOLEXA_MAP = new HashMap<PhredQuality, Integer>();
        for(int solexaValue=-5; solexaValue<=62; solexaValue++){
            PhredQuality phred = privateConvertSolexaQualityToPhredQuality(solexaValue);
            SOLEXA_2_PHRED_MAP.put(solexaValue, phred);           
        }
        //do the phred calcuations separately because
        //there isn't a 1:1 mapping
        for(byte i=0; i<PhredQuality.MAX_VALUE; i++){
            PhredQuality phred = PhredQuality.valueOf(i);
            int solexaValue = privateConvertPhredQualityToSolexaQuality(phred);
            PHRED_2_SOLEXA_MAP.put(phred, solexaValue);
        }
    }
    private SolexaUtil(){}
    /**
     * Convert a solexa quality value into the more common
     * Phred Quality equivalent.  Since Solexa values do not use the same
     * scale as Phred qualities, multiple low solexa values map to 
     * the same Phred value.  Therefore, it is not possible to always convert
     * a solexa value into a quality value and then back into the original solexa
     * value.
     * @param solexaQuality the solexa quality value to convert.
     * @return a {@link PhredQuality} equivalent.
     */
    private static PhredQuality privateConvertSolexaQualityToPhredQuality(int solexaQuality){
        if(solexaQuality ==-5){
            return PhredQuality.valueOf(0);
        }
        if(solexaQuality ==-4){
            return PhredQuality.valueOf(1);
        }
        if(solexaQuality ==-3 ||solexaQuality ==-2){
            return PhredQuality.valueOf(2);
        }
        if(solexaQuality ==-1){
            return PhredQuality.valueOf(3);
        }
        final double math = TEN * Math.log(1 + Math.pow(TEN, solexaQuality/TEN))/Math.log(TEN);
        return PhredQuality.valueOf((int)Math.round(math));
    }
    /**
     * Convert a {@link PhredQuality}value into the Solexa
     * equivalent.  Since Solexa values do not use the same
     * scale as Phred qualities, multiple low solexa values map to 
     * the same Phred value.  Therefore, it is not possible to always convert
     * a solexa value into a quality value and then back into the original solexa
     * value.
     * @param phredQuality the {@link PhredQuality} to convert.
     * @return the Solexa quality equivalent.
     */
    private static int privateConvertPhredQualityToSolexaQuality(PhredQuality phredQuality){
        
        final byte qualityValue = phredQuality.getQualityScore();
        if(qualityValue ==0){
            return -5;
        }
        if(qualityValue ==1){
            return -4;
        }
        if(qualityValue ==2){
            return -2;
        }
        double math=TEN * Math.log(Math.pow(TEN, qualityValue/TEN) -1)/Math.log(TEN);
        return (int)Math.round(math);
    }
    /**
     * Convert a solexa quality value into the more common
     * Phred Quality equivalent.  Since Solexa values do not use the same
     * scale as Phred qualities, multiple low solexa values map to 
     * the same Phred value.  Therefore, it is not possible to always convert
     * a solexa value into a quality value and then back into the original solexa
     * value.
     * @param solexaQuality the solexa quality value to convert.
     * @return a {@link PhredQuality} equivalent.
     */
    public static PhredQuality convertSolexaQualityToPhredQuality(int solexaQuality){
        return SOLEXA_2_PHRED_MAP.get(solexaQuality);
    }
    /**
     * Convert a {@link PhredQuality}value into the Solexa
     * equivalent.  Since Solexa values do not use the same
     * scale as Phred qualities, multiple low solexa values map to 
     * the same Phred value.  Therefore, it is not possible to always convert
     * a solexa value into a quality value and then back into the original solexa
     * value.
     * @param phredQuality the {@link PhredQuality} to convert.
     * @return the Solexa quality equivalent.
     */
    public static int convertPhredQualityToSolexaQuality(PhredQuality phredQuality){
        
        return PHRED_2_SOLEXA_MAP.get(phredQuality);
    }
}
