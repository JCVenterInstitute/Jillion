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

package org.jcvi.fasta.fastq.solexa;

import org.jcvi.glyph.phredQuality.PhredQuality;

/**
 * {@code SolexaUtil} is a utility class for working with 
 * Solexa formatted FASTQ data.
 * @author dkatzel
 * @see <a href="http://nar.oxfordjournals.org/cgi/content/full/38/6/1767"
 * >Cock et al (2009) The Sanger FASTQ file format for sequences with quality scores, and the Solexa/Illumina FASTQ variants. Nucleic Acids Research</a>
 *
 */
public final class SolexaUtil {

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
    public static PhredQuality convertSolexaQualityToPhredQuality(int solexaQuality){
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
        final double math = 10 * Math.log(1 + Math.pow(10, solexaQuality/10.0))/Math.log(10);
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
    public static int convertPhredQualityToSolexaQuality(PhredQuality phredQuality){
        
        final byte qualityValue = phredQuality.getNumber().byteValue();
        if(qualityValue ==0){
            return -5;
        }
        if(qualityValue ==1){
            return -4;
        }
        if(qualityValue ==2){
            return -2;
        }
        double math=10 * Math.log(Math.pow(10, qualityValue/10.0) -1)/Math.log(10);
        return (int)Math.round(math);
    }
}
