/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.assembly.PlacedRead;

public interface AcePlacedRead extends PlacedRead{

    PhdInfo getPhdInfo();
    
}
