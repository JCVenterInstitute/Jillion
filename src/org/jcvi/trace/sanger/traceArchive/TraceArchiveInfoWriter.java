/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.Closeable;
import java.io.IOException;

public interface TraceArchiveInfoWriter extends Closeable{

    void write(TraceArchiveInfo info) throws IOException;
}
