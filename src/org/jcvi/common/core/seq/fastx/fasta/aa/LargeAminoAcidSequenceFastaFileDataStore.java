package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code LargeAminoAcidSequenceFastaFileDataStore} is an implementation
 * of {@link AminoAcidSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 */
public final class LargeAminoAcidSequenceFastaFileDataStore implements AminoAcidSequenceFastaDataStore{
	
	
	private static final Pattern NEXT_ID_PATTERN = Pattern.compile("^>(\\S+)");
    private final File fastaFile;

    private Long size;
    private volatile boolean closed=false;
    /**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static AminoAcidSequenceFastaDataStore create(File fastaFile){
		return new LargeAminoAcidSequenceFastaFileDataStore(fastaFile);
	}
   
    /**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
    private LargeAminoAcidSequenceFastaFileDataStore(File fastaFile) {
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    
    private void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    @Override
    public  void close() throws IOException {
        closed=true;
        
    }

    @Override
    public  boolean isClosed() {
        return closed;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        try {
            in= getRecordFor(id);
            return in !=null;
        } catch (FileNotFoundException e) {
           throw new DataStoreException("could not get record for "+id,e);
        }finally{
           IOUtil.closeAndIgnoreErrors(in);
        }
    }

    @Override
    public AminoAcidSequenceFastaRecord get(String id)
            throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        AminoAcidSequenceFastaDataStore datastore=null;
        try {
            in = getRecordFor(id);
        
            if(in ==null){
                return null;
            }
      
            AminoAcidSequenceFastaDataStoreBuilderVisitor builder= DefaultAminoAcidSequenceFastaDataStore.createBuilder();
            FastaFileParser.parse(in, builder);
            datastore = builder.build();
            return datastore.get(id);
           
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get record for "+id, e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in,datastore);
        }
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,LargeFastaIdIterator.createNewIteratorFor(fastaFile));
        
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                Scanner scanner = new Scanner(fastaFile, IOUtil.UTF_8_NAME);
                long counter =0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    Matcher matcher = NEXT_ID_PATTERN.matcher(line);
                    if(matcher.find()){
                        counter++;
                    }
                }
                size= counter;            
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("could not get record count");
            }
        }   
        return size;

    }

    @Override
    public StreamingIterator<AminoAcidSequenceFastaRecord> iterator() {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile));
       
    }
    /**
     * Get the part of the large fasta file we care about.
     * @param id the id of the fasta record we want.
     * @return an {@link InputStream} containing <strong>only</strong>
     * the fasta record we care about; or null if no such record exists.
     * @throws FileNotFoundException if the fasta file is no longer
     * available to read.
     */
    private InputStream getRecordFor(String id) throws FileNotFoundException{
        Scanner scanner = null;
        try{
            scanner = new Scanner(fastaFile, IOUtil.UTF_8_NAME);
            String expectedHeader = String.format(">%s", id);
            String line = scanner.nextLine();
            boolean done=false;
            //we have to do this while loop to make sure we find
            //the actual read instead of a different read which is happens
            //to include our id as a prefix (for example a TIGR "B" read)
            while(!done){
                if(line.startsWith(expectedHeader)){
                    String currentId= FastaUtil.parseIdFromDefLine(line);
                    if(id.equals(currentId)){
                        done=true;
                        //done
                        continue;
                    }
                }
                if(!scanner.hasNextLine()){
                    done=true;
                    continue;
                }
                line = scanner.nextLine(); 
            }
            
            if(!scanner.hasNextLine()){
                return null;
            }
            StringBuilder record = new StringBuilder(line).append('\n');
            line =scanner.nextLine();
            while(!line.startsWith(">") && scanner.hasNextLine()){
                record.append(line).append('\n');
                line = scanner.nextLine();
            }
            //add final line if needed
            if(!scanner.hasNextLine()){
                record.append(line).append('\n');
            }
            return new ByteArrayInputStream(record.toString().getBytes(IOUtil.UTF_8));
        }finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }


   
}
