/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.position;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
     TestBytePositionStrategy.class,
     TestShortPositionStrategy.class,
     TestPositionStrategyFactory.class
    }
    )
public class AllPositionStrategyUnitTests {

}
