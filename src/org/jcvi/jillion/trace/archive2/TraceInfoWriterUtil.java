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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.archive2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jcvi.jillion.core.io.IOUtil;

public final class TraceInfoWriterUtil{

	 private static final String BEGIN_XML = 
		        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<trace_volume>\n";
    private static final String END_XML = "</trace_volume>\n";

    private TraceInfoWriterUtil(){
    	//can not instantiate
    }
    
    public static void writeTraceInfoXML(OutputStream out, TraceArchiveInfo info) throws IOException{
    	writeTraceInfoXML(out,info,true);
    }
    public static void writeTraceInfoXML(OutputStream out, TraceArchiveInfo info, boolean duplicateCommonSections) throws IOException{
    	 writeString(out, BEGIN_XML);
         Map<TraceInfoField, String> commonMap =info.getCommonFields();
         if(!duplicateCommonSections && !commonMap.isEmpty()){
         	writeString(out, beginTag("common_fields"));
         	for(Entry<TraceInfoField, String> entry : commonMap.entrySet()){
         		 writeString(out,"\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
         	}
         	writeString(out, endTag("common_fields"));
         }
         for(TraceArchiveRecord record : info.getRecordList()){
             writeString(out, "\t\t"+beginTag("trace"));
             final Map<TraceInfoField,String> entries;
             if(duplicateCommonSections){
            	 entries = getEntiresToWrite(record, commonMap);
             }else{
            	 entries = getEntiresToWrite(record);
             }
             for(Entry<TraceInfoField,String> entry :entries.entrySet()){
                 writeString(out,"\t\t\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
             }
             
             writeExtendedData(out, record);
             writeString(out, "\t\t"+endTag("trace"));
         }
        
         writeString(out, END_XML);

	}
    private static Map<TraceInfoField, String> getEntiresToWrite(TraceArchiveRecord record){
    	return getEntiresToWrite(record, Collections.<TraceInfoField, String>emptyMap());
    }
    private static Map<TraceInfoField, String> getEntiresToWrite(TraceArchiveRecord record, Map<TraceInfoField, String> commonMap){
    	Map<TraceInfoField, String> list = new TreeMap<TraceInfoField,String>( new Comparator<TraceInfoField>(){

			@Override
			public int compare(TraceInfoField o1,
					TraceInfoField o2) {
				//sort in alphabetical order
				return o1.name().compareTo(o2.name());
			}
    		
    	});
    	if(!commonMap.isEmpty()){
    		list.putAll(commonMap);
    	}
    	for(Entry<TraceInfoField, String> entry :record.entrySet()){
    		list.put(entry.getKey(), entry.getValue());
    	}
    	
    	return list;
    }
    
    private static void writeExtendedData(OutputStream out,TraceArchiveRecord record)
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

	private static void writeString(OutputStream out, String value) throws IOException{
        out.write(value.getBytes(IOUtil.UTF_8));
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
