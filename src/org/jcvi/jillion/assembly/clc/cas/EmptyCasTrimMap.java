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

package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;

/**
 * {@code EmptyCasTrimMap} is a {@link CasTrimMap}
 * implementation that does not have any
 * mappings.
 * @author dkatzel
 *
 *
 */
public final class EmptyCasTrimMap implements CasTrimMap{

    private static EmptyCasTrimMap INSTANCE = new EmptyCasTrimMap();
    
    public static EmptyCasTrimMap getInstance(){
        return INSTANCE;
    }
    private EmptyCasTrimMap(){}
    /**
     * Always returns trimmedFile.
     * @param trimmedFile the trimmed file.
     * @return the same trimmedFile as given.
     */
    @Override
    public File getUntrimmedFileFor(File trimmedFile) {
        return trimmedFile;
    }

}
