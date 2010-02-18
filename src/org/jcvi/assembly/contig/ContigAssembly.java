/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.Assembly;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.datastore.DataStore;

public interface ContigAssembly extends Assembly<Contig<PlacedRead>, DataStore<Contig<PlacedRead>>>{

}
