/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.assembly.Placed;

public interface CoverageWriter<T extends Placed> extends Closeable{

    void write(CoverageMap<CoverageRegion<T>> write) throws IOException;
    
    
}
