/*
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

public class NameTagTraceArchiveRecordIdGenerator implements
        TraceArchiveRecordIdGenerator {
    @Override
    public String generateIdFor(TraceArchiveRecord record) {
        return record.getAttribute(TraceInfoField.TRACE_NAME);
    }

}
