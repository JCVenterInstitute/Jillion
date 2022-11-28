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
package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestIOUtil_whenMakingDirectories {

    private File mockFile;
    @Before
    public void setup(){
        mockFile = createMock(File.class);
    }
    @Test
    public void mkdir() throws IOException{
    	
        expect(mockFile.exists()).andReturn(false);
        expect(mockFile.mkdir()).andReturn(true);
        replay(mockFile);
        IOUtil.mkdir(mockFile);
        verify(mockFile);
    }
    @Test
    public void mkdirAlreadyExists() throws IOException{
        expect(mockFile.exists()).andReturn(true);
        replay(mockFile);
        IOUtil.mkdir(mockFile);
        verify(mockFile);
    }
   
   
    @Test(expected = IOException.class)
    public void mkdirFailsShouldThrowIOException() throws IOException{
        expect(mockFile.exists()).andReturn(false);
        expect(mockFile.mkdir()).andReturn(false);
        replay(mockFile);
        IOUtil.mkdir(mockFile);
        verify(mockFile);
    }
   
}
