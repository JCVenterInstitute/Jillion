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

package org.jcvi.common.core.io;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.io.IOUtil;
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
    @Test
    public void mkdirs() throws IOException{
        expect(mockFile.exists()).andReturn(false);
        expect(mockFile.mkdirs()).andReturn(true);
        replay(mockFile);
        IOUtil.mkdirs(mockFile);
        verify(mockFile);
    }
    @Test
    public void mkdirsAlreadyExists() throws IOException{
        expect(mockFile.exists()).andReturn(true);
        replay(mockFile);
        IOUtil.mkdirs(mockFile);
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
    @Test(expected = IOException.class)
    public void mkdirsFailsShouldThrowIOException() throws IOException{
        expect(mockFile.exists()).andReturn(false);
        expect(mockFile.mkdirs()).andReturn(false);
        replay(mockFile);
        IOUtil.mkdirs(mockFile);
        verify(mockFile);
    }
}
