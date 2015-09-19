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
package org.jcvi.jillion.fasta.qual;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;

public class TestDefaultQualityFastaDataStoreAsStream extends AbstractTestQualityFastaDataStore{
    
    @Override
    protected QualityFastaDataStore createDataStore(File file) throws IOException{
        InputStream in = null;
        try{
        	in = new BufferedInputStream(new FileInputStream(file));
        	return DefaultQualityFastaFileDataStore.create(file);
        }finally{
        	IOUtil.closeAndIgnoreErrors(in);
        }
    	
    }

}
