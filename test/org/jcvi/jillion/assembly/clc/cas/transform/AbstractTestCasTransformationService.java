/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.transform;


import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.clc.cas.CasContigPair;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasFileParser;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasMatchVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasReferenceDescription;
import org.jcvi.jillion.assembly.clc.cas.CasScoringScheme;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigRead;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public abstract class AbstractTestCasTransformationService {
	private final ResourceHelper RESOURCES = new ResourceHelper(AbstractTestCasTransformationService.class); 
	 private TigrContigDataStore expectedDataStore;
	 private final String pathToCas;
	 private final String pathToExpectedContig;;
	 
   
	    
	protected AbstractTestCasTransformationService(String pathToCas, String pathToContig) {
		this.pathToCas = pathToCas;
		this.pathToExpectedContig = pathToContig;
	}
	@Before
    public void setup() throws IOException{
	 NucleotideFastaDataStore fastas = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("../files/15050.fasta"))
		.build();
        
		expectedDataStore = new TigrContigFileDataStoreBuilder(RESOURCES.getFile(pathToExpectedContig), fastas)
        						.build();
   	    
	 }
	    
	    @Test
	    public void transformCas() throws IOException, DataStoreException{
	        File casFile = RESOURCES.getFile(pathToCas);
	        
	        CasFileTransformationService sut = new CasFileTransformationService(casFile);
	        AssemblyTransformer transformer = createMockTransformer(casFile);
	        replay(transformer);
	        sut.transform(transformer);
	        verify(transformer);
	        
	    }

		private AssemblyTransformer createMockTransformer(File casFile) throws DataStoreException, IOException {
			final AssemblyTransformer mock = createMock(AssemblyTransformer.class);
			
			CasFileVisitor casVisitor = new CasFileVisitor() {
				
				@Override
				public void visitScoringScheme(CasScoringScheme scheme) {
					//no-op					
				}
				
				@Override
				public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
					//no-op
				}
				
				@Override
				public void visitReferenceDescription(CasReferenceDescription description) {
					//no-op
				}
				
				@Override
				public void visitReadFileInfo(CasFileInfo readFileInfo) {
					//no-op
				}
				
				@Override
				public void visitNumberOfReferenceFiles(long numberOfReferenceFiles) {
					//no-op
				}
				
				@Override
				public void visitNumberOfReadFiles(long numberOfReadFiles) {
					//no-op
				}
				
				@Override
				public void visitMetaData(long numberOfReferenceSequences,
						long numberOfReads) {
					//no-op
					
				}
				
				@Override
				public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
					//don't visit matches
					return null;
				}
				
				@Override
				public void visitEnd() {
					//no-op
					
				}
				
				@Override
				public void visitContigPair(CasContigPair contigPair) {
					//no-op
					
				}
				
				@Override
				public void visitAssemblyProgramInfo(String name, String version,
						String parameters) {
					mock.assemblyCommand(name, version, parameters);
					
				}
				
				@Override
				public void halted() {
					//no-op
					
				}
			};
			CasFileParser.create(casFile).parse(casVisitor);
			StreamingIterator<TigrContig> contigIter = expectedDataStore.iterator();
			File readFastaFile = RESOURCES.getFile("../files/15050.fasta");
			
			URI refFastaUri = RESOURCES.getFile("../files/consensus.fasta").toURI();
			mock.referenceFile(refFastaUri);
			
			NucleotideFastaDataStore readDataStore = new NucleotideFastaFileDataStoreBuilder(readFastaFile)
														.build();
			URI readFastaFileUri = readFastaFile.toURI();
			
			mock.readFile(readFastaFileUri);
			//TODO not aligned for now
			mock.notAligned(isA(String.class), isA(NucleotideSequence.class), (QualitySequence)isNull(),(PositionSequence)isNull(), isA(URI.class));
			
			expectLastCall().anyTimes();
			
			while(contigIter.hasNext()){
				TigrContig contig = contigIter.next();
				StreamingIterator<TigrContigRead> readIter = contig.getReadIterator();
				
				mock.referenceOrConsensus(contig.getId(), contig.getConsensusSequence());
				while(readIter.hasNext()){
					AssembledRead read = readIter.next();
					mock.aligned(read.getId(), readDataStore.get(read.getId()).getSequence(), 
							null, null, 
							readFastaFileUri, contig.getId(), 
							
							read.getGappedStartOffset(), read.getDirection(), 
							read.getNucleotideSequence(), read.getReadInfo());
				}
			}
			mock.endAssembly();
			return mock;
		}
		
}
