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
package org.jcvi.jillion.core.residue;

import java.util.Arrays;
import java.util.List;

public enum Frame{
    ONE(1),
    TWO(2),
    THREE(3),
    
    
    NEGATIVE_ONE(-1){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_TWO(-2){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    },
    NEGATIVE_THREE(-3){

        @Override
        public boolean onReverseStrand() {
            return true;
        }
        
    }
    ;
    
    private int frame;
    
    private static final Frame[] VALUES = values();
    private static final List<Frame> FORWARDS = Arrays.asList(ONE, TWO, THREE);
    private static final List<Frame> REVERSE = Arrays.asList(NEGATIVE_ONE, NEGATIVE_TWO, NEGATIVE_THREE);
    
    public  final int getFrame() {
        return frame;
    }
    Frame(int frame){
        this.frame = frame;
    }
    /**
     * Parse a {@link Frame} from the given int value.
     * Valid values are <code>0</code> to <code>2</code>
     * inclusive.
     * @param frame the frame number as an int (1, 2, 3, -1, -2, -3).
     * 
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame &lt; 0 || frame &gt; 2</code>
     */
    public static Frame parseFrame(int frame){
        for(Frame f : Frame.values()){
            if(f.frame == frame){
                return f;
            }
        }
     
        throw new IllegalArgumentException("unable to parse frame " + frame);
    }
    
    
    public static List<Frame> forwardFrames(){
        return FORWARDS;
    }
    
    public static List<Frame> reverseFrames(){
        return REVERSE;
    }
    public boolean onReverseStrand() {
        // overridden by -1, -2 and -3
        return false;
    }
    
    public Frame getOppositeFrame(){
        return VALUES[ (this.ordinal() +3)%VALUES.length];
    }
}
