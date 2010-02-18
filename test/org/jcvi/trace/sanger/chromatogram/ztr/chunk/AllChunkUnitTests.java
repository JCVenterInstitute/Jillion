/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

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
