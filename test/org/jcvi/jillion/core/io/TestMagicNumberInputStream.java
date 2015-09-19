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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.MagicNumberInputStream;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMagicNumberInputStream {

    byte[] data = "@MAG1234567890".getBytes();
    
    @Test
    public void peak() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        MagicNumberInputStream sut = new MagicNumberInputStream(bin);
        assertEquals("@MAG", new String(sut.peekMagicNumber()));
    }
    @Test
    public void read() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        MagicNumberInputStream sut = new MagicNumberInputStream(bin);
        assertTrue(Arrays.equals(data, IOUtil.toByteArray(sut, data.length)));
    }
    @Test
    public void peakThenRead() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        MagicNumberInputStream sut = new MagicNumberInputStream(bin);
        assertEquals("@MAG", new String(sut.peekMagicNumber()));
        assertTrue(Arrays.equals(data, IOUtil.toByteArray(sut, data.length)));
    }
    
    @Test
    public void readThenPeak() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        MagicNumberInputStream sut = new MagicNumberInputStream(bin);
        assertTrue(Arrays.equals(data, IOUtil.toByteArray(sut, data.length)));
        assertEquals("@MAG", new String(sut.peekMagicNumber()));
    }
    @Test
    public void differentLengthMagicNumber() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        MagicNumberInputStream sut = new MagicNumberInputStream(bin,2);
        assertTrue(Arrays.equals(data, IOUtil.toByteArray(sut, data.length)));
        assertEquals("@M", new String(sut.peekMagicNumber()));
    }
    
    @Test(expected = IOException.class)
    public void notEnoughBytesToFillMagicNumberShouldThrowIOException() throws IOException{
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        new MagicNumberInputStream(bin,data.length+1);
    }
}
