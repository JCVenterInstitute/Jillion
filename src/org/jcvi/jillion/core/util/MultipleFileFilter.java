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
package org.jcvi.jillion.core.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code MultipleFileFilter} is a {@link FileFilter}
 * implementation that wraps multiple
 * {@link FileFilter} delegates and will
 * only accept a File if all the delegates
 * accept it.
 * @author dkatzel
 *
 *
 */
final class MultipleFileFilter implements FileFilter{

    private final List<FileFilter> delegates;
    
    
    /**
     * @param delegates
     */
    public MultipleFileFilter(List<FileFilter> delegates) {
        this.delegates = new ArrayList<FileFilter>(delegates);
    }
    public MultipleFileFilter(FileFilter...fileFilters){
        this(Arrays.asList(fileFilters));
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean accept(File pathname) {
        for(FileFilter delegate : delegates) {
            if(!delegate.accept(pathname)){
                return false;
            }
        }
        return true;
    }

}
