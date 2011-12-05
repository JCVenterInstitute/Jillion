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

package org.jcvi.common.core.assembly.clc.cas.var;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultVariationLogFile implements VariationLog, VariationLogFileVisitor {

    private final Map<String, Map<Long, Variation>> variations = new TreeMap<String, Map<Long,Variation>>();
    private Map<Long, Variation> currentMap=null;
    public DefaultVariationLogFile(File variationLogFile) throws FileNotFoundException{
        VariationLogFileParser.parseVariationFile(variationLogFile, this);
    }
    @Override
    public Set<String> getContigIds(){
        return variations.keySet();
    }
    @Override
    public Map<Long, Variation> getVariationsFor(String contigId){
        return variations.get(contigId);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitContig(String id) {
        currentMap=new TreeMap<Long, Variation>();
        variations.put(id, currentMap);
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitVariation(Variation variation) {
        long coordinate = variation.getCoordinate();
        currentMap.put(coordinate, variation);
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }
    
    
    
}
