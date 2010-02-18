/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

public class DefaultPhdInfo implements PhdInfo {

    private final String traceName, phdName;
    private final Date phdDate;
    DefaultPhdInfo(String traceName, String phdName, Date phdDate){
        this.traceName = traceName;
        this.phdName = phdName;
        this.phdDate = phdDate;
    }
    @Override
    public Date getPhdDate() {
        //defensive copy
        return new Date(phdDate.getTime());
    }

    @Override
    public String getPhdName() {
        return phdName;
    }

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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultPhdInfo))
            return false;
        DefaultPhdInfo other = (DefaultPhdInfo) obj;
        if (phdDate == null) {
            if (other.phdDate != null)
                return false;
        } else if (!phdDate.equals(other.phdDate))
            return false;
        if (phdName == null) {
            if (other.phdName != null)
                return false;
        } else if (!phdName.equals(other.phdName))
            return false;
        if (traceName == null) {
            if (other.traceName != null)
                return false;
        } else if (!traceName.equals(other.traceName))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "DefaultPhdInfo [traceName=" + traceName + ", phdName="
                + phdName + ", phdDate=" + phdDate + "]";
    }

}
