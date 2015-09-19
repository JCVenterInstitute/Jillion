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
package org.jcvi.jillion.trace.chromat;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogram;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;

public class TestChromatogramFactory {

	private static final String ZTR_FILE = "ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "scf/files/GBKAK82TF.scf";
    private static final String AB1_FILE = "abi/files/SDBHD01T00PB1A1672F.ab1";
  
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestChromatogramFactory.class);
    
    @Test
    public void parseZtrGivenName() throws IOException{    	
        File ztrFile = RESOURCES.getFile(ZTR_FILE);
        ZtrChromatogram expected = new ZtrChromatogramBuilder(ztrFile.getName(), ztrFile).build();
        ZtrChromatogram actual = (ZtrChromatogram) ChromatogramFactory.create(ztrFile.getName(), ztrFile);       
        assertEquals(expected, actual);
    }
    @Test
    public void parseZtrExtractNameFromFileName() throws IOException{    	
        File ztrFile = RESOURCES.getFile(ZTR_FILE);
        String expectedId = extractNameFromFile(ztrFile);
        ZtrChromatogram expected = new ZtrChromatogramBuilder(expectedId, ztrFile).build();
        ZtrChromatogram actual = (ZtrChromatogram) ChromatogramFactory.create(ztrFile);       
        assertEquals(expected, actual);
    }
    @Test
    public void parseScfGivenName() throws IOException{    	
        File scfFile = RESOURCES.getFile(SCF3_FILE);
        ScfChromatogram expected = new ScfChromatogramBuilder(scfFile.getName(), scfFile)
									.build();
        ScfChromatogram actual = (ScfChromatogram) ChromatogramFactory.create(scfFile.getName(),scfFile);
        assertEquals(expected, actual);
    }
    @Test
    public void parseScfExtractNameFromFileName() throws IOException{    	
        File scfFile = RESOURCES.getFile(SCF3_FILE);
        String expectedId = extractNameFromFile(scfFile);
        ScfChromatogram expected = new ScfChromatogramBuilder(expectedId, scfFile)
									.build();
        ScfChromatogram actual = (ScfChromatogram) ChromatogramFactory.create(scfFile);
        assertEquals(expected, actual);
    }
    @Test
    public void parseAb1GivenName() throws IOException{    	
        File ab1File = RESOURCES.getFile(AB1_FILE);
        AbiChromatogram expected = new AbiChromatogramBuilder(ab1File.getName(),ab1File)
        							.build();
        AbiChromatogram actual = (AbiChromatogram) ChromatogramFactory.create(ab1File.getName(),ab1File);        
        assertEquals(expected, actual);
        
    }
    
    @Test
    public void parseAb1ExtractNameFromFileName() throws IOException{    	
        File ab1File = RESOURCES.getFile(AB1_FILE);
        String expectedId = extractNameFromFile(ab1File);
        AbiChromatogram expected = new AbiChromatogramBuilder(expectedId,ab1File)
        							.build();
        AbiChromatogram actual = (AbiChromatogram) ChromatogramFactory.create(ab1File);        
        assertEquals(expected, actual);
        
    }
	public String extractNameFromFile(File file) {
		String expectedId = FileUtil.getBaseName(file);
		return expectedId;
	}
    
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE() throws IOException{
    	ChromatogramFactory.create(null);
    }
}
