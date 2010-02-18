/*
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.File;
import java.io.IOException;


public interface FileSangerTrace extends SangerTrace{

    File getFile() throws IOException;
}
