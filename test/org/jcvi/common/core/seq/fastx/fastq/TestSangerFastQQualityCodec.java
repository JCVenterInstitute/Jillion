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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestSangerFastQQualityCodec {

    
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=PhredQuality.MIN_VALUE; i<PhredQuality.MAX_VALUE; i++){
            char encodedQuality =(char)(i+33);
            data.add(new Object[]{PhredQuality.valueOf(i), encodedQuality});
            
        }
        return data;
    }
    
    private PhredQuality quality;
    private char encodedQuality;
    private FastQQualityCodec sut = FastQQualityCodec.SANGER;
    
    public TestSangerFastQQualityCodec(PhredQuality quality,char encodedQuality ){
        this.quality = quality;
        this.encodedQuality = encodedQuality;
    }
    @Test
    public void decode(){
        assertEquals(quality, sut.decode(encodedQuality));
    }
    @Test
    public void encode(){
        assertEquals(encodedQuality, sut.encode(quality));
    }
}
