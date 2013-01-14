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
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code DefaultPhdFileDataStore} is a {@link PhdDataStore}
 * implementation that will store all {@link Phd} records
 * in memory for fast lookup but will take up lots of memory
 * (90+ % of the phdBall file size).
 * @author dkatzel
 *
 *
 */
public final class DefaultPhdFileDataStore{
	
	private DefaultPhdFileDataStore(){
		//can not instantiate
	}
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file.
     * @param phdBall the {@literal phd.ball} to parse.
     * @return a new DefaultPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall is null.
     */
    public static PhdDataStore create(File phdBall) throws FileNotFoundException{
        if(phdBall ==null){
            throw new NullPointerException("phdball can not be null");
        }
        PhdDataStoreBuilder builder =  createBuilder();
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file but only store the {@link Phd}
     * records of those reads that match the given {@link DataStoreFilter}.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param filter the {@link DataStoreFilter} to use to 
     * filter which reads get into the datastore.
     * @return a new DefaultPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if the given phdBall OR the given filter
     * is null.
     */
    public static PhdDataStore create(File phdBall,DataStoreFilter filter) throws FileNotFoundException{
        PhdDataStoreBuilder builder =createBuilder(filter);
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    /**
     * Create a new {@link PhdDataStoreBuilder} that can visit multiple
     * phd files.
     * @return a new PhdDataStoreBuilder; never null.
     */
    public static PhdDataStoreBuilder createBuilder(){
        return new DefaultPhdDataStoreBuilder();
    }
    /**
     * Create a new {@link PhdDataStoreBuilder} that can visit multiple
     * phd files.
     * @param filter the {@link DataStoreFilter} to use to 
     * filter which reads get into the datastore.
     * @return a new PhdDataStoreBuilder; never null.
     * @throws NullPointerException if the given filter
     * is null.
     */
    public static PhdDataStoreBuilder createBuilder(DataStoreFilter filter){
        return new DefaultPhdDataStoreBuilder(filter);
    }
    
    /**
     * Private implementation that will store all the phds
     * in a Map.
     * @author dkatzel
     *
     *
     */
    private static final class DefaultPhdDataStoreBuilder extends AbstractPhdDataStoreBuilder{
        //linked map to preserve iteration order
        private final Map<String, Phd> map = new LinkedHashMap<String, Phd>();
      
        
        private DefaultPhdDataStoreBuilder() {
            super();
        }

        private DefaultPhdDataStoreBuilder(DataStoreFilter filter) {
            super(filter);
        }

        @Override
        public PhdDataStore build() {
            return DataStoreUtil.adapt(PhdDataStore.class,map);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected boolean visitPhd(String id, NucleotideSequence bases,
                QualitySequence qualities, PositionSequence positions,
                Properties comments, List<PhdTag> tags) {
        	
            map.put(id, new DefaultPhd(id,
                   bases,
                    qualities,
                    positions,
                    comments,
                    tags));
            
            return true;
            
        }
        
    }

}
