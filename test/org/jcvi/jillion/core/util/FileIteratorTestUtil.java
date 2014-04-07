/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author dkatzel
 *
 *
 */
public class FileIteratorTestUtil {
    /**
     * A {@link FileFilter} that only accepts
     * Files whose names end with "2"
     */
    public static final FileFilter FILE_FILTER_ANYTHING_THAT_DOESNT_END_WITH_2 = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith("2");
        }
    };
}
