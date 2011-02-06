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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityGlyphCodec;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.SangerTraceCodec;

public class PhdCodec implements SangerTraceCodec<Phd>{

    private static final int INITIAL_LIST_SIZE = 700;
    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private static final QualityGlyphCodec QUALITY_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;

    private static final String BEGIN_SEQUENCE = "BEGIN_SEQUENCE";
    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    
    private static final String NEXT_LINE = "\n";
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\s*(\\w+):\\s+(.*?)$");
    private static final Pattern CALLED_INFO_PATTERN = Pattern.compile("^\\s*(\\w)\\s+(\\d+)\\s+(\\d+)");
    private static final Pattern END_SEQUENCE_PATTERN = Pattern.compile("^\\s*"+END_SEQUENCE+"\\s*");
    @Override
    public Phd decode(File sangerTrace) throws TraceDecoderException,
            FileNotFoundException {
        InputStream in = new FileInputStream(sangerTrace);
        try{
            return decode(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public Phd decode(InputStream in) throws TraceDecoderException {
        Scanner scanner = new Scanner(in);
        Properties comments = new Properties();
        List<NucleotideGlyph> bases = new ArrayList<NucleotideGlyph>(INITIAL_LIST_SIZE);
        List<ShortGlyph> peaks = new ArrayList<ShortGlyph>(INITIAL_LIST_SIZE);
        List<PhredQuality> qualities = new ArrayList<PhredQuality>(INITIAL_LIST_SIZE);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher endSequencePattern = END_SEQUENCE_PATTERN.matcher(line);
            if(endSequencePattern.matches()){
                break;
            }
            Matcher infoPattern = CALLED_INFO_PATTERN.matcher(line);
            if(infoPattern.find()){
                bases.add(NucleotideGlyph.getGlyphFor(infoPattern.group(1).charAt(0)));
                qualities.add(PhredQuality.valueOf(Byte.parseByte(infoPattern.group(2))));
                //phd fake peak data can go beyond short.max so use int parser
                peaks.add(PEAK_FACTORY.getGlyphFor(Integer.parseInt(infoPattern.group(3))));
            }
            else{
                Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                if(commentMatcher.find()){
                    comments.put(commentMatcher.group(1), commentMatcher.group(2));
                }
            }
            
        }
        return new DefaultPhd(
                new DefaultNucleotideEncodedGlyphs(bases),
                new DefaultQualityEncodedGlyphs(QUALITY_CODEC, qualities),
                new Peaks(peaks),
                comments,
                Collections.<PhdTag>emptyList());
    }

    @Override
    public void encode(Phd phd, OutputStream out) throws IOException {
        
        write(out, BEGIN_SEQUENCE+"\n");
        write(out, NEXT_LINE);
        
        writeComments(out, phd);
        writeDnaSection(out, phd);
        write(out, NEXT_LINE);
        write(out, END_SEQUENCE+"\n");
        
        
    }

    private void writeDnaSection(OutputStream out, Phd phd) throws IOException {
        write(out, BEGIN_DNA+"\n");
        write(out, NEXT_LINE);
        writeCalledInfo(out,phd);
        write(out, END_DNA+"\n");        
    }

    private void writeCalledInfo(OutputStream out, Phd phd) throws IOException {
        List<NucleotideGlyph> bases = phd.getBasecalls().decode();
        List<PhredQuality> qualities = phd.getQualities().decode();
        List<ShortGlyph> peaks = phd.getPeaks().getData().decode();
        StringBuilder result = new StringBuilder();
        for(int i=0;i< bases.size(); i++){
            result.append(String.format("%s %d %d\n",
                                            bases.get(i), 
                                            qualities.get(i).getNumber(),
                                            peaks.get(i).getNumber()));
        }
        
        write(out, result.toString());
    }

    private void writeComments(OutputStream out, Phd phd) throws IOException {
        write(out, BEGIN_COMMENT+"\n");
        write(out, NEXT_LINE);
        for(Entry<Object, Object> entry :phd.getComments().entrySet()){
            writeComment(out, entry.getKey(), entry.getValue());
        }
        write(out, NEXT_LINE);
        write(out, END_COMMENT+"\n");
        write(out, NEXT_LINE);
    }

    private void writeComment(OutputStream out, Object key, Object value) throws IOException {
        StringBuilder comment = new StringBuilder()
                                    .append(key)
                                   .append(": ")
                                   .append(value)
                                   .append("\n");
        
        write(out, comment.toString());
        
    }

    private void write(OutputStream out, String data) throws IOException{
        out.write(data.getBytes("UTF-8"));
    }
}
