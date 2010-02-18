/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import org.jcvi.Range;
import org.jcvi.assembly.Placed;

public interface CritiquorAmplicon extends Placed{

    String getId();

    String getRegion();

    Range getRange();

    String getForwardPrimerSequence();

    String getReversePrimerSequence();

}