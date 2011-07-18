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
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace;

public class DefaultTraceFileNameIdGenerator implements TraceFileNameIdGenerator{

    private final boolean stripExtension;
    public DefaultTraceFileNameIdGenerator(boolean stripExtension){
        this.stripExtension = stripExtension;
    }
    @Override
    public String generateIdFor(String filePath) {
        String fileName = getFileNameFrom(filePath);
        if(stripExtension){
            return stripExtension(fileName);
        }
        return fileName;
    }

    private String getFileNameFrom(String filePath) {
        String[] brokendownPath =filePath.split("/");
        return  brokendownPath[brokendownPath.length-1];
    }
    private String stripExtension(String fileName){
        int index =fileName.lastIndexOf('.');
        if(index !=-1){
            if(index ==0){
                throw new IllegalArgumentException("fileName can not only have 1 '.' which is at the beginning");
            }
            return fileName.substring(0, index);
        }
        return fileName;
    }

}
