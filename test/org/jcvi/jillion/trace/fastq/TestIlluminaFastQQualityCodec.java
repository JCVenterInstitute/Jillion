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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestIlluminaFastQQualityCodec {
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=PhredQuality.MIN_VALUE; i<PhredQuality.MAX_VALUE; i++){
            char encodedQuality =(char)(i+64);
            data.add(new Object[]{PhredQuality.valueOf(i), encodedQuality});
            
        }
        return data;
    }
    
    private PhredQuality quality;
    private char encodedQuality;
    private FastqQualityCodec sut = FastqQualityCodec.ILLUMINA;
    
    public TestIlluminaFastQQualityCodec(PhredQuality quality,char encodedQuality ){
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
