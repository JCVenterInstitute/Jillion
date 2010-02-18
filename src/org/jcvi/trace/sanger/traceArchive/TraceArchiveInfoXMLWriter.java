/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.io.XMLUtil;


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
        out.write(value.getBytes());
    }
    @Override
    public void write(TraceArchiveInfo info) throws IOException {
        writeString(out, BEGIN_XML);
        for(TraceArchiveRecord record : info){
            writeString(out, String.format("%s\n",XMLUtil.beginTag("trace")));
            /*for(TraceInfoField field : FIELD_ORDER){
                writeString(out,String.format("%s%s%s\n", 
                        XMLUtil.beginTag(field),record.getAttribute(field),XMLUtil.endTag(field)));
            }
            */
            for(Entry<TraceInfoField,String> entry :record.entrySet()){
                writeString(out,String.format("%s%s%s\n", 
                        XMLUtil.beginTag(entry.getKey()),entry.getValue(),XMLUtil.endTag(entry.getKey())));
            }
            
            writeExtendedData(record);
            writeString(out, String.format("%s\n",XMLUtil.endTag("trace")));
        }

        writeString(out, END_XML);

    }


    private void writeExtendedData(TraceArchiveRecord record)
            throws IOException {
        Map<String,String> extendedData = record.getExtendedData();
        if(!extendedData.isEmpty()){
            writeString(out, String.format("%s\n", XMLUtil.beginTag(TraceInfoField.EXTENDED_DATA)));
            for(Entry<String,String> extendedEntry : extendedData.entrySet()){
                writeString(out,String.format("\t<field name='%s'>%s</field>\n", 
                        extendedEntry.getKey(),extendedEntry.getValue()));
            }
            writeString(out, String.format("%s\n", XMLUtil.endTag(TraceInfoField.EXTENDED_DATA)));
        }
    }

    @Override
    public void close() throws IOException {
        out.close();

    }

}
