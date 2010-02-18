/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.List;

public interface AceTagMap {

    List<ConsensusAceTag> getConsensusTags();
    List<ReadAceTag> getReadTags();
    List<WholeAssemblyAceTag> getWholeAssemblyTags();
}
