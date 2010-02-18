/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

public enum MateOrientation {

    
    /**
     * 3' end of one is adjacent to the 5'
     * end of the other.
     * <pre>
     * ----->  ----->
     * </pre>
     */
    NORMAL('N'),
    /**
     * 3' Ends are adjacent.
     * <pre>
     * ----->  <-----
     * </pre>
     */
    INNIE('I'),
    /**
     * 5' ends are adjacent.
     * <pre>
     * <-----  ----->
     * </pre>
     */
    OUTTIE('O'),
    /**
     * Mates are not supported.
     */
    UNORIENTED('U');
    private final char character;
    
    public char getCharacter() {
        return character;
    }

    private MateOrientation(char c){
        this.character = c;
    }
    
    public static MateOrientation parseMateOrientation(char c){
        switch(c){
            case 'I' : return INNIE;
            case 'O' : return OUTTIE;
            case 'N' : return NORMAL;
            default : return UNORIENTED;
        }
    }
    public static MateOrientation parseMateOrientation(String s){
        return parseMateOrientation(s.charAt(0));
    }
}
