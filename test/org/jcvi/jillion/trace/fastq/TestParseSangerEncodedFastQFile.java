/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestParseSangerEncodedFastQFile {

    static final FastqQualityCodec QUALITY_CODEC = FastqQualityCodec.SANGER;
    String file = "files/sanger.fastq";
    ResourceHelper resources = new ResourceHelper(
            TestDefaultFastQFileDataStore.class);
    FastqDataStore sut;
    @Before
    public void setup() throws IOException{
    	FastqParser parser = new FastqFileParserBuilder(resources.getFile(file))
									.hasComments(true)
									.build();


    	sut= DefaultFastqFileDataStore.create(parser,QUALITY_CODEC,DataStoreFilters.alwaysAccept(), null);
        
    }
    
    @Test
    public void qualityValueStartsWithAmpersand() throws DataStoreException{
        FastqRecord actual = sut.get("SOLEXA1_0007:1:13:1658:1080#GGCTAC/2");
        assertEquals("CGTAGTACGATATACGCGCGTGTGTACTGCTACGTCTCACTTCTTTTTCCCCACGGGATGTTATTTCCCTTTTAAGCTTCCTGTACAGTTTTGCCGGGCT",
                actual.getNucleotideSequence().toString());
        assertEquals(QUALITY_CODEC.decode("@;7C9;A)565A;4..9;2;45,?@###########################################################################"),
                actual.getQualitySequence());
    }
    @Test
    public void normalRecord() throws DataStoreException{
        FastqRecord actual = sut.get("SOLEXA1_0007:2:13:163:254#GATCAG/2");
        assertEquals("CGTAGTACGATATACGCGCGTGTACTGCTACGTCTCACTTTCGCAAGATTGCTCAGCTCATTGATGCTCAATGCTGGGCCATATCTCTTTTCTTTTTTTC",
                actual.getNucleotideSequence().toString());
        assertEquals(QUALITY_CODEC.decode("HHHHGHHEHHHHHE=HAHCEGEGHAG>CHH>EG5@>5*ECE+>AEEECGG72B&A*)569B+03B72>5.A>+*A>E+7A@G<CAD?@############"),
                actual.getQualitySequence());
    }
}
