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

package org.jcvi.jillion.trace.sanger.chromat;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.sanger.chromat.abi.AbiChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.abi.AbiChromatogramBuilder;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;

public class TestChromatogramFactory {

	private static final String ZTR_FILE = "ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "scf/files/GBKAK82TF.scf";
    private static final String AB1_FILE = "abi/files/SDBHD01T00PB1A1672F.ab1";
  
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestChromatogramFactory.class);
    
    @Test
    public void parseZTR() throws TraceDecoderException, IOException{    	
        File ztrFile = RESOURCES.getFile(ZTR_FILE);
        ZtrChromatogram expected = new ZtrChromatogramBuilder(ztrFile.getName(), ztrFile).build();
        ZtrChromatogram actual = (ZtrChromatogram) ChromatogramFactory.create(ztrFile);       
        assertEquals(expected, actual);
    }
    @Test
    public void parseSCF3() throws TraceDecoderException, IOException{    	
        File scfFile = RESOURCES.getFile(SCF3_FILE);
        ScfChromatogram expected = new ScfChromatogramBuilder(scfFile.getName(), scfFile)
									.build();
        ScfChromatogram actual = (ScfChromatogram) ChromatogramFactory.create(scfFile);
        assertEquals(expected, actual);
    }
    @Test
    public void parseAB1() throws TraceDecoderException, IOException{    	
        File ab1File = RESOURCES.getFile(AB1_FILE);
        AbiChromatogram expected = new AbiChromatogramBuilder(ab1File.getName(),ab1File)
        							.build();
        AbiChromatogram actual = (AbiChromatogram) ChromatogramFactory.create(ab1File);        
        assertEquals(expected, actual);
    }
}
