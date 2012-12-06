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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Date;
/**
 * {@code PhdInfo} is a value class
 * that contains information
 * to link an ace read
 * to a record in the corresponding
 * phd file.
 * @author dkatzel
 */
public class DefaultPhdInfo implements PhdInfo {

    private final String traceName, phdName;
    private final Date phdDate;
    public DefaultPhdInfo(String traceName, String phdName, Date phdDate){
        this.traceName = traceName;
        this.phdName = phdName;
        this.phdDate = new Date(phdDate.getTime());
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
    @Override
    public Date getPhdDate() {
        //defensive copy
        return new Date(phdDate.getTime());
    }
    /**
     * Get the name of the phd file
     * that contains information
     * for this read.
     * @return a String; never null.
     */
    @Override
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
    @Override
    public String getTraceName() {
        return traceName;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phdDate == null) ? 0 : phdDate.hashCode());
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
        if (!(obj instanceof DefaultPhdInfo)){
            return false;
        }
        DefaultPhdInfo other = (DefaultPhdInfo) obj;
        if (phdDate == null) {
            if (other.phdDate != null){
                return false;
            }
        } else if (!phdDate.equals(other.phdDate)){
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
                + phdName + ", phdDate=" + phdDate + "]";
    }

}
