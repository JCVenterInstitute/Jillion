/*
 * Created on Jun 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
        TestConicConsensusCaller.class,
        TestBasicChurchillWatermanConsensusCaller.class,
        TestAnnotationConsensusCaller.class,
        TestNoAmbiguityConsensusCaller.class
    }
    )
public class AllConsensusUnitTests {

}
