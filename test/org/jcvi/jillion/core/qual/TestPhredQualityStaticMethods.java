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
package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestPhredQualityStaticMethods {

    @Test(expected = IllegalArgumentException.class)
    public void zeroErrorProbabilityShouldThrowIllegalArgumentException(){
        PhredQuality.withErrorProbability(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void negativeErrorProbabilityShouldThrowIllegalArgumentException(){
        PhredQuality.withErrorProbability(-.3);
    }
    @Test(expected = IllegalArgumentException.class)
    public void ErrorProbabilityOf1ShouldThrowIllegalArgumentException(){
        PhredQuality.withErrorProbability(1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void ErrorProbabilityOfGreaterThan1ShouldThrowIllegalArgumentException(){
        PhredQuality.withErrorProbability(2);
    }
}
