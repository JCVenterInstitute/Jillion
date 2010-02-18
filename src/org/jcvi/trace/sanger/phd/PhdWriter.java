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
    
    public static void writePhd(String id, Phd phd,OutputStream out) throws IOException{
        
        write(out, String.format("%s %s%n%n",BEGIN_SEQUENCE, id));
        
        writeComments(out, phd);
        writeDnaSection(out, phd);
        write(out, String.format("%n"));
        write(out, String.format("%s%n",END_SEQUENCE));
        writeTags(out,phd);
        out.flush();
        
    }

    private static void writeTags(OutputStream out, Phd phd) throws IOException {
        for(PhdTag tag : phd.getTags()){
            write(out, String.format("%s{%n%s%n}%n",tag.getTagName(), tag.getTagValue()));
        }
        
    }

    private static void writeDnaSection(OutputStream out, Phd phd) throws IOException {
        write(out, String.format("%s%n",BEGIN_DNA));
        writeCalledInfo(out,phd);
        write(out, String.format("%s%n",END_DNA));        
    }

    private static void writeCalledInfo(OutputStream out, Phd phd) throws IOException {
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
        
        write(out, result.toString());
    }

    private static void writeComments(OutputStream out, Phd phd) throws IOException {
        write(out, BEGIN_COMMENT+"\n");
        write(out, String.format("%n"));
        for(Entry<Object, Object> entry :phd.getComments().entrySet()){
            writeComment(out, entry.getKey(), entry.getValue());
        }
        write(out, String.format("%n"));
        write(out, END_COMMENT+"\n");
        write(out, String.format("%n"));
    }

    private static void writeComment(OutputStream out, Object key, Object value) throws IOException {
        
        write(out, String.format("%s: %s%n",key,value));
        
    }

    private static void write(OutputStream out, String data) throws IOException{
        out.write(data.getBytes("UTF-8"));
    }
}
