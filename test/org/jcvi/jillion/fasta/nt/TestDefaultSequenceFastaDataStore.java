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
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.fasta.nt.DefaultNucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;

public class TestDefaultSequenceFastaDataStore extends AbstractTestSequenceFastaDataStore {

    

    @Override
    protected NucleotideFastaDataStore parseFile(File file)
            throws IOException {
        return DefaultNucleotideFastaFileDataStore.create(file);
    }
    
}
