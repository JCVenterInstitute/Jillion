/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.IOException;

public interface SffDataStoreFactory {

    SffDataStore createDataStoreFor(File sffFile) throws IOException;
}
