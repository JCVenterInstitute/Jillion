/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.Distance;

public interface Library {

    String getId();
    Distance getDistance();
    MateOrientation getMateOrientation();
}
