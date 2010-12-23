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

import java.util.regex.Pattern;

import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * {@code FastQUtil} is a utility class for working with 
 * FASTQ data.
 * @author dkatzel
 *
 *
 */
public final class FastQUtil {

    private FastQUtil(){}
    /**
     * This is the {@link Pattern} to parse
     * the sequence record defline of a FASTQ record.
     * Group 1 will be the read id
     * Group 3 will be the optional comment if there is one,
     * or null if there isn't a comment.
     */
    public static final Pattern SEQ_DEFLINE_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    /**
     * This is the {@link Pattern} to parse
     * the quality record defline of a FASTQ record.
     * Group 1 will be the optional id of the read if there is one
     * or null if there isn't an id.  If the id exists,
     * then it should match the id of the seq defline.
     */
    public static final Pattern QUAL_DEFLINE_PATTERN = Pattern.compile("^\\+(.+$)?");
   
    /**
     * Encode the given {@link FastQRecord} into FastQ format using the given
     * {@link FastQQualityCodec}.  This is the same as 
     * {@link #encode(FastQRecord, FastQQualityCodec, boolean) encode(fastQRecord,qualityCodec,false)}
     * @return a multiline string which is the fastq encoded version of the
     * given FastQRecord.
     */
    public static String encode(FastQRecord fastQRecord, FastQQualityCodec qualityCodec){
       return encode(fastQRecord,qualityCodec,false);
    }
    /**
     * Encode the given {@link FastQRecord} into FastQ format using the given
     * {@link FastQQualityCodec}.
     * @param fastQRecord the record to encode.
     * @param qualityCodec the {@link FastQQualityCodec} to use to encode
     * the qualities.
     * @param should the read id be written (again) on the qualities line,
     * many fastq formats no longer duplicate the id on the quality line to
     * save space.
     * @return a multiline string which is the fastq encoded version of the
     * given FastQRecord.
     */
    public static String encode(FastQRecord fastQRecord, FastQQualityCodec qualityCodec, boolean writeIdOnQualityLine){
        String id = fastQRecord.getId();
        boolean hasComment = fastQRecord.getComment() !=null;
        
        StringBuilder builder = new StringBuilder("@").append(id);
        if(hasComment){
            builder.append(" ").append(fastQRecord.getComment());
        }
        builder.append("\n")
        .append(NucleotideGlyph.convertToString(fastQRecord.getNucleotides().decode())).append("\n")
        .append("+");
        if(writeIdOnQualityLine){
            builder.append(id);
        }
        builder.append("\n").append(qualityCodec.encode(fastQRecord.getQualities())).append("\n");
        return builder.toString();
    }
}
