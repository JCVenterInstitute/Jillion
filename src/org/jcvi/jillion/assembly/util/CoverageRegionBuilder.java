/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.Collection;

import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.util.Builder;

interface CoverageRegionBuilder<P extends Rangeable> extends Builder<CoverageRegion<P>>, Rangeable {

   long start();
   
   boolean isEndIsSet();
   
   boolean canSetEndTo(long end);
   long end();
    
   CoverageRegionBuilder<P> end(long end);
   
   boolean offer(P element);
   
   CoverageRegionBuilder<P> add(P element);
   
   CoverageRegionBuilder<P> remove(P element);
   CoverageRegionBuilder<P> removeAll(Collection<P> elements);
   
   CoverageRegionBuilder<P> shift(long shift);
   
   Collection<P> getElements();
   
   int getCurrentCoverageDepth();
   
   boolean hasSameElementsAs(CoverageRegionBuilder<P> other);

   boolean rangeIsEmpty();
   
   void forceAdd(P element);

}
