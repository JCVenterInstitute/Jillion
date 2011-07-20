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
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.archive.DefaultTraceArchiveInfo;
import org.jcvi.common.core.seq.read.trace.archive.NameTagTraceArchiveRecordIdGenerator;
import org.jcvi.common.core.seq.read.trace.archive.TraceArchiveInfo;
import org.jcvi.common.core.seq.read.trace.archive.TraceArchiveRecord;
import org.jcvi.common.core.seq.read.trace.archive.TraceArchiveRecordIdGenerator;
import org.jcvi.common.core.seq.read.trace.archive.TraceInfoField;
import org.jcvi.common.core.seq.read.trace.archive.TraceInfoXMLTraceArchiveInfoBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestActualTraceArchiveXML {
    private static final String FOLDER_ROOT_DIR = "files/exampleTraceArchive";
    private static final TraceArchiveRecordIdGenerator ID_GENERATOR= new NameTagTraceArchiveRecordIdGenerator();
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestActualTraceArchiveXML.class);
	
    @Test
    public void parseTraceInfo() throws IOException, DataStoreException{
        TraceArchiveInfo traceInfo = new DefaultTraceArchiveInfo(
                new TraceInfoXMLTraceArchiveInfoBuilder<TraceArchiveRecord>(
                ID_GENERATOR, 
                RESOURCES.getFileAsStream(FOLDER_ROOT_DIR+"/TRACEINFO.xml")));
       TraceArchiveRecord actualRecord = traceInfo.get("XX08A02T44F09PB11F");
       
       assertEquals("XX08A02T44F09PB11F", actualRecord.getAttribute(TraceInfoField.TRACE_NAME));
       assertEquals("./base/1119369016157.base", actualRecord.getAttribute(TraceInfoField.BASE_FILE));
       assertEquals("ABI 3730xl", actualRecord.getAttribute(TraceInfoField.RUN_MACHINE_TYPE));
       assertEquals("1048003148673", actualRecord.getAttribute(TraceInfoField.TEMPLATE_ID));
       assertEquals("GCATCCCAGGTGTTGCGATA", actualRecord.getAttribute(TraceInfoField.AMPLIFICATION_REVERSE));
       assertEquals("0", actualRecord.getAttribute(TraceInfoField.REFERENCE_ACCESSION));
       assertEquals("t", actualRecord.getAttribute(TraceInfoField.CHEMISTRY_TYPE));
       
       assertEquals("719", actualRecord.getAttribute(TraceInfoField.CLIP_QUALITY_RIGHT));
       assertEquals("1064144674928", actualRecord.getAttribute(TraceInfoField.RUN_GROUP_ID));
       assertEquals("./qual/1119369016157.qual", actualRecord.getAttribute(TraceInfoField.QUAL_FILE));
       assertEquals("20090618 08:16:04", actualRecord.getAttribute(TraceInfoField.RUN_DATE));
       assertEquals("AGCRAAAGCAGGCAAACCAT", actualRecord.getAttribute(TraceInfoField.AMPLIFICATION_FORWARD));
       
       assertEquals("DNA 361", actualRecord.getAttribute(TraceInfoField.RUN_MACHINE_ID));
       assertEquals("ztr", actualRecord.getAttribute(TraceInfoField.TRACE_FORMAT));
       assertEquals("KB 1.1.2, Trace Tuner 2.0.1", actualRecord.getAttribute(TraceInfoField.PROGRAM_ID));
       assertEquals("./trace/P030546_K18_JTC_swineorigininfluenza_1064144674928_1064144674997_069_1119369016061.ztr", actualRecord.getAttribute(TraceInfoField.TRACE_FILE));
       assertEquals("K18", actualRecord.getAttribute(TraceInfoField.WELL_ID));
       
       assertEquals("F", actualRecord.getAttribute(TraceInfoField.TRACE_END));
       assertEquals("./peak/1119369016157.peak", actualRecord.getAttribute(TraceInfoField.PEAK_FILE));
       assertEquals("P030546", actualRecord.getAttribute(TraceInfoField.PLATE_ID));
       assertEquals("21", actualRecord.getAttribute(TraceInfoField.CLIP_QUALITY_LEFT));
       assertEquals("763", actualRecord.getAttribute(TraceInfoField.AMPLIFICATION_SIZE));
    }
}
