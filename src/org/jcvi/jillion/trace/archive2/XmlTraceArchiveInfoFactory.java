package org.jcvi.jillion.trace.archive2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jcvi.jillion.core.io.IOUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlTraceArchiveInfoFactory {

	private XmlTraceArchiveInfoFactory(){
		//can not instantiate
	}
	public static TraceArchiveInfo create(File traceinfoXml) throws IOException{
		DefaultTraceArchiveInfo.Builder builder = new DefaultTraceArchiveInfo.Builder();
		
		TraceInfoParser parser = new TraceInfoParser(builder);
		 // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        InputStream inputStream = null;
        try {
         inputStream = new FileInputStream(traceinfoXml);
       // Parse the input 
          SAXParser saxParser = factory.newSAXParser();
          saxParser.parse( inputStream, parser ); 
          return builder.build();
        } 
        catch (Throwable t) {
            throw new IOException("error parsing traceInfo", t);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
	}
	
	private static class TraceInfoParser extends DefaultHandler{
        private final DefaultTraceArchiveInfo.Builder instance;
        
        private DefaultTraceArchiveRecord.Builder currentArchiveRecordBuilder;
        private String currentAttributeName;
        private boolean inExtendedData=false;
        
        private StringBuilder  characterBuffer =new StringBuilder();
       
        /**
         * Default Constructor.  
         * @param inputStream the inputStream where the TraceInfo.xml data resides.
         * @throws IOException 
         */
        public TraceInfoParser(DefaultTraceArchiveInfo.Builder instance) {
            
            this.instance = instance;
           
            
        }

        
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {      
            if(characterBuffer !=null){
                characterBuffer.append(new String(ch, start, length));
            }
        }
       
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            String elementName = getElementName(localName, name);
            if(inExtendedData && TraceInfoField.EXTENDED_DATA.toString().equals(elementName)){
                inExtendedData=false;
                characterBuffer=null;
            }
            else if( characterBuffer!=null &&currentArchiveRecordBuilder !=null && currentAttributeName !=null){
                if(inExtendedData){
                    currentArchiveRecordBuilder.putExtendedData(currentAttributeName, characterBuffer.toString());
                }
                else{
                    currentArchiveRecordBuilder.put(TraceInfoField.parseTraceInfoField(currentAttributeName), characterBuffer.toString());
                }
                characterBuffer=null;
           }
            
            
            if("trace".equals(elementName)){  
                TraceArchiveRecord record = currentArchiveRecordBuilder.build();                
                instance.addRecord(record);
                currentArchiveRecordBuilder=null;
                currentAttributeName =null;  
            }
           
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String name,
                Attributes attributes) throws SAXException {
          
            String elementName = getElementName(localName, name);
            if(TraceInfoField.EXTENDED_DATA.toString().equals(elementName)){
                inExtendedData=true;
            }
            if("trace".equals(elementName)){  
                currentArchiveRecordBuilder = new DefaultTraceArchiveRecord.Builder();            
            }
            else if(currentArchiveRecordBuilder !=null){
                if(inExtendedData){
                    this.currentAttributeName = attributes.getValue("name");
                }
                else{
                    this.currentAttributeName = elementName;
                }
                characterBuffer =new StringBuilder();
            }
            
            
            
            
        }

        private String getElementName(String localName, String name) {
            String elementName = localName; 
            if ("".equals(elementName)){
                elementName = name; 
            }
            return elementName;
        }
        
    }
}
