/*
 * Created on Dec 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.Frame;
import org.jcvi.assembly.annot.ref.CodingRegion;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.RefGene;
/**
 * Writes RefGene data into the GoldenPath RefGene.txt format.
 * I haven't found a actual specification yet on this format 
 * so it may not be 100% correct.
 * @author dkatzel
 *
 *
 */
public class RefGeneTxtWriter implements RefGeneWriter {
    private static final String DELIM = "\t";
    private static final String END_LINE = "\n";
    
    private static final String STATE_NONE = "none";
    private static final String STATE_UNKNOWN = "unk";
    private static final String STATE_INCOMPLETE = "incmpl";
    private static final String STATE_COMPLETE = "cmpl";
    private OutputStream out;
    public RefGeneTxtWriter(OutputStream out){
        this.out = out;
    }
    @Override
    public void write(List<RefGene> refGenes) throws IOException {
        for(RefGene refGene : refGenes){
            write(refGene);
        }

    }
    private void write(RefGene refGene) throws IOException {
        StringBuilder result = new StringBuilder();
        //bin always 0?
        append(result,0);
        append(result,refGene.getName());
        append(result,refGene.getReferenceSequenceName());
        append(result, refGene.getStrand());
        append(result, refGene.getTranscriptionRange().getStart());
        append(result, refGene.getTranscriptionRange().getEnd());
        final CodingRegion codingRegion = refGene.getCodingRegion();
        append(result, codingRegion.getRange().getStart());
        append(result, codingRegion.getRange().getEnd());
        final List<Exon> exonsToWrite = getExonsToWrite(codingRegion.getExons());
        writeExons(result, exonsToWrite);
        append(result, refGene.getId());
        writeAlternateName(result, refGene);
        append(result, translateCodingRegionState(codingRegion.getStartCodingRegionState()));
        append(result, translateCodingRegionState(codingRegion.getEndCodingRegionState()));
        writeExonFrames(result, exonsToWrite);
        result.append(END_LINE);
        
        out.write(result.toString().getBytes());        
    }
    private void writeAlternateName(StringBuilder result, RefGene refGene) {
        final String alternateName = refGene.getAlternateName();
        if(alternateName ==null){
            //write the non-alternate name instead of null
            append(result, refGene.getName());
        }
        else{
            append(result, alternateName);
        }
        
    }

    private void writeExons(StringBuilder result, List<Exon> exons) {
        StringBuilder starts = new StringBuilder();
        StringBuilder ends = new StringBuilder();
        
        for(Exon exon : exons){
            starts.append(exon.getStartPosition());
            starts.append(",");
            ends.append(exon.getEndPosition());
            //format requires trailing space on last exon!
            ends.append(",");
        }
        
        append(result, exons.size());
        append(result, starts);
        append(result, ends);
    }
    /**
     * The current version of the nubler mapper that uses
     * the refGene.txt file has a bug where it can not
     * handle single exons. If a single exon is passed
     * then it will make 2 exons covering the same range
     * with a zero sized intron
     * in between.
     * @param exons a List of Exons
     * @return a List of Exons (possibly the same list)
     */
    private List<Exon> getExonsToWrite(List<Exon> exons) {
        if(exons.size() !=1){
            return exons;
        }
        //only 1 exon, break into 2
        Exon actualExon =exons.get(0);
        long start = actualExon.getStartPosition();
        //Simplest way is to make the first exon only 1 codon long 
        long intronStart = start+2;
        final Range fake1 = Range.buildRange(start, intronStart);
        final Range fake2 = Range.buildRange(intronStart+1, actualExon.getEndPosition());
        final Frame frame = actualExon.getFrame();
        return Arrays.<Exon>asList(new DefaultExon(frame, fake1),
                new DefaultExon(frame, fake2));
    }
    private void writeExonFrames(StringBuilder result, List<Exon> exons) {
        
        StringBuilder frames = new StringBuilder();
        for(Exon exon : exons){
            frames.append(exon.getFrame().getFrame());
            //format requires trailing space on last frame!
            frames.append(",");
        }        
        append(result, frames);
    }
    private void append(StringBuilder builder, Object toWrite){
        builder.append(toWrite);
        builder.append(DELIM);
    }
    
    private String translateCodingRegionState(CodingRegionState state){
      if(state == CodingRegionState.NONE ){
          return STATE_NONE;
      }
      if(state == CodingRegionState.INCOMPLETE ){
          return STATE_INCOMPLETE;
      }
      if(state == CodingRegionState.COMPLETE ){
          return STATE_COMPLETE;
      }
      return STATE_UNKNOWN;
    }
}
