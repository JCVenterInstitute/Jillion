/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.List;

public interface Slice extends Iterable<SliceElement>{
    List<SliceElement> getSliceElements();
    int getCoverageDepth();
    boolean containsElement(String elementId);
    SliceElement getSliceElement(String elementId);
}
