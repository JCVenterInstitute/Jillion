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
package org.jcvi.jillion.trace.fastq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.trace.fastq.SolexaUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestSolexaUtil {

    private final PhredQuality phredQuality;
    private final int solexaQuality;
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=-5; i<62; i++){
            data.add(new Object[]{i, SolexaUtil.convertSolexaQualityToPhredQuality(i)});
        }
        return data;
    }
    /**
     * @param phredQuality
     * @param solexaQuality
     */
    public TestSolexaUtil(int solexaQuality,PhredQuality phredQuality) {
        this.phredQuality = phredQuality;
        this.solexaQuality = solexaQuality;
    }
    
    @Test
    public void convertSolexaToPhredquality(){
        int expected = solexaQuality;
        if(expected == -3){
            expected = -2;
        }else if(expected == -1){
            expected = 0;
        }else if(expected == 1){
            expected = 2;
        }
        else if(expected == 4){
            expected = 3;
        }
        else if(expected == 9){
            expected = 10;
        }
        assertEquals(expected, SolexaUtil.convertPhredQualityToSolexaQuality(phredQuality));
    }
}
