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
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

/**
 * Ace files can contain tags at the end of the file
 * which can inform consed tools of various features
 * or hints or additional information that can not
 * be explained in the standard ace file format.
 * @author dkatzel
 */
public interface AceTag{
    /**
     * Each tag has a type which is a free form string 
     * with no whitespace. 
     * @return A String never null.
     */
    String getType();
    /**
     * The program or tool that generated this tag.
     * @return a String never null.
     */
    String getCreator();
    /**
     * The date that this tag was created.
     * @return a {@link Date}; never null.
     */
    Date getCreationDate();
    /**
     * Get the data (not counting header info or comments) in the tag as a String.
     * @return the data or {@code null} if no Data exists.
     */
    String getData();
}
