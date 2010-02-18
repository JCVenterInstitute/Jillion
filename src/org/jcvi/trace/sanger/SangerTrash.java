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
    REARRANGEMENT(Character.valueOf('R'), "Rearrangement. Vector found in middle of sequence");
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
