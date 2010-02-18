/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;

public interface PlacedContig extends Placed {

    String getContigId();
    SequenceDirection getSequenceDirection();
    
    Range getValidRange();
    
}
