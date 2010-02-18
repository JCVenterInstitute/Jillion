/*
 * Created on Feb 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.Closeable;

import org.jcvi.assembly.Placed;

public interface MultipleCoverageWriter<T extends Placed> extends Closeable {

    void add(String id,CoverageMap<CoverageRegion<T>> coverageMap);
}
