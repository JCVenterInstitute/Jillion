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

package org.jcvi.fasta.fastq;

import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public final class FastQUtil {

    private FastQUtil(){}
    /**
     * Encode the given {@link FastQRecord} into FastQ format using the given
     * {@link FastQQualityCodec}.
     * @param fastQRecord the record to encode.
     * @param qualityCodec the {@link FastQQualityCodec} to use to encode
     * the qualities.
     * @return a multiline string which is the fastq encoded version of the
     * given FastQRecord.
     */
    public static String encode(FastQRecord fastQRecord, FastQQualityCodec qualityCodec){
        String id = fastQRecord.getId();
        boolean hasComment = fastQRecord.getComment() !=null;
        
        StringBuilder builder = new StringBuilder("@").append(id);
        if(hasComment){
            builder.append(" ").append(fastQRecord.getComment());
        }
        builder.append("\n")
        .append(NucleotideGlyph.convertToString(fastQRecord.getNucleotides().decode())).append("\n")
        .append("+").append(id).append("\n")
        .append(qualityCodec.encode(fastQRecord.getQualities())).append("\n");
        return builder.toString();
    }
}
