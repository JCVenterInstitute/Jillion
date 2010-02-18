/*
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Set;

import org.jcvi.sequence.Read;

public interface Clone<T extends Read> {

    String getId();  
    
    Set<T> getReads();
}
