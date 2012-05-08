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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.util.ObjectsUtil;

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
        if (this == obj){
            return true;
        }
        if (!(obj instanceof PrivateData)){
            return false;
        }
        final PrivateData other = (PrivateData) obj;
        return ObjectsUtil.allNull(getData(), other.getData())  
        		|| 
        		(ObjectsUtil.noneNull(getData(), other.getData()) &&
         Arrays.equals(getData().array(), other.getData().array()));

    }




}
