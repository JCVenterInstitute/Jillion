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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.junit.Test;
public abstract class AbstractTestSffNucleotideDataStore extends AbstractTestExampleSffFile{

    private final SffFileDataStore dataStore;
    
    {
        
        try {
        	dataStore = DefaultSffFileDataStore.create(SFF_FILE);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    
    protected abstract NucleotideSequenceDataStore createSut(File sffFile) throws Exception;
    
    @Test
    public void datastoresMatch() throws Exception{
        NucleotideSequenceDataStore sut = createSut(SFF_FILE);
        assertEquals(sut.getNumberOfRecords(), dataStore.getNumberOfRecords());
        Iterator<String> ids = sut.idIterator();
        while(ids.hasNext()){
            String id = ids.next();
            assertEquals(sut.get(id),
                    dataStore.get(id).getNucleotideSequence());
        }
    }
    
}
