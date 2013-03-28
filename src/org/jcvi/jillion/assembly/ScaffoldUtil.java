/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public final class ScaffoldUtil {

	private ScaffoldUtil(){		
		//private constructor
	}
    public static Range convertGappedContigRangeToUngappedScaffoldRange(Contig<? extends AssembledRead> contig, Range gappedContigRange, Scaffold scaffold){
        NucleotideSequence consensus =contig.getConsensusSequence();
       Range ungappedRange = Range.of(consensus.getUngappedOffsetFor((int)gappedContigRange.getBegin()),
                consensus.getUngappedOffsetFor((int)gappedContigRange.getEnd())
                );
        
       return scaffold.convertContigRangeToScaffoldRange(contig.getId(), ungappedRange);
        
    }
}
