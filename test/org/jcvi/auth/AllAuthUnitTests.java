/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDefaultJCVIAuthorizer.class,
        TestDefaultTigrAuthorizer.class,
        TestDefaultTigrAuthorizerBuilder.class,
        TestBasicEncodedJCVIAuthorizer.class
    })
public class AllAuthUnitTests {

}
