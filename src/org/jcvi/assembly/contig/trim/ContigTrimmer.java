/*
 * Created on Oct 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import java.util.List;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.datastore.DataStoreException;

public interface ContigTrimmer<R extends PlacedRead> {

    List<TrimmedPlacedRead<R>> trim(ContigCheckerStruct<R> struct) throws DataStoreException;
}
