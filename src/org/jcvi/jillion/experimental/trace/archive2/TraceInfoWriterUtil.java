/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.trace.archive2;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jcvi.jillion.core.io.IOUtil;

public final class TraceInfoWriterUtil{

	 private static final String BEGIN_XML = 
		        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<trace_volume>\n";
    private static final String END_XML = "</trace_volume>\n";

    private static final DateFormat TRACE_ARCHIVE_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd-HH:mm:ss");
    
    private TraceInfoWriterUtil(){
    	//can not instantiate
    }
    
    public static synchronized String formatDate(Date date){
    	return TRACE_ARCHIVE_DATE_FORMAT.format(date);
    }
    
    public static void writeTraceInfoXML(OutputStream out, TraceArchiveInfo info,
    		String volumeName, Date volumeDate, String volumeVersion) throws IOException{
    	writeTraceInfoXML(out,info, volumeName, volumeDate, volumeVersion, true);
    }
    public static void writeTraceInfoXML(OutputStream out, TraceArchiveInfo info,
    		String volumeName, Date volumeDate, String volumeVersion,
    		
    		boolean duplicateCommonSections) throws IOException{
    	 writeString(out, BEGIN_XML);
    	 writeString(out, "\t"+beginAndEndTag("volume_name", volumeName));
    	 writeString(out, "\t"+beginAndEndTag("volume_date", formatDate(volumeDate)));
    	 writeString(out, "\t"+beginAndEndTag("volume_version", volumeVersion));
         Map<TraceInfoField, String> commonMap =info.getCommonFields();
         if(!duplicateCommonSections && !commonMap.isEmpty()){
         	writeString(out, beginTag("common_fields"));
         	for(Entry<TraceInfoField, String> entry : commonMap.entrySet()){
         		 writeString(out,"\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
         	}
         	writeString(out, endTag("common_fields"));
         }
         for(TraceArchiveRecord record : info.getRecordList()){
             writeString(out, "\t"+beginTag("trace"));
             final Map<TraceInfoField,String> entries;
             if(duplicateCommonSections){
            	 entries = getEntiresToWrite(record, commonMap);
             }else{
            	 entries = getEntiresToWrite(record);
             }
             for(Entry<TraceInfoField,String> entry :entries.entrySet()){
                 writeString(out,"\t\t"+beginAndEndTag(entry.getKey(), entry.getValue()));
             }
             
             writeExtendedData(out, record);
             writeString(out, "\t"+endTag("trace"));
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
