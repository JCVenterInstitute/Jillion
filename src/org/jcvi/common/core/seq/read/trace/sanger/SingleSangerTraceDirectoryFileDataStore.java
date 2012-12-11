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

package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.datastore.impl.AbstractDataStore;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.FileIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.DirectoryFileServer;

/**
 * {@code SingleSangerTraceDirectoryFileDataStore} is a {@link SangerFileDataStore}
 * implementation that contains {@link FileSangerTrace} data for all
 * single trace files (.scf, .ztr, phd files that are not phd.balls etc)
 * in the given directory.
 * @author dkatzel
 *
 *
 */
public class SingleSangerTraceDirectoryFileDataStore extends AbstractDataStore<FileSangerTrace> implements SangerFileDataStore<FileSangerTrace>{

    private final DirectoryFileServer fileServer;
    private final String extension;
    private Long numRecords =null;
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer,
            String extension) {
        if(fileServer ==null){
            throw new NullPointerException("fileServer can not be null");
        }
        this.extension=extension;
        this.fileServer = fileServer;
        
    }
    /**
     * @param dir
     * @param traceCodec
     */
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer) {
       this(fileServer,  null);
    }
    
    private String addExtensionIfNeeded(String id){
        if(extension!=null){
            return id + extension;
        }
        return id;
    }
    
    
    @Override
	protected boolean containsImpl(String id) throws DataStoreException {
    	try {
            return this.fileServer.contains(addExtensionIfNeeded(id));
        } catch (IOException e) {
            throw new DataStoreException("error when checking if Datastore contains" +id ,e);
        }
	}
	@Override
	protected FileSangerTrace getImpl(String id) throws DataStoreException {
		try{
            File file = fileServer.getFile(addExtensionIfNeeded(id));
            SangerTrace traceData= SangerTraceParser.INSTANCE.decode(file);
            return new DefaultFileSangerTrace(traceData,file);
        } catch (IOException e) {
           throw new DataStoreException("could not get trace for "+id, e);
        }
	}
	@Override
	protected synchronized long getNumberOfRecordsImpl() throws DataStoreException {
		if(numRecords==null){
			long counter=0L;
			StreamingIterator<FileSangerTrace> iter = iteratorImpl();
			try{
				while(iter.hasNext()){
					counter++;
					iter.next();
				}
				numRecords = Long.valueOf(counter);
			}catch(Exception e){
				throw new DataStoreException("error counting number of records",e);
			}finally{
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
		
		return numRecords.longValue();
	}
	@Override
	protected StreamingIterator<String> idIteratorImpl()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this, new IdIteratorImpl());
	}
	@Override
	protected StreamingIterator<FileSangerTrace> iteratorImpl()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this, new IteratorImpl());
	}
	

    
    
	@Override
	protected void handleClose() throws IOException {
		//no-op
		
	}
      
	private final class IdIteratorImpl implements Iterator<String>{
		private final Iterator<File> iter;
		IdIteratorImpl(){
            iter = FileIterator.createNonRecursiveFileIteratorBuilder(
                    fileServer.getRootDir())
                    .build();
		}
          @Override
          public boolean hasNext() {
              return iter.hasNext();
          }

          @Override
          public String next() {
              String actualFilename = iter.next().getName();
              if(extension!=null){
                  return actualFilename.substring(0, actualFilename.length()-extension.length());
              }
              return actualFilename;
          }

          @Override
          public void remove() {
              throw new UnsupportedOperationException();
              
          }
        
	}
	
	private final class IteratorImpl implements Iterator<FileSangerTrace>{
		private final Iterator<File> iter;
		IteratorImpl(){
            iter = FileIterator.createNonRecursiveFileIteratorBuilder(
                    fileServer.getRootDir())
                    .build();
		}
          @Override
          public boolean hasNext() {
              return iter.hasNext();
          }

          @Override
          public FileSangerTrace next() {
              File file = iter.next();			
			try {
				 SangerTrace traceData = SangerTraceParser.INSTANCE.decode(file);
				 return new DefaultFileSangerTrace(traceData,file);
			} catch (IOException e) {
				throw new IllegalStateException("error parsing sanger trace file : "+ file.getAbsolutePath(), e);
			}
             
          }

          @Override
          public void remove() {
              throw new UnsupportedOperationException();
              
          }
        
	}
      
}
