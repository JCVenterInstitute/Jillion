/*
 * File: AllAlignTests.java
 * 
 * Created: 2009-11-18 15:12:34
 */
package org.jcvi.align;

import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
                  TestSmithWatermanAligner.class
              })
public class AllAlignTests 
{
    public static Test suite() 
    {
        return new junit.framework.JUnit4TestAdapter(AllAlignTests.class);
    }
}
