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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;
/**
 * A {@link WholeAssemblyAceTag} is an {@link AceTag}
 * that applies to the entire assembly in the ace file
 * usually these types of tags refer to things like
 * where the phdball is located. Other uses of 
 * Whole Assembly tags are to store version information
 * about how this assembly was made.
 * @author dkatzel
 *
 *
 */
public final class WholeAssemblyAceTag extends AbstractDefaultAceTag {

    
    /**
     * @param type
     * @param creator
     * @param creationDate
     * @param data
     */
    public WholeAssemblyAceTag(String type, String creator,
            Date creationDate, String data) {
        super(type, creator, creationDate, data);
    }

    


    @Override
    public int hashCode() {
       return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
    	
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WholeAssemblyAceTag)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "DefaultWholeAssemblyAceTag [creationDate=" + getCreationDate()
                + ", creator=" + getCreator() + ", data=" + getData() + ", type=" + getType()
                + "]";
    }

}
