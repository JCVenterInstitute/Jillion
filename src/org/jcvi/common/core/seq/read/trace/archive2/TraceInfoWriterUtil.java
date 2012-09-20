package org.jcvi.common.core.seq.read.trace.archive2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.io.IOUtil;

public class TraceInfoWriterUtil{

	 private static final String BEGIN_XML = 
		        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<trace_volume>\n";
    private static final String END_XML = "</trace_volume>\n";

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
             final List<Entry<TraceInfoField,String>> entries;
             if(duplicateCommonSections){
            	 entries = getEntiresToWrite(record, commonMap);
             }else{
            	 entries = getEntiresToWrite(record);
             }
             for(Entry<TraceInfoField,String> entry :entries){
                 writeString(out,"\t\t\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
             }
             
             writeExtendedData(out, record);
             writeString(out, "\t\t"+endTag("trace"));
         }
        
         writeString(out, END_XML);

	}
    private static List<Entry<TraceInfoField, String>> getEntiresToWrite(TraceArchiveRecord record){
    	return getEntiresToWrite(record, Collections.<TraceInfoField, String>emptyMap());
    }
    private static List<Entry<TraceInfoField, String>> getEntiresToWrite(TraceArchiveRecord record, Map<TraceInfoField, String> commonMap){
    	List<Entry<TraceInfoField, String>> list = new ArrayList<Map.Entry<TraceInfoField,String>>();
    	
    	if(!commonMap.isEmpty()){
    		list.addAll(commonMap.entrySet());
    	}
    	list.addAll(record.entrySet());
    	Collections.sort(list, new Comparator<Entry<TraceInfoField, String>>(){

			@Override
			public int compare(Entry<TraceInfoField, String> o1,
					Entry<TraceInfoField, String> o2) {
				//sort in alphabetical order
				return o1.getKey().name().compareTo(o2.getKey().name());
			}
    		
    	});
    	
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
