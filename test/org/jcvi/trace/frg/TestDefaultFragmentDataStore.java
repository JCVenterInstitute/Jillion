/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.File;

public class TestDefaultFragmentDataStore extends AbstractTestFragmentDataStore{

    @Override
    protected AbstractFragmentDataStore createFragmentDataStore(File file)
            throws Exception {
        return new DefaultFragmentDataStore();
    }

}
