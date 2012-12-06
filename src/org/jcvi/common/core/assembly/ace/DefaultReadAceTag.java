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
package org.jcvi.common.core.assembly.ace;

import java.util.Date;

import org.jcvi.common.core.Rangeable;
/**
 * {@code DefaultReadAceTag} is an {@link AceTag}
 * that maps to a particular location on a specific
 * read of a contig in an ace file.
 * @author dkatzel
 */
public class DefaultReadAceTag extends AbstractDefaultPlacedAceTag{

    public DefaultReadAceTag(String id, String type, String creator,
            Date creationDate, Rangeable location, boolean isTransient) {
        super(id, type, creator, creationDate, location, null, isTransient);
    }

	@Override
	public String toString() {
		return "DefaultReadAceTag [getId()=" + getId() + ", getType()="
				+ getType() + ", getCreator()=" + getCreator()
				+ ", getCreationDate()=" + getCreationDate() + ", asRange()="
				+ asRange() + ", getData()=" + getData() + ", isTransient()="
				+ isTransient() + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)){
			return false;
		}
		return obj instanceof DefaultReadAceTag;
	}
    
    

}
