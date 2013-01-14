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
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;


public class TraceArchiveInfoXMLWriter implements TraceArchiveInfoWriter {
    private static final String BEGIN_XML = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<trace_volume>\n";
    private static final String END_XML = "</trace_volume>\n";
    private static final List<TraceInfoField> FIELD_ORDER = Arrays.asList(
            TraceInfoField.TRACE_NAME,
            TraceInfoField.BASE_FILE,
            TraceInfoField.QUAL_FILE,
            TraceInfoField.PEAK_FILE,
            TraceInfoField.SVECTOR_CODE,
            TraceInfoField.TRACE_END,
            TraceInfoField.PROGRAM_ID,
            TraceInfoField.TRACE_FORMAT,
            TraceInfoField.CLIP_QUALITY_LEFT,
            TraceInfoField.CLIP_QUALITY_RIGHT,
            TraceInfoField.CLIP_VECTOR_LEFT,
            TraceInfoField.CLIP_VECTOR_RIGHT,
            TraceInfoField.WELL_ID,
            TraceInfoField.RUN_GROUP_ID,
            TraceInfoField.RUN_MACHINE_ID,
            TraceInfoField.RUN_DATE,
            TraceInfoField.CHEMISTRY_TYPE,
            TraceInfoField.TEMPLATE_ID,
            TraceInfoField.SEQ_LIB_ID,
            TraceInfoField.INSERT_SIZE,
            TraceInfoField.PLATE_ID,
            TraceInfoField.TRACE_FILE
            
    );
    private final OutputStream out;
    public TraceArchiveInfoXMLWriter(OutputStream out){
        this.out = out;
    }

    
   
    
    private void writeString(OutputStream out, String value) throws IOException{
        out.write(value.getBytes(IOUtil.UTF_8));
    }
    @Override
    public void write(TraceArchiveInfo info) throws IOException {
        writeString(out, BEGIN_XML);
        StreamingIterator<TraceArchiveRecord> iter =null;
        try{
        	iter= info.iterator();
        while(iter.hasNext()){
        	TraceArchiveRecord record = iter.next();
            writeString(out, beginTag("trace"));
            /*for(TraceInfoField field : FIELD_ORDER){
                writeString(out,String.format("%s%s%s\n", 
                        XMLUtil.beginTag(field),record.getAttribute(field),XMLUtil.endTag(field)));
            }
            */
            for(Entry<TraceInfoField,String> entry :record.entrySet()){
                writeString(out,beginAndEndTag(entry.getKey(), entry.getValue()));
            }
            
            writeExtendedData(record);
            writeString(out, endTag("trace"));
        }
        } catch (DataStoreException e) {
			throw new IOException("error getting iterator from trace archive datastore",e);
		}finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        writeString(out, END_XML);

    }


    private void writeExtendedData(TraceArchiveRecord record)
            throws IOException {
        Map<String,String> extendedData = record.getExtendedData();
        if(!extendedData.isEmpty()){
            writeString(out, beginTag(TraceInfoField.EXTENDED_DATA));
            for(Entry<String,String> extendedEntry : extendedData.entrySet()){
                writeString(out,String.format("\t<field name='%s'>%s</field>%n", 
                        extendedEntry.getKey(),extendedEntry.getValue()));
            }
            writeString(out,endTag(TraceInfoField.EXTENDED_DATA));
        }
    }

    @Override
    public void close() throws IOException {
        out.close();

    }
    private static String beginAndEndTag(Object key, Object value){
        return String.format("<%s>%s</%s>%n", key,value,key);
    }
    private static String beginTag(Object value){
        return String.format("<%s>%n", value);
    }
    private static String endTag(Object value){
        return String.format("</%s>%n", value);
    }
}
