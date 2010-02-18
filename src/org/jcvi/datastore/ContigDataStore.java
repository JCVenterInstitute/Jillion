/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;

public interface ContigDataStore<PR extends PlacedRead,C extends Contig<PR>> extends DataStore<C> {
    
}
