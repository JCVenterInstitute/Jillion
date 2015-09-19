/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.consed.AllCas2ConsedTests;
import org.jcvi.jillion.assembly.clc.cas.transform.AllCasTransformationServiceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestReadFileType.class,
        TestCasUtil.class,
        TestCasGappedReferenceDataStoreBuilderVisitor.class,

        TestGappedReferenceDataStoreBuilderNoReadsMaptoReference.class,
        TestFilteredCasGappedReferenceDataStoreBuilderVisitor.class,
        
        AllCas2ConsedTests.class,
        AllCasTransformationServiceUnitTests.class,
        
        AllCasAlignmentUnitTests.class,
        TestCasFileVisitorAdapter.class,
        TestCasMatchVisitorAdapter.class
    }
    )
public class AllCasUnitTests {

}
