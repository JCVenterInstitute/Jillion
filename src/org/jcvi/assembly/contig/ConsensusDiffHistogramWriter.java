/*
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.ContigSlice;
import org.jcvi.assembly.slice.ContigSliceMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class ConsensusDiffHistogramWriter implements
        BasecallCountHistogramWriter {
    
    private static List<NucleotideGlyph> HISTOGRAM_NUCLEOTIDES_TO_INCLUDE = Arrays.asList(
            NucleotideGlyph.Adenine, NucleotideGlyph.Cytosine, NucleotideGlyph.Guanine, NucleotideGlyph.Thymine, NucleotideGlyph.Gap, NucleotideGlyph.Unknown);
    
    private final OutputStream out;
    private final int numberOfDiffsThreshold;
    public ConsensusDiffHistogramWriter(File fileToCreate,int numberOfDiffsThreshold) throws IOException{
        out = new FileOutputStream(fileToCreate); 
        this.numberOfDiffsThreshold = numberOfDiffsThreshold;
        writeHeaders();
    }
    
    @Override
    public void write(SliceMap sliceMap, NucleotideEncodedGlyphs reference) throws IOException {
        int index =0;
        for(Slice slice : sliceMap){
            if(slice ==null){
                System.out.println("slice is null at index "+ index);
            }
            else{
                BasecallCountHistogram histogram = new DefaultBasecallCountHistogram(slice);
                final NucleotideGlyph referenceBase = reference.get(index);
                if(accept(referenceBase,histogram)){                
                    writeHistogram(index,referenceBase,histogram);
                }
            }
            index++;
        }
        
    }
    protected boolean accept(NucleotideGlyph reference,
            BasecallCountHistogram histogram) {
       return !reference.isGap() && hasADiffThatIsNotAGap(reference,histogram) && hasEnoughDifferences(reference,histogram, numberOfDiffsThreshold);
    }
    private void writeHeaders() throws IOException {
        StringBuilder header = new StringBuilder();
        header.append("offset\tconsen\t");
        for(NucleotideGlyph base : HISTOGRAM_NUCLEOTIDES_TO_INCLUDE){ 
            header.append("# ").append(base).append("\t");
        }
        header.append("\n");
        out.write(header.toString().getBytes());
        out.flush();
    }
    private boolean hasEnoughDifferences(NucleotideGlyph reference,BasecallCountHistogram histogram, int threshold) {

        
        
        for(Entry<NucleotideGlyph, Integer> entry : histogram.getHistogram().entrySet()){
            if(entry.getKey() != reference && entry.getValue().intValue() >= threshold){
                return true;
            }
        }
        return false;
    }
    
    private boolean hasADiffThatIsNotAGap(NucleotideGlyph reference,BasecallCountHistogram histogram) {

        boolean hasDiff = false;
        for(Entry<NucleotideGlyph, Integer> entry : histogram.getHistogram().entrySet()){
            
            if(isANonGapValidRangeDifference(reference, entry)){
                    hasDiff = true;
                    break;
            }
        }
        return hasDiff;
    }
    private boolean isANonGapValidRangeDifference(NucleotideGlyph consensusBase,
            Entry<NucleotideGlyph, Integer> histogramEntry) {
        final NucleotideGlyph basecall = histogramEntry.getKey();
        int offset = histogramEntry.getValue();
        return basecall != consensusBase && basecall != NucleotideGlyph.Gap && offset > 0;
    }
    private void writeHistogram(int index,NucleotideGlyph reference,BasecallCountHistogram histogram) throws IOException {
        StringBuilder record = new StringBuilder();
        record.append(String.format("%d\t%s\t",index,reference));
        for(NucleotideGlyph base : HISTOGRAM_NUCLEOTIDES_TO_INCLUDE){            
          record.append(histogram.getHistogram().get(base)).append("\t");
        }
        record.append("\n");
        out.write(record.toString().getBytes());
        out.flush();
    }
    @Override
    public void close() throws IOException {
        out.close();
        
    }
}
