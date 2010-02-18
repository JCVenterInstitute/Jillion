/**
 * Coordinate.java
 *
 * Created: Aug 11, 2009 - 10:29:30 AM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.align;

/**
 * A <code>Coordinate</code> represents the address of a single element in a two-dimensional
 * matrix. 
 * <p>
 * <em>Note:</em>  This object is immutable by design.  This object was designed as a 
 * lightweight object allowing API's to accept a pair of int in a single object in order to 
 * clarify the purpose of the coordinates.  Normally, when these coordinates are passed, they
 * would be marked as <code>final</code> or treated as such by policy.  By wrapping this pair
 * in a single object, but allowing mutators on the object, the read-only policy of the 
 * abstracted data has been removed (or at the very least, weakened).  Instead, each instance
 * provides relative factory methods which return new copies of nearby coordinates.  Not only
 * does this enforce good faith design on the part of the developer, but it maintains thread
 * safe usage of this class.
 *
 * @author jsitz@jcvi.org
 */
public class Coordinate
{
    /** The primary (horizontal) component of the coordinate. */
    public final int x;
    /** The secondary (vertical) component of the coordinate. */
    public final int y;
    
    /**
     * Creates a new <code>Coordinate</code>.
     * 
     * @param x The primary component.
     * @param y The secondary component.
     */
    public Coordinate(int x, int y)
    {
        super();
        
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns a translated version of this <code>Coordinate</code>.  This is a new coordinate
     * and the current one is left unchanged.
     * 
     * @param deltaX The positive or negative change in the primary component.
     * @param deltaY The positive or negative change in the secondary component.
     * @return A new <code>Coordinate</code>.
     */
    public Coordinate translate(int deltaX, int deltaY)
    {
        return new Coordinate(this.x + deltaX, this.y + deltaY);
    }
    
    /**
     * Returns the <code>Coordinate</code> which is above this <code>Coordinate</code> on the 
     * secondary axis.
     * 
     * @return A new <code>Coordinate</code>.
     */
    public Coordinate up()
    {
        return this.translate(0, -1);
    }
    
    /**
     * Returns the <code>Coordinate</code> which is below this <code>Coordinate</code> on the 
     * secondary axis.
     * 
     * @return A new <code>Coordinate</code>.
     */
    public Coordinate down()
    {
        return this.translate(0, 1);
    }
    
    /**
     * Returns the <code>Coordinate</code> which is left of this <code>Coordinate</code> on the 
     * primary axis.
     * 
     * @return A new <code>Coordinate</code>.
     */
    public Coordinate left()
    {
        return this.translate(-1, 0);
    }
    
    /**
     * Returns the <code>Coordinate</code> which is to the right of this <code>Coordinate</code>
     * on the primary axis.
     * 
     * @return A new <code>Coordinate</code>.
     */
    public Coordinate right()
    {
        return this.translate(1, 0);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "[ " + this.x + " , " + this.y + "]";
    }
}
