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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PhdWriter {
    private static final String BEGIN_SEQUENCE = "BEGIN_SEQUENCE";
    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern(
    "EEE MMM dd kk:mm:ss yyyy");
    
    public static void writePhd(Phd phd, OutputStream out) throws IOException{
        try{
            StringBuilder phdRecord = new StringBuilder();
            
            phdRecord.append( String.format("%s %s%n%n",BEGIN_SEQUENCE, phd.getId()));
            
            phdRecord.append(createComments(phd));
            phdRecord.append(writeDnaSection(phd));
            phdRecord.append( String.format("%n"));
            phdRecord.append(String.format("%s%n",END_SEQUENCE));
            phdRecord.append(createTags(phd));
            write(out, phdRecord.toString());
        }catch(Throwable t){
            throw new IOException("error writing phd record for "+phd.getId(), t);
        }
        
    }

    private static String createTags(Phd phd) {
        StringBuilder tags = new StringBuilder();
        for(PhdTag tag : phd.getTags()){
            tags.append(String.format("%s{%n%s%n}%n",tag.getTagName(), tag.getTagValue()));
        }
        return tags.toString();
        
        
    }

    private static String writeDnaSection(Phd phd) {
        StringBuilder dna = new StringBuilder();
        dna.append(String.format("%s%n",BEGIN_DNA));
        dna.append(writeCalledInfo(phd));
        dna.append(String.format("%s%n",END_DNA));   
        return dna.toString();
    }

    private static String writeCalledInfo( Phd phd){
        List<NucleotideGlyph> bases = phd.getBasecalls().decode();
        List<PhredQuality> qualities = phd.getQualities().decode();
        List<ShortGlyph> peaks = phd.getPeaks().getData().decode();
        StringBuilder result = new StringBuilder();
        for(int i=0;i< bases.size(); i++){
            result.append(String.format("%s %d %d%n",
                                            bases.get(i), 
                                            qualities.get(i).getNumber(),
                                            peaks.get(i).getNumber()));
        }
        
        return result.toString();
        
    }

    private static String createComments(Phd phd) {
        StringBuilder comments = new StringBuilder();
        
        comments.append( BEGIN_COMMENT+"\n");
        comments.append( String.format("%n"));
        for(Entry<Object, Object> entry :phd.getComments().entrySet()){
            comments.append(String.format("%s: %s%n",entry.getKey(),entry.getValue()));
        }
        comments.append(String.format("%n"));
        comments.append(END_COMMENT+"\n");
        comments.append(String.format("%n"));
        return comments.toString();
    }


    private static void write(OutputStream out, String data) throws IOException{
        out.write(data.getBytes("UTF-8"));
    }
}
