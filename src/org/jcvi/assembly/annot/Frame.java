/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;
/**
 * A {@link Frame} is an object representation
 * of a reading frame.
 * @author dkatzel
 *
 *
 */
public enum Frame {

    
    NO_FRAME(-1),
    ZERO(0),
    ONE(1),
    TWO(2);
    
    private int frame;
    
    public  final int getFrame() {
        return frame;
    }
    Frame(int frame){
        this.frame = frame;
    }
    /**
     * Parse a {@link Frame} from the given int value.
     * Valid values are <code>-1</code> to <code>2</code>
     * inclusive.
     * @param frame
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame < -1 || frame > 2</code>
     */
    public static Frame parseFrame(int frame){
        for(Frame f : Frame.values()){
            if(f.frame == frame){
                return f;
            }
        }
     
        throw new IllegalArgumentException("unable to parse frame " + frame);
    }
}
