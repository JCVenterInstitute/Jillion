/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public class AcePlacedReadAdapter implements AcePlacedRead{

    private final PlacedRead placedRead;
    private final PhdInfo phdInfo;
    /**
     * @param placedRead
     */
    public AcePlacedReadAdapter(PlacedRead placedRead,Date phdDate, File traceFile) {
        this.placedRead = placedRead;
        String readId = placedRead.getId();
        final String id;
        if(traceFile !=null && "sff".equals(FilenameUtils.getExtension(traceFile.getName()))){
           id="sff:"+traceFile.getName()+":"+readId;
        }else{
            id= readId;
        }
        this.phdInfo= new DefaultPhdInfo(id, readId+".phd.1", phdDate);
    }

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        return placedRead.convertReferenceIndexToValidRangeIndex(referenceIndex);
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        return placedRead.convertValidRangeIndexToReferenceIndex(validRangeIndex);
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return placedRead.getSequenceDirection();
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return placedRead.getSnps();
    }
    @Override
    public Range getValidRange() {
        return placedRead.getValidRange();
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return placedRead.getEncodedGlyphs();
    }
    @Override
    public String getId() {
        return placedRead.getId();
    }
    @Override
    public long getLength() {
        return placedRead.getLength();
    }
    @Override
    public long getEnd() {
        return placedRead.getEnd();
    }
    @Override
    public long getStart() {
        return placedRead.getStart();
    }
    @Override
    public String toString() {
        return "AcePlacedReadAdapter [placedRead="
                + placedRead + ", phdInfo=" + phdInfo +  "]";
    }
    
}
