package org.jcvi.jillion.assembly.consed.nav;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
		{
	        
	        TestConsensusNavigationElement.class,
	        TestConsensusNavigationElementFactoryMethod.class,
	        TestReadNavigationElement.class,
	       
	        TestConsedNavigationWriter.class,
	        TestConsedNavigationParser.class
		}
		)
public class AllConsedNavigationUnitTests {

}
