/*
 * Created on Dec 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.conflict;

import org.jcvi.assembly.Placed;

public interface ConflictedRegion extends Placed{

    Conflict getConflict();
}
