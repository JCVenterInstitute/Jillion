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
