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
 * Created on Apr 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
import org.junit.Test;
/**
 * Tests to see if SCF parser can handle 
 * data that is not A,C,G or T.  SCF spec
 * says that kind  of data is stored in the T channel.
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramWithGaps {
	 private final static ResourceHelper RESOURCES = new ResourceHelper(TestSCFChromatogramWithGaps.class);
		
    private static final String File_path = "files/containsGaps.scf";
    
    @Test
    public void parse() throws ScfDecoderException, IOException{
    	ScfChromatogramBuilder builder = new ScfChromatogramBuilder("id", RESOURCES.getFile(File_path));
    	ScfChromatogram actual = builder.build();
        assertEquals(actual.getNucleotideSequence().toString(), "-----");
        
    }
}
