/*
 * Created on Jun 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.Collection;

import org.jcvi.Builder;
import org.jcvi.assembly.Placed;

public interface CoverageRegionBuilder<P extends Placed> extends Builder<CoverageRegion<P>> {

   long start();
    
   boolean canSetEndTo(long end);
   long end();
    
   CoverageRegionBuilder<P> end(long end);
   
   CoverageRegionBuilder<P> add(P element);
   CoverageRegionBuilder<P> remove(P element);
   CoverageRegionBuilder<P> removeAll(Collection<P> elements);
}
