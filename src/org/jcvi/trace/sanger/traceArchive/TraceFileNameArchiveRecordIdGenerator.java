/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

public class TraceFileNameArchiveRecordIdGenerator implements
        TraceArchiveRecordIdGenerator {

    @Override
    public String generateIdFor(TraceArchiveRecord record) {
        String fullPathToTraceFile = record.getAttribute(TraceInfoField.TRACE_FILE);
        String[] brokendownPath =fullPathToTraceFile.split("/");
        String traceFileName = brokendownPath[brokendownPath.length-1];
        final int extensionIndex = traceFileName.lastIndexOf(".");
        if(extensionIndex ==-1){
            return traceFileName;
        }
        return traceFileName.substring(0,extensionIndex);
    }

}
