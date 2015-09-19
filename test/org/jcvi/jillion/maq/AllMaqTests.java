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
package org.jcvi.jillion.maq;

import org.jcvi.jillion.maq.bfa.TestBfaDataStores;
import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileParser;
import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileWriter;
import org.jcvi.jillion.maq.bfq.TestBinaryFastqDataStore;
import org.jcvi.jillion.maq.bfq.TestBinaryFastqFileWriter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestBinaryFastqDataStore.class,
    	
    	TestBinaryFastqFileWriter.class,
    	
    	TestBinaryFastaFileParser.class,
    	TestBinaryFastaFileWriter.class,
    	TestBfaDataStores.class
    }
    )
public class AllMaqTests {

}
