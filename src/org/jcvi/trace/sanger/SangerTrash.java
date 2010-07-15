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
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.util.HashMap;
import java.util.Map;

public enum SangerTrash {
    NONE(null, "Good Sequence"),
    CONTAMINATED(Character.valueOf('C'),"Contaminant found after processing" ),
    ECOLI(Character.valueOf('E'),"E. coli contamination"),
    INSERT(Character.valueOf('I'),"Insert Trash: Clear range is too short"),
    LOW(Character.valueOf('W'), "Low signal or low DNA concentration"),
    NOISE(Character.valueOf('B'),"High background noise"),
    P_GEM(Character.valueOf('P'),"pGEM contamination"),
    REACTION(Character.valueOf('X'),"Reaction error" ),
    S_CEREVISIAE(Character.valueOf('Y'),"S. cerevisiae contamination"),
    SUPERCEDED(Character.valueOf('M'),  "Sequence superceded by new sequence"),
    UNIDENTIFIED(Character.valueOf('U'),"Unidentified cause. File reading errors"),
    VECTOR(Character.valueOf('V'),"Vector found in clear range"),
    REARRANGEMENT(Character.valueOf('R'), "Rearrangement. Vector found in middle of sequence"),
    SHUTTLE(Character.valueOf('S'), "Shuttle vector contamination");
    private final Character code;
    private final String description;

    static final Map<Character, SangerTrash> trashMap;
    static{
        trashMap = new HashMap<Character, SangerTrash>();
        for(SangerTrash trash :SangerTrash.values()){
            if(trash == NONE){
                //sometimes we have a space which means null trash?
                trashMap.put(Character.valueOf(' '), trash);
            }
            else{
                trashMap.put(trash.getCode(), trash);
            }
        }
    }

    public static SangerTrash getTrashByCode(Character trashCode){

        SangerTrash trash= trashMap.get(trashCode);
        return (trash ==null)? SangerTrash.NONE: trash;
    }

    public static SangerTrash getMappedTrashByCode(Character trashCode){
        return trashMap.get(trashCode);
    }
    
    SangerTrash(Character code, String description){
        this.code = code;
        this.description = description;
    }
    /**
     * Retrieves the Trash Code.
     *
     * @return A {@link Character}
     */
    public Character getCode() {
        return code;
    }
    /**
     * Retrieves the description.
     *
     * @return A {@link String}
     */
    public String getDescription() {
        return description;
    }
}
