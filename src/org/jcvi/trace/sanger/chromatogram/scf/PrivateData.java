/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.CommonUtil;

public class PrivateData {

    private ByteBuffer data;

    public PrivateData(ByteBuffer data){
        this.data = data;
    }

    public PrivateData(byte[] data){
        this(ByteBuffer.wrap(data));
    }

    /**
     * @return the data
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getData() == null) ? 0 : Arrays.hashCode(getData().array()));
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PrivateData)){
            return false;
        }
        final PrivateData other = (PrivateData) obj;
        return CommonUtil.bothNull(getData(), other.getData())  
                ||        
         (!CommonUtil.onlyOneIsNull(getData(), other.getData()) 
                && 
         Arrays.equals(getData().array(), other.getData().array()));

    }




}
