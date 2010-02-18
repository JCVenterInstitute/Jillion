/*
 * Created on Aug 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
/**
 * {@code FileIterator} is an {@link Iterator} for File objects.
 * @author dkatzel
 *
 *
 */
public abstract class FileIterator implements Iterator<File>, Iterable<File>{
    
    /**
     * A FileFilter that only accepts files that are not 
     * directories.
     * @author dkatzel
     */
    private static final class NonDirectoryFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return !file.isDirectory();
        }
    }
    
    private static final NonDirectoryFileFilter  NON_DIRECTORY_FILTER = new NonDirectoryFileFilter();
    /**
     * Creates a new FileIterator that recursively iterates over 
     * Files in Depth First order.
     * @param rootDir the rootDirectory to begin iterating.
     * @param includeDirectories should files that are directories
     * be returned by {@link FileIterator#next()}
     * @return a new FileIterator.
     * @throws NullPointerException if rootDir is null.
     * @throws IllegalArgumentException if rootDir is not a directory.
     */
    public static FileIterator createDepthFirstFileIterator(File rootDir, boolean includeDirectories){
        return new DepthFirstFileIterator(rootDir,includeDirectories);
    }
    /**
     * Creates a new FileIterator that recursively iterates over 
     * Files in Breadth First order.
     * @param rootDir the rootDirectory to begin iterating.
     * @param includeDirectories should files that are directories
     * be returned by {@link FileIterator#next()}
     * @return a new FileIterator.
     * @throws NullPointerException if rootDir is null.
     * @throws IllegalArgumentException if rootDir is not a directory.
     */
    public static FileIterator createBreadthFirstFileIterator(File rootDir,boolean includeDirectories){
        return new BreadthFirstFileIterator(rootDir,includeDirectories);
    }
    /**
     * Creates a new FileIterator that ONLY iterates over the files in 
     * the given directory.  This iterator will not recurse.
     * @param dir the directory to begin iterating.
     * @param includeDirectories should files that are directories
     * be returned by {@link FileIterator#next()}
     * @return a new FileIterator.
     * @throws NullPointerException if dir is null.
     * @throws IllegalArgumentException if dir is not a directory.
     */
    public static FileIterator createFileIterator(File dir,boolean includeDirectories){
        return new NonRecursiveFilterIterator(dir,includeDirectories);
    }
    
    /**
     * Convenience method, same as calling 
     * <code> {@link #createFileIterator(File, boolean) FileIterator.createFileIterator(dir, false)}</code>.
     * @param dir the directory to begin iterating.
     * @return a new FileIterator.
     * @throws NullPointerException if dir is null.
     * @throws IllegalArgumentException if dir is not a directory.
     */
    public static FileIterator createFileIterator(File dir){
        return new NonRecursiveFilterIterator(dir,false);
    }
    
    private Iterator<File> fileIterator;
    private final boolean includeDirectories;
    private File nextFile;
    private final File rootDir;
    
    
    protected FileIterator(File rootDir,boolean includeDirectories){
        if(rootDir ==null){
            throw new NullPointerException("rootDir can not be null");
        }
        if(!rootDir.isDirectory()){
            throw new IllegalArgumentException("rootDir must be a directory");
        }
        this.includeDirectories = includeDirectories;
        this.rootDir = rootDir;
        setUpInitialState(rootDir);
        
    }
    protected void setUpInitialState(File rootDir){
        updateFileIterator(rootDir);
        nextFile = getNextFile();
    }
    protected void updateFileIterator(File rootDir) {
        fileIterator = getFilesFor(rootDir);
    }
    
    private Iterator<File> getFilesFor(File dir){
        final File[] listFiles;
        if(includeDirectories){
            listFiles = dir.listFiles();
        }
        else{
            listFiles = dir.listFiles(NON_DIRECTORY_FILTER);
        }
        if(listFiles ==null){
            //either no files or no files we have permission to see
            return EmptyIterator.createEmptyIterator();
        }
        //sort files
        Arrays.sort(listFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
            
        });
        return new ArrayIterable<File>(listFiles).iterator();
    }
    /**
     * Returns a new iterator with the same
     * parameters as this.
     */
    @Override
    public Iterator<File> iterator() {
        return createNewInstance(this.rootDir, this.includeDirectories);
    }
    
    protected abstract Iterator<File> createNewInstance(File root, boolean includeSubdirs);
    protected File getNextFile(){
        if(fileIterator.hasNext()){
            return fileIterator.next();
        }        
        return null;        
    }

   
    @Override
    public boolean hasNext() {
        return nextFile !=null;
    }

    @Override
    public File next() {
        if(!hasNext()){
            throw new NoSuchElementException("no more files");
        }
        File fileToReturn = nextFile;
        nextFile = getNextFile();
        return fileToReturn;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("can not remove");
    }
    private static class NonRecursiveFilterIterator extends FileIterator{

        protected NonRecursiveFilterIterator(File rootDir,
                boolean includeDirectories) {
            super(rootDir, includeDirectories);
        }

        @Override
        protected Iterator<File> createNewInstance(File root,
                boolean includeSubdirs) {
            return new NonRecursiveFilterIterator(root, includeSubdirs);
        }
        
    }
    /**
     * RecursiveFileIterator adds recursive abilities to FileIterator.
     * @author dkatzel
     *
     *
     */
    private static abstract class RecursiveFileIterator extends FileIterator{
        /**
         * A FileFilter that only accepts files that ARE 
         * directories.
         * @author dkatzel
         */
        private static final class DirectoryFileFilter implements FileFilter {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        }
        
        private static final DirectoryFileFilter  DIRECTORY_FILTER = new DirectoryFileFilter();
       
        private Queue<File> dirIterator;
        protected RecursiveFileIterator(File rootDir,boolean includeDirectories) {
            super(rootDir,includeDirectories);
        }
        
        /**
         * Create an empty Queue of Files
         * that will be used to track the directories
         * to recursively iterate over.  The implementation
         * of the Queue returned will determine the order
         * the directories will be iterated.
         * @return a new empty Queue.
         */
        protected abstract Queue<File> createDirectoryIterator();
        
        private List<File> getSubdirectoriesFor(File dir){            
            final File[] listFiles = dir.listFiles(DIRECTORY_FILTER);
            if(listFiles ==null){
                //either no files or no files we have permission to see
                return Collections.emptyList();
            }
            return Arrays.asList(listFiles);
        }
        @Override
        protected void setUpInitialState(File rootDir) {
            dirIterator = createDirectoryIterator();
            dirIterator.addAll(getSubdirectoriesFor(rootDir));
            super.setUpInitialState(rootDir);
        }
        @Override
        protected File getNextFile() {
            File nextFile = super.getNextFile();
            if(nextFile ==null){
                if(hasMoreSubDirs()){            
                    return getNextFromSubDir();
                }
            }
            
            return nextFile;
        }
        private boolean hasMoreSubDirs() {
            return !dirIterator.isEmpty();
        }

        private File getNextFromSubDir() {
            File newDir = dirIterator.poll();
            updateFileIterator(newDir);
            dirIterator.addAll(getSubdirectoriesFor(newDir));
            return getNextFile();
        }
        
    }
    private static class DepthFirstFileIterator extends RecursiveFileIterator{

        public DepthFirstFileIterator(File rootDir,boolean includeDirectories) {
            super(rootDir,includeDirectories);           
        }

        
        @Override
        protected Queue<File> createDirectoryIterator() {
            return new LIFOQueue<File>();
        }


        @Override
        protected Iterator<File> createNewInstance(File root,
                boolean includeSubdirs) {
            return new DepthFirstFileIterator(root, includeSubdirs);
        } 
    }
    private static class BreadthFirstFileIterator extends RecursiveFileIterator{

        public BreadthFirstFileIterator(File rootDir,boolean includeDirectories) {
            super(rootDir,includeDirectories);           
        }

        

        @Override
        protected Queue<File> createDirectoryIterator() {
            return new FIFOQueue<File>();
        }  
        @Override
        protected Iterator<File> createNewInstance(File root,
                boolean includeSubdirs) {
            return new BreadthFirstFileIterator(root, includeSubdirs);
        } 
    }
    
}
