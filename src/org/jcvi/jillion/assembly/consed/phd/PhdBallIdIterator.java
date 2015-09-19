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
            PhdBallFileParser.create(phdFile).accept(visitor);
        } catch (IOException e) {
           throw new RuntimeException(
                   String.format("error parsing phd file: %s" ,phdFile.getAbsolutePath()),
                   e);
        }
    }

}
