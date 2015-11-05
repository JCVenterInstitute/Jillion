/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

public enum MateOrientation {

    
    /**
     * 3' end of one is adjacent to the 5'
     * end of the other.
     * <pre>
     * {@code
     * 5'   3' 5'   3'
     * ----->  ----->}
     * </pre>
     */
    NORMAL('N'),
    /**
     * 3' Ends are adjacent.
     * <pre>
     * {@code
     * 5'   3' 3'   5'
     * ----->  <-----}
     * </pre>
     */
    INNIE('I'),
    /**
     * 5' ends are adjacent.
     * <pre>
     * {@code
     * 3'   5' 5'   3'
     * <-----  ----->}
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
