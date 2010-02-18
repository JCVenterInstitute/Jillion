/*
 * Created on Aug 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.jcvi.util.FileIterator;
import org.jcvi.util.StringUtilities;
/**
 * {@code LegacyTigrLocalChromoTraceFileServer} is a {@link TraceFileServer}
 * implementation that can read a legacy TIGR "local chromo" directory.
 * @author dkatzel
 *
 *
 */
public class LegacyTigrLocalChromoTraceFileServer implements TraceFileServer, Iterable<String> {

    private static final List<String> SUPPORTED_SUFFIXES = Arrays.asList(".ztr",".gz");
    private final String baseDir;
    public LegacyTigrLocalChromoTraceFileServer(String localChromoPath,String tigrProject){
        baseDir = localChromoPath + "/"+tigrProject.toUpperCase() + "/ABISSed";
    }
    
    protected String generateLocalChromoPathFor(String seqName){
        
        return StringUtilities.join("/", 
                baseDir, 
                seqName.substring(0, 3),
                seqName.substring(0, 4),
                seqName.substring(0, 5),
                seqName);
       
    }
    private File getFileFor(String seqName){
        String expectedPath = generateLocalChromoPathFor(seqName);
        for(String extension : SUPPORTED_SUFFIXES){
            File f = new File(expectedPath + extension);
            if(f.exists()){
                return f;
            }
        }
        return null;
        
    }
    @Override
    public boolean contains(String seqName) throws IOException {
        return getFileFor(seqName) !=null;
    }

    @Override
    public File getFile(String seqName) throws IOException {
        File f= getFileFor(seqName);
        if(f==null){
            throw new IOException("trace file for "+ seqName + " does not exist");
        }
        return f;
    }

    @Override
    public InputStream getFileAsStream(String seqName) throws IOException {
        final File f = getFile(seqName);
        final FileInputStream fileInputStream = new FileInputStream(f);
        if(f.getName().endsWith(".gz")){            
            return new ZipInputStream(fileInputStream);
        }
        return fileInputStream;
    }

    @Override
    public boolean supportsGettingFileObjects() {
        return true;
    }

    @Override
    public void close() throws IOException {
        //no-op
    }

    @Override
    public Iterator<String> iterator() {
        return new SeqNameFileIterator(FileIterator.createDepthFirstFileIterator(new File(baseDir),false));
    }

    

    private String getSeqnameFor(File file) {
        String nameWithSuffix = file.getName();
        String nameWithoutSuffix = nameWithSuffix.replaceAll("\\..+?$", "");
        return nameWithoutSuffix;
    }

    private final class SeqNameFileIterator implements Iterator<String>{
        private final Iterator<File> fileIterator;
        private SeqNameFileIterator(Iterator<File> fileIterator){
            this.fileIterator = fileIterator;
        }
        @Override
        public boolean hasNext() {
            return fileIterator.hasNext();
        }

        @Override
        public String next() {
            File file = fileIterator.next();
            return getSeqnameFor(file);
        }

        @Override
        public void remove() {
            fileIterator.remove();
            
        }
        
    }
    
}
