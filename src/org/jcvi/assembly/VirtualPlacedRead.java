/*
 * Created on Apr 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

public interface VirtualPlacedRead<T extends PlacedRead> extends PlacedRead{

    T getRealPlacedRead();
    
    int getRealIndexOf(int virtualIndex);
    
    int getVirtualIndexOf(int realIndex);
}
