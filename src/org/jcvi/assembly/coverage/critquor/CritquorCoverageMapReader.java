/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.io.IOException;
import java.io.InputStream;


public interface CritquorCoverageMapReader {

    CritiquorCovereageMap read(InputStream inputStream) throws IOException;
}
