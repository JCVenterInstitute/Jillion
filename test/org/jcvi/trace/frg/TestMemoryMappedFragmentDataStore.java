/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.File;

import org.jcvi.util.DefaultMemoryMappedFileRange;

public class TestMemoryMappedFragmentDataStore extends AbstractTestFragmentDataStore{

    @Override
    protected AbstractFragmentDataStore createFragmentDataStore(File file)
            throws Exception {
        return new MemoryMappedFragmentDataStore(file, 
                new DefaultMemoryMappedFileRange(), new DefaultMemoryMappedFileRange(), new Frg2Parser());
    }

}
