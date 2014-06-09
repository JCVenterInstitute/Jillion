package org.jcvi.jillion.core.residue;

public enum Frame{
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
     * Valid values are <code>0</code> to <code>2</code>
     * inclusive.
     * @param frame
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame < 0 || frame > 2</code>
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