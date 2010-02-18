/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.io.IOException;

import org.jcvi.assembly.PlacedRead;

public interface ContigCheckReportWriter<P extends PlacedRead>  {

    void write(ContigCheckReport<P> report) throws IOException;
    
    void close();
}
