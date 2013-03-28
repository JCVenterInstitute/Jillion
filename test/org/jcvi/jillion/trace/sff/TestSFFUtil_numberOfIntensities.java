/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSFFUtil_numberOfIntensities {

    
    @Test
    public void noHomoPolymerRunsshouldReturnNumberOfBases(){
        final List<Nucleotide> oneOfEachBasecall = Arrays.asList(Nucleotide.values());
        assertEquals(oneOfEachBasecall.size(),SffUtil.numberOfIntensities(oneOfEachBasecall));
    }
    
    @Test
    public void emptyListShouldReturnZero(){
        assertEquals(0, SffUtil.numberOfIntensities(Collections.<Nucleotide>emptyList()));
    }
    
    @Test
    public void onlyOneHomopolymerRunShouldReturn1(){
        List<Nucleotide> oneHomopolymer = Arrays.asList(Nucleotide.Adenine,Nucleotide.Adenine,Nucleotide.Adenine);
        assertEquals(1,SffUtil.numberOfIntensities(oneHomopolymer));
    }
    @Test
    public void twoHomopolymerRunsShouldReturn2(){
        List<Nucleotide> twoDifferentBases = Arrays.asList(Nucleotide.Adenine,Nucleotide.Adenine,
                Nucleotide.Cytosine);
        assertEquals(2,SffUtil.numberOfIntensities(twoDifferentBases));
    }
    
    @Test
    public void threeRunsShouldReturn3(){
        List<Nucleotide> threeRuns = Arrays.asList(Nucleotide.Adenine,Nucleotide.Adenine,
                Nucleotide.Cytosine,
                Nucleotide.Adenine,Nucleotide.Adenine);
        assertEquals(3,SffUtil.numberOfIntensities(threeRuns));
    }
}
