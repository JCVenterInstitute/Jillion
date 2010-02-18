package org.jcvi.sequence;

/**
 * The <code>SequenceDirection</code> is a declaration of expected
 * directionality for some sequence of bases.  Theoretically, the idea of
 * "forward" and "reverse" can often get rather confused.  In practice, these
 * ideas are used to standardize a set of sequences which share the same
 * orientation with respect to some reference.  Many sequences have no notion
 * of direction and this state is fully supported.
 * <p>
 * The primary uses for directionality are in finding or assigning sequence
 * mate pairs or in performing trimming with specific primers.
 *
 * @author jsitz
 * @author dkatzel
 */
public enum SequenceDirection
{
    /**
     * The sequence has an orientation which matches the directionality of
     * the reference.
     */
    FORWARD,
    /**
     * The sequence has an orientation opposite of the directionality of
     * the reference.
     */
    REVERSE,
    /**
     * Sequence does not have a direction
     * or the concept of
     * direction is meaningless.
     */
    NONE,
    /**
     * The Sequence has a direction,
     * but it is currently not known.
     */
    UNKNOWN;

    public static SequenceDirection parseSequenceDirection(String dirString){
        if(dirString.equals("-")){
            return SequenceDirection.REVERSE;
        }
        if(dirString.equals("+")){
            return SequenceDirection.FORWARD;
        }
        
        for (SequenceDirection dir : SequenceDirection.values())
        {
            if (dir.name().equalsIgnoreCase(dirString) ||
                dir.name().substring(0, 1).equalsIgnoreCase(dirString))
            {
                return dir;
            }
        }
        if(dirString.equalsIgnoreCase("TF")){
            return SequenceDirection.FORWARD;
        }
        if(dirString.equalsIgnoreCase("TR")){
            return SequenceDirection.REVERSE;
        }
        return SequenceDirection.UNKNOWN;
        
        
    }
    

    public String getCode()
    {
        return this.name().substring(0, 1);
    }

    public SequenceDirection oppositeOrientation(){
        if(this == FORWARD ){
            return REVERSE;
        }
        if(this == REVERSE){
            return FORWARD;
        }
        return this;
    }
}
