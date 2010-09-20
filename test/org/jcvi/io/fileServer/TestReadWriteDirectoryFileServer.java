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
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestReadWriteDirectoryFileServer extends
AbstractTestDirectoryFileServer {
    private static final String TEST_DIR_CREATION_PATH = "Test_ReadWriteRootDir_Creation";
    
    @BeforeClass
    @AfterClass
    public static void cleanTestDir() throws IOException{
        IOUtil.recursiveDelete(new File(TEST_DIR_CREATION_PATH));
    }
    @Override
    protected DirectoryFileServer createFileServer(File file) throws IOException {
        return DirectoryFileServer.createReadWriteDirectoryFileServer(file);
    }
    @Test(expected= NullPointerException.class)
    public void nullPathShouldThrowNullPointerException() throws IOException{
        DirectoryFileServer.createReadWriteDirectoryFileServer((String)null);
    }
    @Test
    public void pathConstructor() throws IOException{
        final File dir = new File(TEST_DIR_CREATION_PATH);
        IOUtil.recursiveDelete(dir);
        DirectoryFileServer.createReadWriteDirectoryFileServer(TEST_DIR_CREATION_PATH);
        assertTrue(dir.exists());
    }
    @Test
    public void createDirectoryIfItDoesntExist() throws IOException{
        final File dir = new File(TEST_DIR_CREATION_PATH);
        IOUtil.recursiveDelete(dir);
        DirectoryFileServer.createReadWriteDirectoryFileServer(dir);
        assertTrue(dir.exists());
    }
    @Test
    public void failingToCreateDirectoryIfItDoesntExistShouldThrowIOException(){
        File mockDir = createMock(File.class);
        expect(mockDir.exists()).andReturn(false);
        expect(mockDir.mkdirs()).andReturn(false);
        replay(mockDir);
        try {
            DirectoryFileServer.createReadWriteDirectoryFileServer(mockDir);
            fail("should throw IOException if dir can not be created");
        } catch (IOException e) {
            assertEquals("could not create rootDir " + mockDir, e.getMessage());
            verify(mockDir);
        }
        
    }
    
    @Test
    public void putFile() throws IOException{
        DirectoryFileServer readOnlyDir = DirectoryFileServer.createReadOnlyDirectoryFileServer(this.PATH_TO_ROOT_DIR);
        File expectedFile =readOnlyDir.getFile("README.txt");
        ReadWriteFileServer sut =DirectoryFileServer.createReadWriteDirectoryFileServer(new File(TEST_DIR_CREATION_PATH));
        sut.putFile("README_copy.txt", expectedFile);
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        IOUtil.writeToOutputStream(new FileInputStream(expectedFile), expected);
        
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        final InputStream fileAsStream = sut.getFileAsStream("README_copy.txt");
        IOUtil.writeToOutputStream(fileAsStream, actual);
        IOUtil.closeAndIgnoreErrors(fileAsStream);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }
    @Test
    public void putStream() throws IOException{
        DirectoryFileServer readOnlyDir = DirectoryFileServer.createReadOnlyDirectoryFileServer(this.PATH_TO_ROOT_DIR);
        File expectedFile =readOnlyDir.getFile("README.txt");
        ReadWriteFileServer sut =DirectoryFileServer.createReadWriteDirectoryFileServer(new File(TEST_DIR_CREATION_PATH));
        sut.putStream("README_copy.txt", readOnlyDir.getFileAsStream("README.txt"));
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        IOUtil.writeToOutputStream(new FileInputStream(expectedFile), expected);
        
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        final InputStream fileAsStream = sut.getFileAsStream("README_copy.txt");
        IOUtil.writeToOutputStream(fileAsStream, actual);
        IOUtil.closeAndIgnoreErrors(fileAsStream);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }
    
    @Test
    public void putThrowsExceptionShouldtossUpExceptionAndCloseStreamsInFinallyBlock() throws IOException{
        IOException expectedException = new IOException("expected");
        ReadWriteFileServer sut =DirectoryFileServer.createReadWriteDirectoryFileServer(new File(TEST_DIR_CREATION_PATH));
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class))).andThrow(expectedException);
        mockInputStream.close();
        replay(mockInputStream);
        try{
            sut.putStream("id", mockInputStream);
            fail("should toss up IOException");
        }catch(IOException e){
            assertEquals(expectedException, e);
        }
        verify(mockInputStream);
    }
}
