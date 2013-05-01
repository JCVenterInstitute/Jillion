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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * {@code PhdBallIterator} is a {@link StreamingIterator}
 * implementation that iterates over a phd ball (potentially large)
 * file while parsing it.  This should be the fastest and most
 * memory efficient way to iterate over a phd ball file that is not
 * already entirely stored in memory.
 * @author dkatzel
 *
 */
final class PhdBallIterator extends AbstractBlockingStreamingIterator<Phd>{
    private final File phdFile;
    private final DataStoreFilter filter;
    
    
    public static PhdBallIterator createNewIterator(File phdFile, DataStoreFilter filter){
    	PhdBallIterator iter= new PhdBallIterator(phdFile, filter);
        iter.start();
        return iter;
    }
    private PhdBallIterator(File phdFile, DataStoreFilter filter) {
        this.phdFile = phdFile;
        this.filter = filter;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        PhdBallVisitor visitor = new AbstractPhdBallVisitor() {
            
        
        	@Override
			public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id, Integer version) {
				if(!PhdBallIterator.this.filter.accept(id)){
					return null;
				}
				return new AbstractPhdVisitor(id, version) {
					
					@Override
					protected void visitPhd(String id, Integer version,
							NucleotideSequence basecalls, QualitySequence qualities,
							PositionSequence positions, Map<String, String> comments,
							List<PhdWholeReadItem> wholeReadItems, List<PhdReadTag> readTags) {
						Phd phd = new DefaultPhd(id,
								basecalls,
		                         qualities,
		                         positions,
		                         comments,
		                         wholeReadItems,
		                         readTags
		                         );
		                 blockingPut(phd);
						
					}
				};
			}

			
        };
        
        try {
            PhdBallParser.create(phdFile).accept(visitor);
        } catch (IOException e) {
           throw new RuntimeException(
                   String.format("error parsing phd file: %s" ,phdFile.getAbsolutePath()),
                   e);
        }
    }

}
