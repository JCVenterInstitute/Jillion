/*
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.trace.sanger.phd.ArtificialPhd;
import org.jcvi.trace.sanger.phd.DefaultPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdTag;
import org.jcvi.trace.sanger.phd.PhdWriter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CreatePhdBallFromAceFile implements AceFileVisitor{
    private final byte lowQualValue =20;
    private final byte highQualValue =30;
    private final OutputStream out;
    private String currentLine;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd kk:mm:ss yyyy");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("([A-Z_]+):\\s+(\\S+)");
    /**
     * @param out
     */
    public CreatePhdBallFromAceFile(OutputStream out) {
        this.out = out;
    }

    private String currentReadId;
    private StringBuilder currentBasecalls = new StringBuilder();
    Map<String, SequenceDirection> directions = new HashMap<String, SequenceDirection>();
    
    private boolean inARead=false;
    @Override
    public void visitAssembledFromLine(String readId, SequenceDirection dir,
            int gappedStartOffset) {
        directions.put(readId, dir);
    }

    @Override
    public void visitBaseSegment(Range gappedConsensusRange, String readId) {
    }

    @Override
    public void visitBasesLine(String bases) { 
        if(inARead){
            currentBasecalls.append(bases.trim());
        }
    }

    @Override
    public void visitConsensusQualities() { }

    @Override
    public void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) { }

    @Override
    public void visitHeader(int numberOfContigs, int totalNumberOfReads) { }

    @Override
    public void visitQualityLine(int clearLeft, int clearRight, int alignLeft,
            int alignRight) { 
        
    }
    @Override
    public void visitReadHeader(String readId, int gappedLength) {
        currentReadId = readId;
        currentBasecalls = new StringBuilder();
        inARead =true;
    }

    @Override
    public void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        List<NucleotideGlyph> ungappedBases =NucleotideGlyph.getGlyphsFor(
                currentBasecalls.toString().replaceAll("\\*", ""));
        if(directions.get(currentReadId)== SequenceDirection.REVERSE){
            ungappedBases = NucleotideGlyph.reverseCompliment(ungappedBases);
        }
        byte[] quals = new byte[ungappedBases.size()];
        Arrays.fill(quals, lowQualValue);
        EncodedGlyphs<PhredQuality> qualities = new DefaultEncodedGlyphs<PhredQuality>(
                RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(quals));
        
        Properties comments =createPhdCommentsFor(currentLine,date);
        Phd phd = new ArtificialPhd(new DefaultNucleotideEncodedGlyphs(ungappedBases),
                qualities, comments, Collections.<PhdTag>emptyList(),12);
        try {
            PhdWriter.writePhd(currentReadId, phd, out);
        } catch (IOException e) {
            throw new RuntimeException("error writing phd file for "+ currentReadId, e);
        }
        inARead=false;
    }

    private Properties createPhdCommentsFor(String commentLine,Date date){
       
        Properties comments= new Properties();
        
        Matcher matcher = COMMENT_PATTERN.matcher(commentLine);
        while(matcher.find()){
            comments.put(matcher.group(1),matcher.group(2));
        }
        comments.put("TIME", DATE_TIME_FORMATTER.print(date.getTime()));
        return comments;
    }
    @Override
    public void visitLine(String line) {  
        currentLine = line;
    }

    @Override
    public void visitEndOfFile() {}

    @Override
    public void visitFile() {       
    }

    @Override
    public void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitConsensusTagComment(String comment) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitConsensusTagData(String data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEndConsensusTag() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        // TODO Auto-generated method stub
        
    }


    public static void main(String[] args) throws IOException{
        File aceFile = new File(args[0]);
        FileOutputStream out = new FileOutputStream(args[1]);
        
        CreatePhdBallFromAceFile phdWriter = new CreatePhdBallFromAceFile(out);
        AceFileParser.parseAceFile(aceFile, phdWriter);
        out.close();
    }
}
