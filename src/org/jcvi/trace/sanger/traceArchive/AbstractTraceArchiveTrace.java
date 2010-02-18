/*
 * Created on Jul 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;

public abstract class AbstractTraceArchiveTrace implements TraceArchiveTrace {

    private final TraceArchiveRecord record;
    private final String rootDirPath;
    
    public AbstractTraceArchiveTrace(TraceArchiveRecord record,String rootDirPath){
        if(record ==null || rootDirPath == null){
            throw new IllegalArgumentException("can not have null parameters");
        }
        this.record = record;
        this.rootDirPath = rootDirPath;
    }

    public TraceArchiveRecord getRecord() {
        return record;
    }

    public String getRootDirPath() {
        return rootDirPath;
    }

    
    @Override
    public File getFile() throws IOException{
        File f= getFile(TraceInfoField.TRACE_FILE);
        if(!f.exists()){
            throw new IOException("file does not exist");
        }
        return f;
    }

    protected final InputStream getInputStreamFor(TraceInfoField traceInfoField) throws FileNotFoundException{
        return new FileInputStream(getFile(traceInfoField));
    }

    private File getFile(TraceInfoField traceInfoField) {
        return new File(rootDirPath+"/"+record.getAttribute(traceInfoField));
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + record.hashCode();
        result = prime * result + rootDirPath.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof AbstractTraceArchiveTrace)){
            return false;
        }
        AbstractTraceArchiveTrace other = (AbstractTraceArchiveTrace) obj;
        return CommonUtil.similarTo(getRecord(), other.getRecord()) &&
        CommonUtil.similarTo(getRootDirPath(), other.getRootDirPath());
    }

    @Override
    public int getNumberOfTracePositions() {
        EncodedGlyphs<ShortGlyph> encodedPeaks= getPeaks().getData();        
        int lastIndex= (int)encodedPeaks.getLength() -1;
        return encodedPeaks.get(lastIndex).getNumber();
    }
    
    
}
