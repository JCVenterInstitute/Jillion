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

package org.jcvi.assembly.cas;

import java.io.File;

/**
 * {@code UnTrimmedExtensionTrimMap} is a {@link CasTrimMap}
 * which assumes the untrimmed version of the given file
 * is in the same directory and has the same file name plus 
 * an additional extension appended
 * to the end of the file name.  If no additional extension
 * is given, then the default extension is {@link UnTrimmedExtensionTrimMap#DEFAULT_UNTRIMMED_EXT}
 * 
 * @author dkatzel
 *
 *
 */
public class UnTrimmedExtensionTrimMap implements CasTrimMap{
    /**
     * The default file extension for the untrimmed version of the file
     * if none is provided.
     */
    private static final String DEFAULT_UNTRIMMED_EXT = ".untrimmed";
    
    private final String untrimmedExtension;
    /**
     * Create a new UnTrimmedExtensionTrimMap instance using the default
     * untrimmed extension.  This is the same as 
     * {@link #UnTrimmedExtensionTrimMap(String) new UnTrimmedExtensionTrimMap(DEFAULT_UNTRIMMED_EXT)}.
     * @see {@link UnTrimmedExtensionTrimMap#DEFAULT_UNTRIMMED_EXT}
     */
    public UnTrimmedExtensionTrimMap(){
        this(DEFAULT_UNTRIMMED_EXT);
    }
    /**
     * Create a new UnTrimmedExtensionTrimMap instance using the
     * file extension to specify the name of the untrimmed
     * version of the file.
     * @param untrimmedExtension the file extension to append for the 
     * untrimmed version.
     */
    public UnTrimmedExtensionTrimMap(String untrimmedExtension){
        this.untrimmedExtension = untrimmedExtension;
    }
    
    
    public String getUntrimmedExtension() {
        return untrimmedExtension;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public File getUntrimmedFileFor(File trimmedFile) {
        String trimmedFileName = trimmedFile.getName();
        String expectedUntrimmedFileName = trimmedFileName+ getUntrimmedExtension();
        File expectedUnTrimmedFile = new File(trimmedFile.getParent(), expectedUntrimmedFileName);
        if(expectedUnTrimmedFile.exists()){
            return expectedUnTrimmedFile;
        }
        return trimmedFile;
    }

}
