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
 * Created on Dec 11, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import java.util.Arrays;

import org.jcvi.jillion.trace.Trace;


/**
 * This is a wrapper around a short array so that
 * less XML is required to serialize this
 * when used by <code>XMLEncoder</code>.
 * I created this class and similar classes
 * so I can serialize arrays using XMLEncoder
 * but cut down on file size.
 * If  <code>short[] = new short[]{30,30,30}</code> were to be serialized by XMLEncoder,
 *  it would be written as:
 * <p>
 * <code>
 *  &lt;array class="short" length="3"&gt;<br>
      &lt;void index="0"&gt;<br>
       &lt;short&gt;30&lt/short&gt;<br>
      &lt;/void&gt;<br>
      &lt;void index="1"&gt;<br>
       &lt;short&gt;30&lt/short><br>
      &lt;/void&gt;<br>
      &lt;void index="2"&gt;<br>
       &lt;short&gt;30&lt;/short&gt;<br>
       &lt;/void&gt;<br>
       &lt;/array&gt;
       </code>

<p>
With this class, and by overwritting the XMLEncoder's PersistanceDelegate,
the same array could be serialized as:
<p>
<code>
&lt;object class="org.tigr.common.sequencingData.chromatogram.EncodedShortData"><br>
    &lt;string>
30,30,30&lt;/string&gt;<br>

&lt;/object&gt;
</code>
<p>
using this method, the size of a serialized XML representation of an {@link Trace} can be reduced by 90%.
* @author dkatzel
*/
final class EncodedShortData {
    private short data[];

    private static short[] decodeData(String encodedData){
        if(encodedData ==null){
            return null;
        }
        int i=0;

        String dataValues[] = encodedData.split(",\\s*");
        short[] result = new short[dataValues.length];
        for(String datum : dataValues){
            short value = Short.parseShort(datum);
            result[i++] =    value;
        }
        return result;
    }
    public EncodedShortData(short[] data){
        if(data ==null){
            throw new IllegalArgumentException("data can not be null");
        }
        this.data = Arrays.copyOf(data, data.length);
    }
    public EncodedShortData(String encodedData){
        this(decodeData(encodedData));
    }

    /**
     * @return Returns the data.
     */
    public short[] getData() {
        return Arrays.copyOf(data, data.length);
    }



    public String encodeData(){
        String encodedData= Arrays.toString(data);
       return encodedData.replaceAll("\\[|\\]","");
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof EncodedShortData)){
            return false;
        }
        final EncodedShortData other = (EncodedShortData) obj;
        if (!Arrays.equals(data, other.data)){
            return false;
        }
        return true;
    }

}
