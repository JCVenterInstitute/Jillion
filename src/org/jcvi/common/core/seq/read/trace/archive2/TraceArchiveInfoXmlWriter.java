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
package org.jcvi.common.core.seq.read.trace.archive2;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.io.IOUtil;


public class TraceArchiveInfoXmlWriter implements Closeable{
    private static final String BEGIN_XML = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<trace_volume>\n";
    private static final String END_XML = "</trace_volume>\n";
   
    private final OutputStream out;
    public TraceArchiveInfoXmlWriter(OutputStream out){
        this.out = out;
    }

    
   
    
    private void writeString(OutputStream out, String value) throws IOException{
        out.write(value.getBytes(IOUtil.UTF_8));
    }

    public void write(TraceArchiveInfo info) throws IOException {
        writeString(out, BEGIN_XML);
        Map<TraceInfoField, String> commonMap =info.getCommonFields();
        if(!commonMap.isEmpty()){
        	writeString(out, beginTag("common_fields"));
        	for(Entry<TraceInfoField, String> entry : commonMap.entrySet()){
        		 writeString(out,"\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
        	}
        	writeString(out, endTag("common_fields"));
        }
        for(TraceArchiveRecord record : info.getRecordList()){
            writeString(out, "\t\t"+beginTag("trace"));
            for(Entry<TraceInfoField,String> entry :record.entrySet()){
                writeString(out,"\t\t\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
            }
            
            writeExtendedData(record);
            writeString(out, "\t\t"+endTag("trace"));
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
