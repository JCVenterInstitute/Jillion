/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Set;

public interface ConsensusAceTag extends PlacedAceTag {

    Set<String> getComments();
    String getData();
}
