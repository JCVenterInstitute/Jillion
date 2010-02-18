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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author dkatzel
 *
 *
 */
public class FakeTigrSeqnameMatedTraceIdGenerator implements MatedTraceIdGenerator<String, String>{

    private static final String FORWARD_SUFFIX = "TF";
    private static final String REVERSE_SUFFIX = "TR";
    
    private static final int NUMBER_OF_WELLS = 26*99;
    private static final int NUMBER_OF_LIB_ID_POSITION = 35;
    
    private final String libraryPrefix;
    private final List<String> excludedLibraries = new ArrayList<String>();
    
    private int wellCounter=0;
    public FakeTigrSeqnameMatedTraceIdGenerator(String libraryPrefix, List<String> excludedLibraries){
        if(libraryPrefix.length() !=4){
            throw new IllegalArgumentException("library prefix must be 4 letters long");
        }
        int wellsToSkip = (computeLibraryPositionFrom(libraryPrefix.charAt(2))* 35 +computeLibraryPositionFrom(libraryPrefix.charAt(3))) * NUMBER_OF_WELLS;
        wellCounter += wellsToSkip;
        this.libraryPrefix = libraryPrefix.substring(0, 2);
        this.excludedLibraries.addAll(excludedLibraries);
    }
    
    @Override
    public List<String> generateIdsFor(String forward, String reverse) {
        return generateIdsAndIncrementCounter(reverse !=null);
    }
    
    protected List<String> generateIdsAndIncrementCounter(boolean makeReverseNameToo){
        String tigrNamePrefix = computeTigrSeqnamePrefix();
        List<String> result = new ArrayList<String>(2);
        result.add(tigrNamePrefix+ FORWARD_SUFFIX);
        if(makeReverseNameToo){
            result.add(tigrNamePrefix+ REVERSE_SUFFIX);
        }
        incrementCounterAndPositions();
        return result;
    }
    private void incrementCounterAndPositions() {
        wellCounter++;
        
    }

    public int getWellCounter() {
        return wellCounter;
    }

    protected String computeTigrSeqnamePrefix(){
        int wellCounter = getWellCounter();
        int libIdPosition = wellCounter /NUMBER_OF_WELLS;
        String librarySuffix = new StringBuilder()
                                .append(computeLibraryLetterFrom(libIdPosition / NUMBER_OF_LIB_ID_POSITION))
                                .append(computeLibraryLetterFrom(libIdPosition % NUMBER_OF_LIB_ID_POSITION)).toString();
        if(excludedLibraries.contains(librarySuffix)){
            this.wellCounter+=NUMBER_OF_WELLS;
            return computeTigrSeqnamePrefix();
        }
        return new StringBuilder(libraryPrefix)                    
                    .append(librarySuffix)
                    .append(computeWellPositionFrom(wellCounter % NUMBER_OF_WELLS))
                    .toString();
    }
    public static String computeWellPositionFrom(int counter){
        char well = (char)((counter/100) +'A');
        int wellNum = counter%100;
        return String.format("%s%02d",well, wellNum);
    }
    public static char computeLibraryLetterFrom(int position){
        int asciiCode = position+48;
        if(asciiCode > '9'){
            //skip over special chars between ascii '9' and ascii 'A'
            asciiCode += 7;
        }
        return (char)asciiCode;
    }
    public static int computeLibraryPositionFrom(char character){
        int offset = character -'0';
        if(offset > 9){
            //skip over special chars between ascii '9' and ascii 'A'
            offset -= 7;
        }
        return offset;
    }
}
