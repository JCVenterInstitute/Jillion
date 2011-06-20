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
 * Created on Dec 11, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.util.Arrays;


/**
 * This is a wrapper around a byte array so that
 * less XML is required to serialize this
 * when used by <code>XMLEncoder</code>.
 * I created this class and similar classes
 * so I can serialize arrays using XMLEncoder
 * but cut down on file size.
 * If  <code>byte[] = new byte[]{30,30,30}</code> were to be serialized by XMLEncoder,
 *  it would be written as:
 * <p>
 * <code>
 *  &lt;array class="byte" length="3"&gt;<br>
      &lt;void index="0"&gt;<br>
       &lt;byte&gt;30&lt/byte&gt;<br>
      &lt;/void&gt;<br>
      &lt;void index="1"&gt;<br>
       &lt;byte&gt;30&lt/byte><br>
      &lt;/void&gt;<br>
      &lt;void index="2"&gt;<br>
       &lt;byte&gt;30&lt;/byte&gt;<br>
       &lt;/void&gt;<br>
       &lt;/array&gt;
       </code>

<p>
With this class, and by overwritting the XMLEncoder's PersistanceDelegate,
the same array could be serialized as:
<p>
<code>
&lt;object class="org.tigr.common.sequencingData.chromatogram.EncodedByteData"><br>
    &lt;string>
30,30,30&lt;/string&gt;<br>

&lt;/object&gt;
</code>
<p>
using this method, the size of a serialized XML representation of an {@link Trace} can be reduced by 90%.

 * @author dkatzel
 *
 *
 */
public class EncodedByteData {
    private byte data[];

    private static byte[] decodeData(String encodedData){
        if(encodedData ==null){
            return null;
        }
        int i=0;
        String dataValues[] = encodedData.split(",\\s*");
        byte[] result = new byte[dataValues.length];
        for(String datum : dataValues){
            byte value = Byte.parseByte(datum);
            result[i++] =    value;
        }
        return result;
    }

    public EncodedByteData(byte[] data){
        if(data ==null){
            throw new IllegalArgumentException("data can not be null");
        }
        //defensive copy
        this.data = Arrays.copyOf(data, data.length);
    }
    /**
     * Takes an encoded string array of the form:
     * "x, y, z" and turns it into an array
     * array[0]=x
     * array[1]=y
     * array[2] =z.
     *
     * @param encodedData
     * @throws NumberFormatException if there is a problem
     * parsing the byte data.
     */
    public EncodedByteData(String encodedData){
        this(decodeData(encodedData));
    }



    /**
     * @return Returns the data.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }


    /**
     * Encodes the array data of this object into a comma
     * separated list of values.
     * @return
     */
    public String encodeData(){
        String encodedData= Arrays.toString(data);
        //remove brackets
       return encodedData.replaceAll("\\[|\\]","");
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode(data);
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
        if (!(obj instanceof EncodedByteData)){
            return false;
        }
        final EncodedByteData other = (EncodedByteData) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

}

