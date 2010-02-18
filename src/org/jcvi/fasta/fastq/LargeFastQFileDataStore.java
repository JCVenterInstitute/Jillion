/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.AbstractLargeIdIterator;
/**
 * {@code LargeFastQFileDataStore} is a {@link FastQDataStore} implementation
 * to be used a very large FastQ Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances of 
 * {@link LargeFastQFileDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 *
 */
public class LargeFastQFileDataStore extends AbstractFastQFileDataStore<FastQRecord> {
    private static final Pattern BEGIN_SEQ_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    private final File fastQFile;
    private Integer size=null;
    /**
     * @param qualityCodec
     */
    public LargeFastQFileDataStore(File fastQFile, FastQQualityCodec qualityCodec) {
        super(qualityCodec);
        this.fastQFile = fastQFile;        
    }

    @Override
    protected boolean visitFastQRecord(String id,
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities, String optionalComment) {
        return true;
        
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return get(id)!=null;
    }

    @Override
    public FastQRecord get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        try {
            
            DefaultFastQFileDataStore dataStore = new SingleFastQDataStore(id,this.getQualityCodec());
            FastQFileParser.parse(fastQFile, dataStore);
            return dataStore.get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse fasta q file",e);
        }
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        try {
            return new FastQIdIterator();
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse fast q file",e);
        }
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        if(size !=null){
            return size;
        }
        int count=0;
        Scanner scanner;
        try {
            scanner = new Scanner(fastQFile);
        
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("@")){
                count++;
            }
        }
        size = count;
        return size;
        } catch (FileNotFoundException e) {
            size = 0;
            throw new DataStoreException("could not determine size",e);
        }
    }

    private class FastQIdIterator extends AbstractLargeIdIterator{

        private FastQIdIterator() throws FileNotFoundException{
                super(fastQFile);
        }
        
        
        @Override
        protected void advanceToNextId(Scanner scanner) {
          //skip basecalls line
            scanner.nextLine();
            //skip begin quality block
            scanner.nextLine();
            //skip qualities line
            scanner.nextLine();
            
        }


        @Override
        protected String getNextId(Scanner scanner) {
            Matcher matcher = BEGIN_SEQ_PATTERN.matcher(scanner.nextLine());
            matcher.find();
            return  matcher.group(1);
        }


        
        
    }
    
    private class SingleFastQDataStore extends DefaultFastQFileDataStore{

        private final String idToLookFor;
        private boolean found=false;
        
        public SingleFastQDataStore(String idToLookFor,FastQQualityCodec qualityCodec) {
            super(qualityCodec);
            this.idToLookFor = idToLookFor;
        }
        @Override
        public boolean visitBeginBlock(String id, String optionalComment) {
            if(!found && idToLookFor.equals(id)){
                found=true;
                super.visitBeginBlock(id, optionalComment);
            }
            return !found;
        }
        
    }
}
