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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public class PhdDirDataStore implements PhdDataStore{

	private static final Pattern PHD_FILE_PATTERN = Pattern.compile("^(\\S+)\\.phd.\\d+$");
    private final File phdDir;
    private final PhdDataStore phdBallDataStore;
    private volatile boolean closed =false;
    
    /**
     * @param phdDir
     * @throws FileNotFoundException 
     */
    public PhdDirDataStore(File phdDir) throws IOException {
        this.phdDir = phdDir;
        File phdBall = getPhdFileFor("phd.ball");
        if(phdBall ==null){
            phdBallDataStore = null;
        }else{
            phdBallDataStore = new PhdFileDataStoreBuilder(phdBall)
									.build();
        }
    }

    private File getPhdFileFor(String readId){
        int latestVersion= Integer.MIN_VALUE;
        File latestFile=null;
        for(File phd : phdDir.listFiles(new ReadPhdFileFilter(readId))){
            int version = Integer.parseInt(FileUtil.getExtension(phd.getName()));
            if(version>latestVersion){
                latestVersion = version;
                latestFile=phd;
            }
        }
        return latestFile;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
    	 
    	 return new StreamingIterator<String>() {
    		 final StreamingIterator<Phd> phdIter = iterator();
			@Override
			public boolean hasNext() {
				return phdIter.hasNext();
			}

			@Override
			public void close() {
				phdIter.close();
				
			}

			@Override
			public String next() {
				return phdIter.next().getId();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
				
			}
    		 
		};
    }

	private Set<String> getPhdDirUniqueNames() {
		Set<String> ids = new HashSet<String>();
        for(String filename : phdDir.list()){
        	Matcher matcher = PHD_FILE_PATTERN.matcher(filename);
        	if(matcher.matches()){
        		ids.add(matcher.group(1));
        	}
        }
        return ids;
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Phd get(String id) throws DataStoreException {
        return getPhdFor(id);
    }

    private Phd getPhdFor(String id) throws DataStoreException{
        File phdFile = getPhdFileFor(id);
        if(phdFile ==null){
            if(phdBallDataStore==null){
                throw new IllegalArgumentException("could not find any phd files for "+ id);
                
            }
            return phdBallDataStore.get(id);
        }
        return new PhdFileDataStoreBuilder(phdFile).build().get(id);
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        
        boolean hasPhd= getPhdFileFor(id)!=null;
        if(!hasPhd && phdBallDataStore !=null){
            return phdBallDataStore.contains(id);
        }
        return hasPhd;
    }

    private Set<String> getUniquePhdDirNamesNotInPhdBall() throws DataStoreException{
    	//this is complicated because 
    	//there could be multiple versions of the phd
    	//in both the individual phd location
    	//and also an additional copy in the phdball
    	//individual files trump phdball 
    	//(this is implied because and edited phd
    	//will get its own individual phd file
    	//and the phdball will not be updated).
    	if(phdBallDataStore==null){
    		//no phdball so 
    		//the total is the number of unique names
    		//in the phddir
    		return getPhdDirUniqueNames();
   	 	}
    	//we are here if we have a phdball
    	//need to combine phds and phdball records
    	//assume individual phds are fewer than the phdball
    	Set<String> individualIds = getPhdDirUniqueNames();
    	//use iterator so we can call remove()
    	Iterator<String> phdDirIter = individualIds.iterator();
    	while(phdDirIter.hasNext()){
    		String id = phdDirIter.next();
    		if(phdBallDataStore.contains(id)){
    			//this read is also in phdball
    			//don't double count it
    			phdDirIter.remove();
    		}
    	}
    	//calling remove() on iter
    	//has modified the set
    	//so there should only be unique names left
    	//that aren't in the phdball
    	return individualIds;
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
    	//this is complicated because 
    	//there could be multiple versions of the phd
    	//in both the individual phd location
    	//and also an additional copy in the phdball.
    	//individual files trump phdball 
    	//(this is because edited phd
    	//will get its own individual phd file
    	//and the phdball will not be updated).
    	if(phdBallDataStore==null){
    		//no phdball so 
    		//the total is the number of unique names
    		//in the phddir
    		return getPhdDirUniqueNames().size();
   	 	}
    	
		return		 phdBallDataStore.getNumberOfRecords() 
				+ getUniquePhdDirNamesNotInPhdBall().size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close(){
        closed =true;
        if(phdBallDataStore !=null){
        	IOUtil.closeAndIgnoreErrors(phdBallDataStore);
        }        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<Phd> iterator() {
        
        try {
			return new PhdFromIdIterator();
		} catch (DataStoreException e) {
			throw new IllegalStateException("error getting phd iterator", e);
		}
    }
    
    
    
    @Override
	public StreamingIterator<DataStoreEntry<Phd>> entryIterator()
			throws DataStoreException {
		return new StreamingIterator<DataStoreEntry<Phd>>(){
			StreamingIterator<Phd> iter = iterator();
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public void close() {
				iter.close();
			}

			@Override
			public DataStoreEntry<Phd> next() {
				Phd next = iter.next();
				return new DataStoreEntry<Phd>(next.getId(), next);
			}

			@Override
			public void remove() {
				iter.remove();
			}
			
		};
	}

	private PhdDirDataStore getOuter(){
    	return this;
    }
    
    private class PhdFromIdIterator implements StreamingIterator<Phd>{

    	private final Set<String> individualPhds;
    	private final StreamingIterator<String> individualPhdIter;
    	private final StreamingIterator<Phd> phdBallIter;
    	
    	private Phd next = null;
    	
    	public PhdFromIdIterator() throws DataStoreException{
    		individualPhds = getPhdDirUniqueNames();
    		individualPhdIter = DataStoreStreamingIterator.create(getOuter(), individualPhds.iterator());
    	
    		if(phdBallDataStore ==null){
    			phdBallIter =null;
    		}else{
    			phdBallIter = phdBallDataStore.iterator();
    		}
    		updateNext();
    	}
    	
    	private void updateNext() throws DataStoreException{
    		if(individualPhdIter.hasNext()){
    			next = getPhdFor(individualPhdIter.next());
    			return;
    		}
			if(phdBallDataStore ==null){
				next=null;
			}else{
				boolean done=false;
				while(!done && phdBallIter.hasNext()){
					Phd nextPhdBallRecord = phdBallIter.next();
					if(!individualPhds.contains(nextPhdBallRecord.getId())){
						//this record was NOT superceded by
						//an individual phd
						//so we can use this one!
						next= nextPhdBallRecord;
						return;
					}
				}
				//if we are here we didn't find any next
				//not-yet used phds
				next=null;
    		}
    	}
    	
		@Override
		public boolean hasNext() {
			return next !=null;
		}

		@Override
		public void close() {
			IOUtil.closeAndIgnoreErrors(phdBallIter, individualPhdIter);
			
		}

		@Override
		public Phd next() {
			Phd ret = next;
			try {
				updateNext();
			} catch (DataStoreException e) {
				throw new IllegalStateException("could not get next record", e);
			}
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
    	
    }

}
