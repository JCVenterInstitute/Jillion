/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.seq.trace.fastq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.seq.trace.fastq.SolexaUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
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
