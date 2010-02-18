/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.io.IOUtil;


public class TextContigCheckReportWriter<P extends PlacedRead> implements ContigCheckReportWriter<P>{

    private OutputStream out;
    public TextContigCheckReportWriter(OutputStream out, String id) throws IOException{
        this.out = out;
        writeTitle(id);
    }
    private void writeTitle(String id) throws IOException {
        out.write(String.format("%s:%n", id).getBytes());
        out.flush();
        
    }
    @Override
    public void write(ContigCheckReport<P> report) throws IOException {
       final Contig<P> contig = report.getContig();
    final String header = String.format("REPORT FOR CONTIG : %s - number of reads = %d%n====================%n", 
            contig.getId(),contig.getNumberOfReads());
        out.write(header.getBytes());
       for(AnalysisIssue issue: report.getAnalysisIssues()){
           String issueString = String.format("\t%s%n", issue);
           out.write(issueString.getBytes());
       }
       out.write(String.format("%n").getBytes());
       out.flush();
    }
    @Override
    public void close() {
        IOUtil.closeAndIgnoreErrors(out);
        
    }


}
