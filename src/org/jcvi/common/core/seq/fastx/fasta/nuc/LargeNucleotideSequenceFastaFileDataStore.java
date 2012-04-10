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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 */
public final class LargeNucleotideSequenceFastaFileDataStore extends AbstractNucleotideFastaFileDataStore{
	/**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideSequenceFastaDataStore create(File fastaFile){
		return new LargeNucleotideSequenceFastaFileDataStore(fastaFile, DefaultNucleotideSequenceFastaRecordFactory.getInstance());
	}
	
	private static final Pattern NEXT_ID_PATTERN = Pattern.compile("^>(\\S+)");
    private final File fastaFile;

    private Integer size;
    
   
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file and the given {@link NucleotideSequenceFastaRecordFactory}.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
    private LargeNucleotideSequenceFastaFileDataStore(File fastaFile,
            NucleotideSequenceFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    
    
    @Override
	public EndOfBodyReturnCode visitEndOfBody() {
		return EndOfBodyReturnCode.KEEP_PARSING;
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
    public synchronized NucleotideSequenceFastaRecord get(String id)
            throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        NucleotideSequenceFastaDataStore datastore=null;
        try {
            in = getRecordFor(id);
        
            if(in ==null){
                return null;
            }
      
            NucleotideFastaDataStoreBuilderVisitor builder= DefaultNucleotideSequenceFastaFileDataStore.createBuilder();
            FastaParser.parseFasta(in, builder);
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
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        checkNotYetClosed();
        return LargeFastaIdIterator.createNewIteratorFor(fastaFile);
        
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                Scanner scanner = new Scanner(fastaFile, IOUtil.UTF_8_NAME);
                int counter =0;
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
    public synchronized CloseableIterator<NucleotideSequenceFastaRecord> iterator() {
        checkNotYetClosed();
        LargeNucleotideSequenceFastaIterator iter = new LargeNucleotideSequenceFastaIterator(fastaFile);
        iter.start();
        return iter;
       
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
            StringBuilder record = new StringBuilder(line).append("\n");
            line =scanner.nextLine();
            while(!line.startsWith(">") && scanner.hasNextLine()){
                record.append(line).append("\n");
                line = scanner.nextLine();
            }
            //add final line if needed
            if(!scanner.hasNextLine()){
                record.append(line).append("\n");
            }
            return new ByteArrayInputStream(record.toString().getBytes(IOUtil.UTF_8));
        }finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }


   
}
