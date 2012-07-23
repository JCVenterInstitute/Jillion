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
 * Created on Jul 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.fileServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.FileIterator;
/**
 * {@code DirectoryFileServer} is a {@link FileServer}
 * to work off of a directory on the file system.
 * @author dkatzel
 *
 *
 */
public abstract class DirectoryFileServer extends AbstractFileServer implements Iterable<File>  {
    /**
     * Default file name given
     * to a temp file.
     */
    public static final String DEFAULT_TEMP_FILE_NAME = "temp";
    
    /**
     * This is the root of our File Server.
     */
    private final File rootDir;
    /**
     * Factory method to create a Directory File Server.
     * @param rootDir the root directory of this file server (can not be null). 
     * If the rootDir does not exist, it will be created.
     * @return an DirectoryFileServer implementation that is writable.
     * @throws IOException if rootDir does not exist AND can not be created.
     * @throws NullPointerException if rootDir is null.
     */
    public static ReadWriteDirectoryFileServer createReadWriteDirectoryFileServer(File rootDir) throws IOException{       
        return new ReadWriteDirectoryFileServer(rootDir);
    }
    /**
     * Factory method to create a Directory File Server.
     * @param rootDirPath the root directory of this file server (can not be null). 
     * If the rootDirPath does not exist, it will be created.
     * @return an DirectoryFileServer implementation that is writable.
     * @throws IOException if rootDirPath does not exist AND can not be created.
     * @throws NullPointerException if rootDirPath is null.
     */
    public static ReadWriteDirectoryFileServer createReadWriteDirectoryFileServer(String rootDirPath) throws IOException{       
        return new ReadWriteDirectoryFileServer(createFileFor(rootDirPath));
    }
    /**
     * Factory method to create a Directory File Server.
     * @param rootDir the root directory of this file server (can not be null). 
     * If the rootDir does not exist 
     * @return an DirectoryFileServer implementation that is read-only.
     * @throws IOException if rootDir does not exist.
     * @throws NullPointerException if rootDir is null.
     */
    public static ReadOnlyDirectoryFileServer createReadOnlyDirectoryFileServer(File rootDir) throws IOException{       
        return new ReadOnlyDirectoryFileServer(rootDir);
    }
    /**
     * Factory method to create a Directory File Server.
     * @param rootDirPath the root directory of this file server (can not be null). 
     * @return an DirectoryFileServer implementation that is read-only.
     * @throws IOException if rootDir does not exist.
     * @throws NullPointerException if rootDirPath is null.
     */
    public static ReadOnlyDirectoryFileServer createReadOnlyDirectoryFileServer(String rootDirPath) throws IOException{       
        File rootDir = createFileFor(rootDirPath);
        return createReadOnlyDirectoryFileServer(rootDir);
    }
    /**
     * Convenience method for creating a {@link TemporaryDirectoryFileServer}
     * with the given prefix and and a suffix of ".tmp"  under
     * the given parentDirectory. This method is the same as calling 
     * <code> {@link #createTemporaryDirectoryFileServer(File, String, String)
     * createTemporaryDirectoryFileServer(prefix, null, parentDir)}</code>
     * @param prefix argument must be at least three characters long. 
     * It is recommended that the prefix be a short, meaningful.
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @throws IOException if the temp directory can not be created.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer(File parentDir, String prefix) throws IOException{
        return createTemporaryDirectoryFileServer(parentDir, prefix, null);
    }
    /**
     * Create a new unique Temporary Directory named
     * using the given, prefix, suffix  and located under the 
     * given parentDir using 
     * <code> {@link File#createTempFile(String, String, File)
     * File.createTempFile(prefix, suffix,parentDir)}</code>.
     * This temp directory will be automatically
     * deleted when the JVM exits normally.
     * @param parentDir parent directory which this temp directory
     * will be created. if this parameter is {@code null} then 
     * the system-dependent default temporary-file directory will be used. 
     * @param prefix argument must be at least three characters long. 
     * It is recommended that the prefix be a short, meaningful.
     * @param suffix the suffix argument may be null, 
     * in which case the suffix ".tmp" will be used.
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @see File#createTempFile(String, String, File)
     * @throws IOException if the temp directory can not be created.
     * @throws NullPointerException if prefix is null.
     * @throws IllegalArgumentException if prefix is < 3 characters long.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer(File parentDir, String prefix,String suffix) throws IOException{
        //make a temp file in the given parentDir
        File tempFile = File.createTempFile(prefix, suffix,parentDir);
        //now that we have a new empty file
        //we need to delete it and then create it again, but this
        //time as a directory
        if(!tempFile.delete() || !tempFile.mkdir()){
            throw new IOException("Could not create temp directory: " + tempFile.getAbsolutePath());
        }
        return new TemporaryDirectoryFileServer(tempFile);
    }
    /**
     * Convenience method, for creating
     * a {@link TemporaryDirectoryFileServer} with the
     * default temp file name defined by {@value #DEFAULT_TEMP_FILE_NAME}
     * under the specified parent directory. This is the 
     * same as calling 
     * <code> {@link #createTemporaryDirectoryFileServer(File, String) 
     * createTemporaryDirectoryFileServer(DEFAULT_TEMP_FILE_NAME, parentDir)}
     * </code>
     * @param parentDir parent directory which this temp directory
     * will be created. if this parameter is {@code null} then 
     * the system-dependent default temporary-file directory will be used. 
     * 
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @throws IOException if the temp directory can not be created.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer(File parentDir) throws IOException{
        return createTemporaryDirectoryFileServer(parentDir, DEFAULT_TEMP_FILE_NAME);
    }
    /**
     * Convenience method for creating a {@link TemporaryDirectoryFileServer}
     * with the given prefix and suffix in the system-dependent 
     * default temporary-file directory. This method is the same as calling 
     * <code> {@link #createTemporaryDirectoryFileServer(File, String, String)
     * createTemporaryDirectoryFileServer(prefix, suffix, null)}</code>
     * @param prefix argument must be at least three characters long. 
     * It is recommended that the prefix be a short, meaningful.
     * @param suffix the suffix argument may be null, 
     * in which case the suffix ".tmp" will be used.
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @throws IOException if the temp directory can not be created.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer(String prefix, String suffix) throws IOException{
        return createTemporaryDirectoryFileServer(null, prefix, suffix);
    }
    /**
     * Convenience method for creating a {@link TemporaryDirectoryFileServer}
     * with the given prefix and a suffix of ".tmp" in the system-dependent 
     * default temporary-file directory. This method is the same as calling 
     * <code> {@link #createTemporaryDirectoryFileServer(File, String)
     * createTemporaryDirectoryFileServer(prefix, (File)null)}</code>
     * @param prefix argument must be at least three characters long. 
     * It is recommended that the prefix be a short, meaningful.
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @throws IOException if the temp directory can not be created.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer(String prefix) throws IOException{
        return createTemporaryDirectoryFileServer((File)null, prefix);
    }
    /**
     * Convenience method, for creating
     * a {@link TemporaryDirectoryFileServer} with the
     * default temp file name defined by {@value #DEFAULT_TEMP_FILE_NAME}
     * in the system-dependent 
     * default temporary-file directory.  This is the 
     * same as calling 
     * <code> {@link #createTemporaryDirectoryFileServer(File) 
     * createTemporaryDirectoryFileServer((File)null)}
     * </code>
     * 
     * @return a new {@link TemporaryDirectoryFileServer}.
     * @throws IOException if the temp directory can not be created.
     */
    public static TemporaryDirectoryFileServer createTemporaryDirectoryFileServer() throws IOException{
        return createTemporaryDirectoryFileServer((File)null);
    }
    
    private static File createFileFor(String rootDirPath) {
        if(rootDirPath ==null){
            throw new NullPointerException("rootDirPath can not be null");
        }
        File rootDir = new File(rootDirPath);
        return rootDir;
    }
    
    
    
    protected DirectoryFileServer(File rootDir){
        if(rootDir == null){
            throw new NullPointerException("rootDir can not be null");
        }        
        this.rootDir = rootDir;
    }
    
    /**
     * Get the root of our File Server.
     * @return
     */
    public File getRootDir() {
        return rootDir;
    }
    
    @Override
    public Iterator<File> iterator() {
        return FileIterator.createDepthFirstFileIteratorBuilder(rootDir)
            .includeDirectories(true)
            .build();
    }
    @Override
    public synchronized File getFile(String fileId) throws IOException {
        verifyNotClosed();
        File f = getFileFor(fileId);
        if(!f.exists()){
            throw new IOException("file "+ fileId + " does not exist");
        }
        return f;
    }
    
    
    @Override
    public boolean supportsGettingFileObjects() {
        return true;
    }
    protected String convertToAbsolutePath(String fileId) {
        verifyNotClosed();
        return rootDir.getAbsolutePath() + File.separator + fileId;
    }

    @Override
    public boolean contains(String fileId) throws IOException {
        File f = getFileFor(fileId);
        return f.exists();
    }
    private File getFileFor(String fileId) {
        return new File(convertToAbsolutePath(fileId));
    }
    @Override
    public synchronized InputStream getFileAsStream(String fileId) throws IOException {
        verifyNotClosed();
        return new FileInputStream(convertToAbsolutePath(fileId));
    }

    /**
     * {@code ReadWriteDirectoryFileServer} is the implementation
     * of {@link DirectoryFileServer} that allows put operations.
     * @author dkatzel
     *
     */
    public static class ReadWriteDirectoryFileServer extends DirectoryFileServer implements ReadWriteFileServer{
    
        private ReadWriteDirectoryFileServer(File rootDir) throws IOException {
            super(rootDir);
            if(!rootDir.exists() && !rootDir.mkdirs()){
                throw new IOException("could not create rootDir " + rootDir);
            }
        }
    
        @Override
        public final synchronized void putFile(String fileId, File fileToPut) throws IOException {
            InputStream in = new FileInputStream(fileToPut);
            putStream(fileId, in);
        }
    
        @Override
        public final synchronized void putStream(String fileId, InputStream inputStream)
                throws IOException {
            FileOutputStream out=null;
            try{
                verifyNotClosed();
                
                File newFile = createNewFile(fileId);
                out = new FileOutputStream(newFile);
                IOUtil.copy(inputStream, out);
            }
            finally{
                IOUtil.closeAndIgnoreErrors(out);
                IOUtil.closeAndIgnoreErrors(inputStream);
            }
    
        }
        /**
         * Creates a new File for the given filepath.
         * @param filePath the path of the file to create.
         * @return a new File.
         */
        @Override
        public File createNewFile(String filePath) throws IOException{
            String absoluteFilePath = convertToAbsolutePath(filePath);
            File newFile= new File(absoluteFilePath);
            if(newFile.exists()){
                try{
                    IOUtil.recursiveDelete(newFile);
                }catch(IOException e){
                    throw new IOException("could not delete pre-existing file "+ filePath, e);
                }
            }
            return newFile;
        }

        @Override
        public final File createNewDir(String dirPath) throws IOException {
            File dir =createNewFile(dirPath);
            if(!dir.mkdirs()){
                throw new IOException("could not create dir "+ dirPath);
            }
            return dir;
        }
        
        @Override
        public final File createNewDirIfNeeded(String dirPath) throws IOException {
            if(!contains(dirPath)){
                return createNewDir(dirPath);
            }
            return getFile(dirPath);
        }

        @Override
        public final void createNewSymLink(String pathtoFileToLink,
                String symbolicPath) throws IOException {
            if(contains(symbolicPath)){
               throw new IOException("file already exists");
            }
            //TODO use NIO 2 to do this when Java 7 comes out
            Process process =new ProcessBuilder("/bin/ln","-s",pathtoFileToLink, symbolicPath)
                                        .directory(getRootDir())
                                        .start();
            try {
                int exitCode =process.waitFor();
                if(exitCode !=0){
                    throw new IOException("error creating symlink: exit code: "+exitCode);
                }
            } catch (InterruptedException e) {
            	//re-interrupt current thread
                Thread.currentThread().interrupt();
            }                       
        }

        @Override
        public final boolean supportsSymlinks() {
            return true;
        }
    }
    
    /**
     * {@code TemporaryDirectoryFileServer} is a {@link ReadWriteDirectoryFileServer}
     * that will get deleted when the JVM exits normally.
     * @author dkatzel
     *
     *
     */
    public static final class TemporaryDirectoryFileServer extends ReadWriteDirectoryFileServer{

        private TemporaryDirectoryFileServer(File rootDir) throws IOException {
            super(rootDir);
            rootDir.deleteOnExit();
        }
        /**
         * Creates a new File that will be deleted when the 
         * JVM exits normally.
         * @throws IOException 
         */
        @Override
        public File createNewFile(String filePath) throws IOException{
            File tempFile = super.createNewFile(filePath);
            tempFile.deleteOnExit();
            return tempFile;
        }
        @Override
        public synchronized void close() throws IOException {
            super.close();
            IOUtil.recursiveDelete(getRootDir());
        }
        
        
    }
    /**
     * {@code ReadOnlyDirectoryFileServer} is the implementation
     * of {@link DirectoryFileServer} that DOES NOT allow put operations.
     * @author dkatzel
     *
     */
    public static final class ReadOnlyDirectoryFileServer extends DirectoryFileServer{

        private ReadOnlyDirectoryFileServer(File rootDir) {
            super(rootDir);
            if(!rootDir.exists()){
                throw new IllegalArgumentException("root dir must exist");
            }
        }
        
    }
    
}
