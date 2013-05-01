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

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * {@code PhdBallIdIterator} is a {@link StreamingIterator}
 * implementation that iterates over the ids of a phd ball (potentially large)
 * file while parsing it.  This should be the fastest and most
 * memory efficient way to iterate over a phd ball file that is not
 * already entirely stored in memory.
 * @author dkatzel
 *
 */
final class PhdBallIdIterator extends AbstractBlockingStreamingIterator<String>{
    private final File phdFile;
    private final DataStoreFilter filter;
    
    
    public static PhdBallIdIterator createNewIterator(File phdFile, DataStoreFilter filter){
    	PhdBallIdIterator iter= new PhdBallIdIterator(phdFile, filter);
        iter.start();
        return iter;
    }
    private PhdBallIdIterator(File phdFile, DataStoreFilter filter) {
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
			public PhdVisitor visitPhd(PhdBallVisitorCallback callback,
					String id, Integer version) {
        	   if(PhdBallIdIterator.this.filter.accept(id)){
					blockingPut(id);					
				}
				return null;
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
