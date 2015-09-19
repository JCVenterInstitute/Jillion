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
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestChunk.class,
     TestClipChunk.class,
     TestBASEChunk.class,
     TestBPOSChunk.class,
     TestSMP4Chunk.class,
     TestCNF4Chunk.class,
     TestTEXTChunk.class,
     TestChunkFactory.class
    }
    )
public class AllChunkUnitTests {

}
