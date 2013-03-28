/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jul 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.sanger.PositionSequence;

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
	public String getId() {
		try {
			return getFile().getName();
		} catch (IOException e) {
			throw new IllegalStateException("could not get file id",e);
		}
	}

	@Override
    public File getFile() throws IOException{
        File f= getFile(TraceInfoField.TRACE_FILE);
        if(!f.exists()){
            throw new IOException("file does not exist");
        }
        return f;
    }


    protected final File getFile(TraceInfoField traceInfoField) {
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
        return ObjectsUtil.nullSafeEquals(getRecord(), other.getRecord()) &&
        ObjectsUtil.nullSafeEquals(getRootDirPath(), other.getRootDirPath());
    }

    @Override
    public int getNumberOfTracePositions() {
        PositionSequence encodedPeaks= getPositionSequence();        
        int lastIndex= (int)encodedPeaks.getLength() -1;
        return encodedPeaks.get(lastIndex).getValue();
    }
    
    
}
