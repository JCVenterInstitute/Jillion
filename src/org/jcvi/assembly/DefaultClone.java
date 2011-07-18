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
 * Created on Jan 12, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.common.core.seq.read.Read;

public class DefaultClone<T extends Read> implements Clone {

    private final String id;
    private final Set<T> reads;
    
    /**
     * @param id
     * @param reads
     */
    public DefaultClone(String id, Set<T> reads) {
        this.id = id;
        this.reads = new HashSet<T>(reads);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<T> getReads() {
        return Collections.unmodifiableSet(reads);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((reads == null) ? 0 : reads.hashCode());
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
        if (!(obj instanceof DefaultClone)) {
            return false;
        }
        DefaultClone other = (DefaultClone) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (reads == null) {
            if (other.reads != null) {
                return false;
            }
        } else if (!reads.equals(other.reads)) {
            return false;
        }
        return true;
    }

}
