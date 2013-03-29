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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jcvi.jillion.core.io.IOUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TraceInfoXMLTraceArchiveInfoBuilder implements TraceArchiveInfoBuilder<TraceArchiveRecord>{

    private final TraceArchiveInfoBuilder<TraceArchiveRecord> builder;
    public TraceInfoXMLTraceArchiveInfoBuilder(TraceArchiveRecordIdGenerator recordIdGenerator, InputStream inputStream) throws IOException{
        this(new DefaultTraceArchiveInfoBuilder(), recordIdGenerator, inputStream);
    }
    public TraceInfoXMLTraceArchiveInfoBuilder(TraceArchiveInfoBuilder<TraceArchiveRecord> builder,TraceArchiveRecordIdGenerator recordIdGenerator, InputStream inputStream) throws IOException{
        this.builder = builder;
        new TraceInfoParser<TraceArchiveRecord>(this,recordIdGenerator,inputStream);
    }
    @Override
    public Map<String, TraceArchiveRecord> getTraceArchiveRecordMap() {
       return builder.getTraceArchiveRecordMap();
    }

    @Override
    public TraceArchiveInfoBuilder put(String id, TraceArchiveRecord record) {
        builder.put(id, record);
        return this;
    }

    @Override
    public TraceArchiveInfoBuilder putAll(Map<String, TraceArchiveRecord> map) {
        builder.putAll(map);
        return this;
    }

    @Override
    public TraceArchiveInfoBuilder remove(String id) {
        builder.remove(id);
        return this;
    }

    @Override
    public TraceArchiveInfoBuilder removeAll(Collection<String> ids) {
        builder.removeAll(ids);
        return this;
    }

    
    @Override
	public TraceArchiveInfo build() {
		return builder.build();
	}


	private static class TraceInfoParser<T extends TraceArchiveRecord> extends DefaultHandler{
        private final TraceArchiveRecordIdGenerator recordIdGenerator;
        private final TraceInfoXMLTraceArchiveInfoBuilder instance;
        
        private DefaultTraceArchiveRecord.Builder currentArchiveRecordBuilder;
        private String currentAttributeName;
        private boolean inExtendedData=false;
        
        private StringBuilder  characterBuffer =new StringBuilder();
       
        /**
         * Default Constructor.  
         * @param inputStream the inputStream where the TraceInfo.xml data resides.
         * @throws IOException 
         */
        public TraceInfoParser(TraceInfoXMLTraceArchiveInfoBuilder instance,
                TraceArchiveRecordIdGenerator recordIdGenerator, 
                InputStream inputStream) throws IOException {
            
            this.instance = instance;
            this.recordIdGenerator = recordIdGenerator;
            // Use the default (non-validating) parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
             
           // Parse the input 
              SAXParser saxParser = factory.newSAXParser();
              saxParser.parse( inputStream, this ); 
              
            } 
            catch (Throwable t) {
                throw new IOException("error parsing traceInfo", t);
            }
            finally{
                IOUtil.closeAndIgnoreErrors(inputStream);
            }
            
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
                final String generatedId = recordIdGenerator.generateIdFor(record);
                instance.put(generatedId, new DefaultTraceArchiveRecord.Builder(record)
                                                .put(TraceInfoField.TRACE_NAME,generatedId).build());
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
