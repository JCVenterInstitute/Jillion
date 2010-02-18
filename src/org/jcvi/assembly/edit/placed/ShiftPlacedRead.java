/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.placed;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.edit.EditException;

public abstract class ShiftPlacedRead<P extends PlacedRead> extends AbstractPlacedReadEdit<P> {

    private final long shiftAmount;
    
    /**
     * @param shiftAmount
     */
    public ShiftPlacedRead(int shiftAmount) {
        this.shiftAmount = shiftAmount;
    }

    @Override
    public P performEdit(P original) throws EditException {
        long newOffset = original.getStart()+shiftAmount;
        return createNewPlacedRead(original.getEncodedGlyphs(), newOffset, original.getValidRange(), original.getSequenceDirection());
    }

    
}
