/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
   
   public  boolean isEndIsSet();
   
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
