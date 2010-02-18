/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.CommonUtil;

public class DefaultConfidence implements Confidence {

    private byte[] data;

    public DefaultConfidence(ByteBuffer data){
        this(data.array());
    }
    public DefaultConfidence(byte[] data){
        this.data = Arrays.copyOf(data,data.length);
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return Arrays.copyOf(data,data.length);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : Arrays.hashCode(data));
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultConfidence)){
            return false;
        }
        final Confidence other = (Confidence) obj;
        return CommonUtil.bothNull(getData(), other.getData())  
                            ||        
            (!CommonUtil.onlyOneIsNull(getData(), other.getData()) 
                            && 
            Arrays.equals(getData(), other.getData()));

    }


}
