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
 * Created on Dec 19, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.Frame;
import org.jcvi.assembly.annot.Strand;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.DefaultCodingRegion;
import org.jcvi.assembly.annot.ref.DefaultRefGene;
import org.jcvi.assembly.annot.ref.RefGene;
import org.jcvi.assembly.annot.ref.ncbi.FluNcbiRefGeneParser;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestFluNcbiRefGeneParser {

    FluNcbiRefGeneParser sut = new FluNcbiRefGeneParser();
    abstract InputStream getStreamForOneGeneGbf() throws IOException;
    @Test
    public void oneGene() throws IOException{
        List<RefGene> expected = Arrays.<RefGene>asList(
                                    new DefaultRefGene(1,"NA", "gi|58531092|gb|AB166864.1|", Strand.FORWARD,
                                        Range.buildRange(0,1350), 
                                            new DefaultCodingRegion(Range.buildRange(0,1350), 
                                                    CodingRegionState.COMPLETE,
                                                    CodingRegionState.COMPLETE,
                                                    Arrays.<Exon>asList(
                                                            new DefaultExon(Frame.ZERO, 0, 1350)
                                                            )
                                            )
                                        )
                                 );
        assertRefGenesParsedCorrectly(expected, getStreamForOneGeneGbf());
    }
    abstract InputStream getStreamFortwoGenesGbf() throws IOException;
    @Test
    public void twoGenes() throws IOException{
        String referenceName = "gi|58531097|gb|AB166866.1|";
        List<RefGene> expected = Arrays.<RefGene>asList(
                                    new DefaultRefGene(2,"NS2", referenceName, Strand.FORWARD,
                                        Range.buildRange(0,823), 
                                            new DefaultCodingRegion(Range.buildRange(0,823), 
                                                    CodingRegionState.COMPLETE,
                                                    CodingRegionState.COMPLETE,
                                                    Arrays.<Exon>asList(
                                                            new DefaultExon(Frame.ZERO, 0, 30),
                                                            new DefaultExon(Frame.ONE, 487, 823)
                                                            )
                                            )
                                        ),
                                        new DefaultRefGene("NS1", referenceName, Strand.FORWARD,
                                                Range.buildRange(0,678), 
                                                    new DefaultCodingRegion(Range.buildRange(0,678), 
                                                            CodingRegionState.COMPLETE,
                                                            CodingRegionState.COMPLETE,
                                                            Arrays.<Exon>asList(
                                                                    new DefaultExon(Frame.ZERO, 0, 678)
                                                                    )
                                                    )
                                                )
                                        
                                 );
       
        assertRefGenesParsedCorrectly(expected, getStreamFortwoGenesGbf());
    }
    
    private void assertRefGenesParsedCorrectly(List<RefGene> expected,
            final InputStream stream) throws MalformedURLException, IOException {

        try{
            List<RefGene> actual= sut.parse(stream);
            
            assertEquals(expected, actual);
        }
        finally{
            stream.close();
        }
    }
}
