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
package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.trace.sanger.PositionSequence;

/**
 * {@code PhdBallIterator2} is a {@link StreamingIterator}
 * implementation that iterates over a phd ball (potentially large)
 * file while parsing it.  This should be the fastest and most
 * memory efficient way to iterate over a phd ball file that is not
 * already entirely stored in memory.
 * @author dkatzel
 *
 */
final class PhdBallIterator2 extends AbstractBlockingStreamingIterator<Phd>{
    private final File phdFile;
    private final DataStoreFilter filter;
    
    
    public static PhdBallIterator2 createNewIterator(File phdFile, DataStoreFilter filter){
    	PhdBallIterator2 iter= new PhdBallIterator2(phdFile, filter);
        iter.start();
        return iter;
    }
    private PhdBallIterator2(File phdFile, DataStoreFilter filter) {
        this.phdFile = phdFile;
        this.filter = filter;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        PhdBallVisitor2 visitor = new AbstractPhdBallVisitor2() {
            
           @Override
			public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback,
					String id) {
				return handlePhd(callback, id);
			}

			private PhdVisitor2 handlePhd(PhdBallVisitorCallback callback, String id) {
				if(!PhdBallIterator2.this.filter.accept(id)){
					return null;
				}
				return new AbstractPhdVisitor2(id) {
					
					@Override
					protected void visitPhd(String id, Integer version,
							NucleotideSequence basecalls, QualitySequence qualities,
							PositionSequence positions, Map<String, String> comments) {
						Phd phd = new DefaultPhd(id,
								basecalls,
		                         qualities,
		                         positions,
		                         comments
		                         );
		                 blockingPut(phd);
						
					}
				};
			}

			@Override
			public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback,
					String id, int version) {
				return handlePhd(callback, id);
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
