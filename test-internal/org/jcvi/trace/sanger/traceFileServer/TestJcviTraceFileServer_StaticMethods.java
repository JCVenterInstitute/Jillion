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

package org.jcvi.trace.sanger.traceFileServer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestJcviTraceFileServer_StaticMethods {

    private static String oldChromatogramArchiverURL;
    @BeforeClass
    public static void saveProperties(){
        oldChromatogramArchiverURL = System.getProperty(TraceFileServerUtil.TRACE_FILE_SERVER_WRITER_URL_BASE_KEY);
    }
    
    
    @AfterClass
    public static void restoreProperties(){
        if(oldChromatogramArchiverURL != null){
            System.setProperty(TraceFileServerUtil.TRACE_FILE_SERVER_WRITER_URL_BASE_KEY,oldChromatogramArchiverURL);
        }
    }
    
    @Test
    public void setWriteUrl(){
        JcviTraceFileServer.useTraceFileServerWriterUrl(TraceFileServerUtil.TIGR_URL);
        assertEquals(TraceFileServerUtil.TIGR_URL,
                System.getProperty(TraceFileServerUtil.TRACE_FILE_SERVER_WRITER_URL_BASE_KEY));
    }
}
