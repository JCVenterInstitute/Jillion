/*
 * Created on May 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.contigChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigChecker;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.TextContigCheckReportWriter;
import org.jcvi.assembly.contig.ConsensusDiffHistogramWriter;
import org.jcvi.assembly.coverage.PngCoverageWriter;
import org.jcvi.assembly.coverage.SequenceCoverageWriter;

public class ContigCheckerUtil {

    public  static <P extends PlacedRead> void writeContigCheckerResults(String outputBasePath,
            String prefix, String id,
           ContigCheckerStruct<P> struct,
            ContigChecker<P> contigchecker,
            boolean writeContigHistogram)
            throws IOException, FileNotFoundException {
        final Contig<P> contig = struct.getContig();
        final String currentContigPrefix = outputBasePath + "/"+prefix+"."+id;
        
        TextContigCheckReportWriter<P> contigCheckWriter = new TextContigCheckReportWriter<P>(new FileOutputStream(currentContigPrefix + ".contigChecker.out"), id);
        contigCheckWriter.write(contigchecker.getContigCheckReport());
        contigCheckWriter.close();
        
        SequenceCoverageWriter<P> sequenceCoverageWriter = new SequenceCoverageWriter<P>(new File(currentContigPrefix + ".sequenceCoverage.png"),"ungapped Sequence Coverage of " +id);
        
        sequenceCoverageWriter.write(contig, struct.getSequenceCoverageMap());
        sequenceCoverageWriter.close();
        
        PngCoverageWriter<Placed> validRangeCoverageWriter = new PngCoverageWriter<Placed>(new File(currentContigPrefix + ".validRangeCoverage.png"), "Valid Range Coverage of "+id);
        validRangeCoverageWriter.write(struct.getSequenceValidRangeCoverageMap());
        validRangeCoverageWriter.close();
        if(writeContigHistogram){
        File histogramFile = new File(currentContigPrefix + ".histogram.csv");
            ConsensusDiffHistogramWriter histogramWriter = new ConsensusDiffHistogramWriter(histogramFile, 2);
            histogramWriter.write(struct.getSliceMap(),contig.getConsensus());
            histogramWriter.close();
            
        }
    }
}
