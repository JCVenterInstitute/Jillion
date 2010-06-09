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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

public class DefaultWholeAssemblyAceTag implements WholeAssemblyAceTag {

    private final Date creationDate;
    private final String creator;
    private final String type;
    private final String data;
    
    /**
     * @param type
     * @param creator
     * @param creationDate
     * @param data
     */
    public DefaultWholeAssemblyAceTag(String type, String creator,
            Date creationDate, String data) {
        this.type = type;
        this.creator = creator;
        this.creationDate = new Date(creationDate.getTime());
        this.data = data;
    }

    @Override
    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultWholeAssemblyAceTag)) {
            return false;
        }
        DefaultWholeAssemblyAceTag other = (DefaultWholeAssemblyAceTag) obj;
        if (creationDate == null) {
            if (other.creationDate != null) {
                return false;
            }
        } else if (!creationDate.equals(other.creationDate)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultWholeAssemblyAceTag [creationDate=" + creationDate
                + ", creator=" + creator + ", data=" + data + ", type=" + type
                + "]";
    }

}
