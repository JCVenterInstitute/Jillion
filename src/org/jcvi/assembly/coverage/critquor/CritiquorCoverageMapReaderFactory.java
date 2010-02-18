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
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

/**
 * Get the appropriate {@link CritquorCoverageMapReader}
 * based on File Extension.
 * @author dkatzel
 *
 *
 */
public class CritiquorCoverageMapReaderFactory {
    private static final CritquorCoverageMapReader EXCEL_READER = new ExcelCritquorCoverageMapReader();
    private static final CritquorCoverageMapReader CSV_READER = new CSVCritquorCoverageMapReader();
    
    public static CritquorCoverageMapReader getReaderFor(String fileName){
        if(fileName.endsWith(".xls")){
            return EXCEL_READER;
        }
        return CSV_READER;
    }
}
