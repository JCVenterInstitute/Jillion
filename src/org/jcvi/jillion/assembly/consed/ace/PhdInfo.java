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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;
/**
 * {@code PhdInfo} is a value class
 * that contains information
 * to link an ace read
 * to a record in the corresponding
 * phd file.
 * @author dkatzel
 */
public final class PhdInfo {

    private final String traceName, phdName;
    private final long phdDateMillis;
    /**
     * Create a new {@link PhdInfo} instance.
     * @param traceName the name of the trace
	 * in the phd file.  This is usually
	 * the read id or a variation
	 * of the read id; can not be null.
     * @param phdName Get the name of the phd file
     * that contains information
     * for this read; can not be null.
     * @param phdDate Get the {@link Date}
     * that this phd file was
     * last modified; can not be null.
     * This class will make a defensive copy
     * of this input since {@link Date}
     * is mutable.
     * @throws NullPointerException if any of these inputs
     * is null.
     */
    public PhdInfo(String traceName, String phdName, Date phdDate){
    	if(traceName ==null){
    		throw new NullPointerException("trace name can not be null");
    	}
    	if(phdName ==null){
    		throw new NullPointerException("phd name can not be null");
    	}
    	if(phdDate ==null){
    		throw new NullPointerException("phd date can not be null");
    	}
        this.traceName = traceName;
        this.phdName = phdName;
        this.phdDateMillis = phdDate.getTime();
    }
    /**
     * Get the {@link Date}
     * that this phd file was
     * last modified.
     * @return a {@link Date};
     * never null.The implementations
     * of method
     * might return new (but equal) 
     * Date instances
     * each time it is called.
     */
    public Date getPhdDate() {
        //defensive copy
        return new Date(phdDateMillis);
    }
    /**
     * Get the name of the phd file
     * that contains information
     * for this read.
     * @return a String; never null.
     */
    public String getPhdName() {
        return phdName;
    }
    /**
	 * Get the name of the trace
	 * in the phd file.  This is usually
	 * the read id or a variation
	 * of the read id.
	 * @return a String;
	 * never null.
	 */
    public String getTraceName() {
        return traceName;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (phdDateMillis ^ (phdDateMillis >>> 32));
        result = prime * result + ((phdName == null) ? 0 : phdName.hashCode());
        result = prime * result
                + ((traceName == null) ? 0 : traceName.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof PhdInfo)){
            return false;
        }
        PhdInfo other = (PhdInfo) obj;
        if (phdDateMillis != other.getPhdDate().getTime()){
            return false;
        }
        if (phdName == null) {
            if (other.phdName != null){
                return false;
            }
        } else if (!phdName.equals(other.phdName)){
            return false;
        }
        if (traceName == null) {
            if (other.traceName != null){
                return false;
            }
        } else if (!traceName.equals(other.traceName)){
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "DefaultPhdInfo [traceName=" + traceName + ", phdName="
                + phdName + ", phdDate=" + phdDateMillis + "]";
    }

}
