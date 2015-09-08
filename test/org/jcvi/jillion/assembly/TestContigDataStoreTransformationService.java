/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collections;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;
public class TestContigDataStoreTransformationService {

	 
	
	@Test(expected = NullPointerException.class)
	public void nullDataStoreShouldThrowNPE(){
		new ContigDataStoreTransfomationService.Builder(null);
	}
	
	@Test
	public void oneContigOneRead(){
		String contigId = "contigId";
		NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		String readId = "readId";
		int offset=0;
		String bases = "ACG-ACGT";
		
		Contig<AssembledRead> contig = new DefaultContig.Builder(contigId, consensus)
											.addRead(readId, offset, bases)
											.build();
		
		ContigDataStoreTransfomationService sut = new ContigDataStoreTransfomationService.Builder(toDataStore(contig))
														.build();
		
		AssemblyTransformer transformer = createMock(AssemblyTransformer.class);
		
		transformer.referenceOrConsensus(contigId, consensus);
		transformer.aligned(readId, null, null, null, null, 
				contigId, offset, Direction.FORWARD, 
				new NucleotideSequenceBuilder(bases).build(),
				new ReadInfo(Range.ofLength(7), 7));
		
		transformer.endAssembly();
		
		replay(transformer);
		sut.transform(transformer);
		verify(transformer);
		
	}
	
	@SuppressWarnings("unchecked")
	private ContigDataStore<?,?> toDataStore(Contig<?> contig){
		return DataStoreUtil.adapt(ContigDataStore.class, Collections.singletonMap(contig.getId(), contig));
	}
	
	
}
