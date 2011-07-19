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

package org.jcvi.common.core.assembly.contig.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@code DifferentFileCasIdLookupAdapter} is a {@link CasIdLookup}
 * implementation which overrides the File returned for specific
 * lookup ids (Strings).  This allows substituting a File (for example, an
 * untrimmed version).
 * @author dkatzel
 *
 *
 */
public class DifferentFileCasIdLookupAdapter implements CasIdLookup{
    private final CasIdLookup delegate;
    private final Map<String, File> differentFileLookupMap;
    /**
     * @param delegate
     * @param differentFileLookupMap
     */
    public DifferentFileCasIdLookupAdapter(CasIdLookup delegate,
            Map<String, File> differentFileLookupMap) {
        this.delegate = delegate;
        this.differentFileLookupMap = differentFileLookupMap;
    }
    @Override
    public void close() throws IOException {
        delegate.close();
        differentFileLookupMap.clear();
        
    }
    @Override
    public File getFileFor(long casReadId) {
        return getFileFor(delegate.getLookupIdFor(casReadId));
    }
    @Override
    public File getFileFor(String lookupId) {
        if(differentFileLookupMap.containsKey(lookupId)){
            return differentFileLookupMap.get(lookupId);
        }
        return delegate.getFileFor(lookupId);
    }
  
    @Override
    public List<File> getFiles() {
        Set<File> files = new HashSet<File>();
        files.addAll(differentFileLookupMap.values());
        final int numberOfIds = getNumberOfIds();
        for(int i=0; i<numberOfIds; i++){
            String id = getLookupIdFor(i);
            if(!differentFileLookupMap.containsKey(id)){
                files.add(delegate.getFileFor(id));
            }
        }     
        List<File> ret = new ArrayList<File>();
        for(File f : files){
            ret.add(f);
        }
        return ret;
    }
    @Override
    public String getLookupIdFor(long casReadId) {
        return delegate.getLookupIdFor(casReadId);
    }
    @Override
    public int getNumberOfIds() {
        return delegate.getNumberOfIds();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getCasIdFor(String lookupId) {
        return delegate.getCasIdFor(lookupId);
    }
    
    
}
