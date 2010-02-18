/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.Range;

public class AmpliconCritiquorFileVisitor implements CritiquorFileVisitor {
    private final Set<CritiquorAmplicon> amplicons = new HashSet<CritiquorAmplicon>();
    @Override
    public void visitAmplicon(String id, String region, Range designedRange,
            String forwardPrimer, String reversePrimer) {
        CritiquorAmplicon amp = new DefaultCritiquorAmplicon(id, region, designedRange, forwardPrimer, reversePrimer);
        amplicons.add(amp);

    }

    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitStartOfFile() {
        // TODO Auto-generated method stub
        
    }

    public Set<CritiquorAmplicon> getAmplicons() {
        return amplicons;
    }
    
    

}
