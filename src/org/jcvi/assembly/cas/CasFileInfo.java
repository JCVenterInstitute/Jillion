/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.math.BigInteger;
import java.util.List;

public interface CasFileInfo {

    long getNumberOfSequences();
    
    BigInteger getNumberOfResidues();
    
    List<String> getFileNames();
}
