/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
public class TestIOUtil_deleteDir {

    File rootDir;
    @Test(expected = NullPointerException.class)
    public void nullDirThrowsNullPointerException()throws IOException{
        IOUtil.recursiveDelete(null);
    }
    private File createMockFileThatIsNeverAccessed(){
        File singleFile= createMock(File.class);       
        return singleFile;
    }
    
    /*
     *   private static FileSystemProvider provider(Path path) {
        return path.getFileSystem().provider();
    }
     */
    private void createDeleteSuccessfulPathMocks(File file) throws IOException{
    	  Path mockPath = createMock(Path.class);
          expect(file.toPath()).andReturn(mockPath);
          
          FileSystem fs = createMock(FileSystem.class);
          
          FileSystemProvider fsp = createMock(FileSystemProvider.class);
          
         expect(mockPath.getFileSystem()).andReturn(fs);
         
         expect(fs.provider()).andReturn(fsp);
         
         expect(fsp.deleteIfExists(mockPath)).andReturn(true);
         
         replay(mockPath, fs, fsp);
    }
    
    private void createUnsuccessfulDeletePath(File file, IOException expectedException) throws IOException{
    	Path mockPath = createMock(Path.class);
        expect(file.toPath()).andReturn(mockPath);
        
        FileSystem fs = createMock(FileSystem.class);
        
        FileSystemProvider fsp = createMock(FileSystemProvider.class);
        
       expect(mockPath.getFileSystem()).andReturn(fs);
       
       expect(fs.provider()).andReturn(fsp);
       
       expect(fsp.deleteIfExists(mockPath)).andThrow(expectedException);
       
       replay(mockPath, fs, fsp);
    }
    
    private File createMockFile() throws IOException{
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andStubReturn(true);
        expect(singleFile.isDirectory()).andReturn(false);
        
        createDeleteSuccessfulPathMocks(singleFile);
        return singleFile;
    }
    private File createMockDir(File...subFiles) throws IOException{
        File dir=createMockDirThatIsNotDeleted(subFiles);
        
        createDeleteSuccessfulPathMocks(dir);
        return dir;
    }
    private File createMockDirThatIsNotDeleted(File...subFiles){
        File dir= createMock(File.class);
        expect(dir.exists()).andStubReturn(true);
        expect(dir.isDirectory()).andReturn(true);
        expect(dir.listFiles()).andReturn(subFiles);
        return dir;
    }
    
    private File createMockFileThatCantBeDeleted(IOException expectedException) throws IOException{
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andStubReturn(true);
        expect(singleFile.isDirectory()).andReturn(false);
      //  expect(singleFile.delete()).andReturn(false);
        createUnsuccessfulDeletePath(singleFile, expectedException);
        return singleFile;
    }
    private File createNonExistentMockFile(){
        File singleFile= createMock(File.class);
        expect(singleFile.exists()).andStubReturn(false);
        return singleFile;
    }
    @Test
    public void singleFileGetsDeleted() throws IOException{
        File singleFile = createMockFile();
        replay(singleFile);
        IOUtil.recursiveDelete(singleFile);
        verify(singleFile);
    }
    @Test
    public void fileThatDoesNotExistShouldDoNothing() throws IOException{
        File singleFile = createNonExistentMockFile();
        replay(singleFile);
        IOUtil.recursiveDelete(singleFile);
        verify(singleFile);
    }
    
    @Test
    public void errorOnDeleteShouldThrowIOException() throws IOException{
    	
    	IOException expected = new IOException("expected");
    	
        File singleFile = createMockFileThatCantBeDeleted(expected);
        replay(singleFile);
        try {
            IOUtil.recursiveDelete(singleFile);
            fail("if can't be deleted should throw IOException");
        } catch (IOException e) {
           assertEquals(e, expected);
        }
        verify(singleFile);
    }
    public static <T> void replayAll(T[] mocks){
        for(T mock : mocks){
            replay(mock);
        }
    }
    public static <T> void verifyAll(T[] mocks){
        for(T mock : mocks){
            verify(mock);
        }
    }
    @Test
    public void deleteDir() throws IOException{
        File[] subFiles = new File[]{
                createMockFile(),createMockFile(),createMockFile()};
        File dir = createMockDir(subFiles);
        replay(dir);
        replayAll(subFiles);
        IOUtil.recursiveDelete(dir);
        verify(dir);
        verifyAll(subFiles);
    }
    @Test
    public void deleteChildren() throws IOException{
        File[] subFiles = new File[]{
                createMockFile(),createMockFile(),createMockFile()};
        File dir = createMockDirThatIsNotDeleted(subFiles);
        replay(dir);
        replayAll(subFiles);
        IOUtil.deleteChildren(dir);
        verify(dir);
        verifyAll(subFiles);
    }

    @Test
    public void nestedDirs() throws IOException{
        File[] subSubFiles = new File[]{
                createMockFile(),createMockFile(),createMockFile()};
        File subDir = createMockDir(subSubFiles);
        File[] subFiles = new File[]{
                createMockFile(),createMockFile()};
        List<File> fileList = new ArrayList<File>();
        fileList.add(subDir);
        for(File subFile : subFiles){
            fileList.add(subFile);
        }
        final File[] subFilesAndSubDirs = fileList.toArray(new File[]{});
        File dir = createMockDir(subFilesAndSubDirs);
        replay(dir,subDir);
        replayAll(subFiles);
        replayAll(subSubFiles);
        IOUtil.recursiveDelete(dir);
        verify(dir,subDir);
        verifyAll(subFiles);
        verifyAll(subSubFiles);
    }
    
    @Test
    public void nestedDeleteFailsShouldThrowIOExceptionAndStopDeleting() throws IOException{
    	IOException expected = new IOException("expected");
    	
        File[] subSubFiles = new File[]{
                createMockFile(),
                createMockFileThatCantBeDeleted(expected),
                createMockFileThatIsNeverAccessed()};
        File subDir = createMockDirThatIsNotDeleted(subSubFiles);
        File[] subFiles = new File[]{
                createMockFileThatIsNeverAccessed(),createMockFileThatIsNeverAccessed()};
        List<File> fileList = new ArrayList<File>();
        fileList.add(subDir);
        for(File subFile : subFiles){
            fileList.add(subFile);
        }
        final File[] subFilesAndSubDirs = fileList.toArray(new File[]{});
        File dir = createMockDirThatIsNotDeleted(subFilesAndSubDirs);
        replay(dir,subDir);
        replayAll(subFiles);
        replayAll(subSubFiles);
        try {
            IOUtil.recursiveDelete(dir);
            fail("failure to delete file should throw IOException");
        } catch (IOException e) {
        	assertEquals(e, expected);
            verify(dir,subDir);
            verifyAll(subFiles);
            verifyAll(subSubFiles);
        }
        
    }
    
}


