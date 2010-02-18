/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileNotFoundException;

public class TestDefaultContigFileDataStore extends AbstractTestContigFileDataStore{
   
    @Override
    protected DefaultContigFileDataStore buildContigFileDataStore(
            File file) throws FileNotFoundException {
        return new DefaultContigFileDataStore(file);
    }
}
