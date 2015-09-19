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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class TasmContigFileContigIterator extends AbstractBlockingStreamingIterator<TasmContig>{

	private final File contigFile;
	private final DataStoreFilter filter;
	private final DataStore<Long> fullLengthSequences;
	
	public static StreamingIterator<TasmContig> create(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter){
		TasmContigFileContigIterator iter = new TasmContigFileContigIterator(contigFile,fullLengthSequences,filter);
		iter.start();
		return iter;
	}
	
	private TasmContigFileContigIterator(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter) {
		this.contigFile = contigFile;
		this.filter = filter;
		this.fullLengthSequences = fullLengthSequences;
	}



	@Override
	protected void backgroundThreadRunMethod() throws RuntimeException {
		TasmVisitor visitor = new TasmVisitor() {
			
			@Override
			public void halted() {
				//no-op				
			}
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			
			@Override
			public TasmContigVisitor visitContig(TasmVisitorCallback callback,
					String contigId) {
				if(filter.accept(contigId)){
					return new AbstractTasmContigBuilderVisitor(contigId, fullLengthSequences) {
						
						@Override
						protected void visitRecord(TasmContigBuilder contig) {
							blockingPut(contig.build());
							
						}
					};
				}
				return null;
			}
		};
		
		try {
			TasmFileParser.create(contigFile).parse(visitor);
		} catch (IOException e) {
			throw new RuntimeException("error parsing contig file",e);
		}
	}

	
}
