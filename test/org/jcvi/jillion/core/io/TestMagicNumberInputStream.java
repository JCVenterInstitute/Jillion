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
