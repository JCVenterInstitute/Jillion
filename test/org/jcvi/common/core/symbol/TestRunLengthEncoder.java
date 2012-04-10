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
 * Created on FeT 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.jcvi.common.core.symbol.RunLength;
import org.jcvi.common.core.symbol.RunLengthEncoder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncoder {

   String BasesAsString = "AAAAAAAAAAAATAAAAAAAAAAAATTTAAAAAAAAAAAAAAAAAAAAAAAATAAAAAAAAAAAAAA";
    List<Nucleotide> list = Nucleotides.parse(BasesAsString);
    List<RunLength<Nucleotide>> expectedEncoding = Arrays.asList(
            new RunLength<Nucleotide>(Nucleotide.Adenine,12),
            new RunLength<Nucleotide>(Nucleotide.Thymine,1),
            new RunLength<Nucleotide>(Nucleotide.Adenine,12),
            new RunLength<Nucleotide>(Nucleotide.Thymine,3),
            new RunLength<Nucleotide>(Nucleotide.Adenine,24),
            new RunLength<Nucleotide>(Nucleotide.Thymine,1),
            new RunLength<Nucleotide>(Nucleotide.Adenine,14)
    );
    @Test
    public void encode(){        
        List<RunLength<Nucleotide>> actual =RunLengthEncoder.encode(list);
        assertEquals(expectedEncoding, actual);
    }
    
    @Test
    public void decode(){
        List<Nucleotide> actual = RunLengthEncoder.decode(expectedEncoding);
        assertEquals(list, actual);
    }
    
    @Test
    public void enocdeEmptyList(){
        assertEquals(new ArrayList<RunLength<Nucleotide>>(), 
                RunLengthEncoder.encode(Collections.<Nucleotide>emptyList()));
    }
}
