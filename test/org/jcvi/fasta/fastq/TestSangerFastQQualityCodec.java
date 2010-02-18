/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
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
    private GlyphCodec<PhredQuality> qualityCodec= createMock(GlyphCodec.class);
    private SangerFastQQualityCodec sut = new SangerFastQQualityCodec(qualityCodec);
    
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
