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
/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.experimental.trace.archive2;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DefaultTraceArchiveRecord implements TraceArchiveRecord {
   private final Map<TraceInfoField,String> map;
   private final Map<String,String> extendedData;
   protected DefaultTraceArchiveRecord(Map<TraceInfoField,String> map,Map<String,String> extendedData){
       if(map ==null){
           throw new IllegalArgumentException("map can not be null");
       }
       if(extendedData ==null){
           throw new IllegalArgumentException("extendedData can not be null");
       }
       this.map = map;
       this.extendedData = extendedData;
   }
   @Override
   public Map<String, String> getExtendedData() {
       return Collections.unmodifiableMap(extendedData);
   }
    @Override
    public Set<Entry<TraceInfoField, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String getAttribute(TraceInfoField traceInfoField) {
        return map.get(traceInfoField);
    }
    @Override
    public boolean contains(TraceInfoField traceInfoField) {
        return map.containsKey(traceInfoField);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + map.entrySet().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }       
        if (!(obj instanceof DefaultTraceArchiveRecord)){
            return false;
        }           
        DefaultTraceArchiveRecord other = (DefaultTraceArchiveRecord) obj;
        Map<TraceInfoField, String> otherMap = new HashMap<TraceInfoField, String>();
        for(Entry<TraceInfoField, String> entry : other.entrySet()){
        	otherMap.put(entry.getKey(), entry.getValue());
        }
        return map.equals(otherMap);
    }



    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(Entry<TraceInfoField, String> entry : map.entrySet()){
    		builder.append(String.format("%s = %s%n", entry.getKey(), entry.getValue()));
    	}
        return builder.toString();
    }



    public static class Builder implements TraceArchiveRecordBuilder{
        private final Map<TraceInfoField,String> map = new LinkedHashMap<TraceInfoField, String>();
        private final Map<String,String> extendedData = new HashMap<String, String>();
        /**
         * Create a new Builder instance.
         */
        public Builder(){
            super();
        }
        
        public Builder(TraceArchiveRecord record){
            for(Entry<TraceInfoField,String> entry: record.entrySet()){
                map.put(entry.getKey(),entry.getValue());
            }
            for(Entry<String,String> extendedDataEntry: record.getExtendedData().entrySet()){
                extendedData.put(extendedDataEntry.getKey(),extendedDataEntry.getValue());
            }
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public TraceArchiveRecordBuilder put(TraceInfoField traceInfoField, String value){
            map.put(traceInfoField, value);
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public TraceArchiveRecordBuilder putExtendedData(String key, String value){
            extendedData.put(key, value);
            return this;
        }
        public TraceArchiveRecordBuilder removeExtendedData(String key){
            extendedData.remove(key);
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public TraceArchiveRecordBuilder putAll(Map<TraceInfoField,String> map){
            this.map.putAll(map);
            return this;
        }
        public TraceArchiveRecordBuilder remove(TraceInfoField traceInfoField){
            map.remove(traceInfoField);
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
		@Override
        public TraceArchiveRecord build() {
            return new DefaultTraceArchiveRecord(map,extendedData);
        }        
        
    }



    

}
