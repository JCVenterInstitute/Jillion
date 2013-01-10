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
package org.jcvi.common.core.assembly.scaffold.agp;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.assembly.DefaultScaffoldDataStore;
import org.jcvi.common.core.assembly.Scaffold;
import org.jcvi.common.core.assembly.ScaffoldDataStore;
import org.jcvi.common.core.assembly.ScaffoldDataStoreBuilder;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;



public final class DefaultAgpScaffoldDataStore {
    
	private DefaultAgpScaffoldDataStore(){
		//private constructor.
	}
	public static ScaffoldDataStoreBuilderAgpVisitor createBuilder(){
		return new Builder();
	}
	
	public static ScaffoldDataStore create(File agpFile) throws FileNotFoundException{
		ScaffoldDataStoreBuilderAgpVisitor builder = createBuilder();
		AgpParser.parseAgpFile(agpFile, builder);
		return builder.build();
	}
	
	private static final class Builder implements ScaffoldDataStoreBuilderAgpVisitor{
		ScaffoldDataStoreBuilder builder = DefaultScaffoldDataStore.createBuilder();
	
	    public void visitContigEntry(String scaffoldId, Range contigRange, String contigId, Direction dir) {
	       builder.addPlacedContig(scaffoldId, contigId, DirectedRange.create(contigRange, dir));
	    }
	
	    public void visitLine(String line) {
	        // do nothing for this visit method
	    }
	
	    public void visitEndOfFile() {
	    	//no-op
	    }
	
	    @Override
	    public void visitFile() {
	    	//no-op
	        
	    }
	
		@Override
		public ScaffoldDataStoreBuilder addScaffold(Scaffold scaffold) {
			builder.addScaffold(scaffold);
			return this;
		}
	
		@Override
		public ScaffoldDataStoreBuilder addPlacedContig(String scaffoldId,
				String contigId, DirectedRange directedRange) {
			builder.addPlacedContig(scaffoldId, contigId, directedRange);
			return this;
		}
	
		@Override
		public ScaffoldDataStore build() {
			return builder.build();
		}
	}
}
