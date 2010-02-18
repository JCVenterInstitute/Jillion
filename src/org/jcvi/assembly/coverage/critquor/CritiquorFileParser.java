/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.io.File;
import java.io.IOException;

public interface CritiquorFileParser {

    void parse(File critiquorFile, CritiquorFileVisitor visitor) throws IOException;
}
