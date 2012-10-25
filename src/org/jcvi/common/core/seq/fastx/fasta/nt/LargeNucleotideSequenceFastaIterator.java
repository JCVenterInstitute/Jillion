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

package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
final class LargeNucleotideSequenceFastaIterator extends AbstractBlockingCloseableIterator<NucleotideSequenceFastaRecord>{

	private final File fastaFile;
	private final DataStoreFilter filter;
	
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(File fastaFile, DataStoreFilter filter){
		 LargeNucleotideSequenceFastaIterator iter = new LargeNucleotideSequenceFastaIterator(fastaFile, filter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeNucleotideSequenceFastaIterator(File fastaFile, DataStoreFilter filter){
		 this.fastaFile = fastaFile;
		 this.filter = filter;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	        FastaFileVisitor visitor = new AbstractFastaVisitor() {
				
				@Override
				protected boolean visitRecord(String id, String comment, String entireBody) {
					boolean accept;
					if(filter instanceof FastXFilter){
						accept=((FastXFilter)filter).accept(id, comment);
					}else{
						accept = filter.accept(id);
					}
					if(accept){
						NucleotideSequenceFastaRecord fastaRecord = new NucleotideSequenceFastaRecordBuilder(id, entireBody)
																		.comment(comment)
																		.build();
						blockingPut(fastaRecord);
					}
	                return !LargeNucleotideSequenceFastaIterator.this.isClosed();
				}
			};
	        try {
	            FastaFileParser.parse(fastaFile, visitor);
	        } catch (FileNotFoundException e) {
	            throw new RuntimeException("fasta file does not exist",e);
	        }
	        
	    }
}
