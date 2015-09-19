/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.assembly.agp;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.jillion.assembly.DefaultScaffoldDataStore;
import org.jcvi.jillion.assembly.Scaffold;
import org.jcvi.jillion.assembly.ScaffoldDataStore;
import org.jcvi.jillion.assembly.ScaffoldDataStoreBuilder;
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
