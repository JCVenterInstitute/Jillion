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
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf;

import java.util.Arrays;

import org.jcvi.jillion.trace.sanger.chromat.scf.PrivateData;

/**
 * PrivateData is a wrapper around a byte array
 * for additional optional data in an scf file.
 * The scf file specification puts no limitations
 * on what this data can contain and is implementation 
 * specific.  It is up to different scf writer implementations
 * to decide what data to put here (if any) and how to encode it. 
 * 
 * @author dkatzel
 *
 */
public final class PrivateDataImpl implements PrivateData {

    private final byte[] data;

    public PrivateDataImpl(byte[] data){
        this.data =Arrays.copyOf(data, data.length);
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
	public byte[] getBytes() {
    	//defensive copy
        return Arrays.copyOf(data, data.length);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
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
        return
         Arrays.equals(data, other.getBytes());

    }




}
